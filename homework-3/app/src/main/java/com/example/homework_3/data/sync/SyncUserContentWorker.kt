package com.example.homework_3.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.homework_3.data.settings.SettingsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class SyncUserContentWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val userContentSyncRunner: UserContentSyncRunner,
    private val settingsRepository: SettingsRepository,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val enabled = settingsRepository.isBackgroundRefreshEnabled.first()
        if (!enabled) return Result.success()

        val syncResult = userContentSyncRunner.syncUserContent()
        if (syncResult.shouldRetry) return Result.retry()

        if (syncResult.updatedIds.isNotEmpty() || syncResult.skippedNotFoundIds.isNotEmpty()) {
            settingsRepository.setLastBackgroundRefreshSuccessAt(System.currentTimeMillis())
        }

        return Result.success()
    }
}
