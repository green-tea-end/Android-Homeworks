package com.example.homework_3

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.homework_3.data.settings.SettingsRepository
import com.example.homework_3.data.sync.BackgroundSyncScheduler
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class HomeworkApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var settingsRepository: SettingsRepository
    @Inject lateinit var backgroundSyncScheduler: BackgroundSyncScheduler

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        appScope.launch {
            combine(
                settingsRepository.isBackgroundRefreshEnabled,
                settingsRepository.isBackgroundRefreshWifiOnly,
                settingsRepository.backgroundRefreshInterval,
            ) { enabled, wifiOnly, interval ->
                Triple(enabled, wifiOnly, interval)
            }
                .distinctUntilChanged()
                .collect { (enabled, wifiOnly, interval) ->
                    backgroundSyncScheduler.update(
                        enabled = enabled,
                        wifiOnly = wifiOnly,
                        interval = interval,
                    )
                }
        }
    }
}