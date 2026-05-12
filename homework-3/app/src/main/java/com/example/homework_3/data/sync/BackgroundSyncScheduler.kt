package com.example.homework_3.data.sync

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.homework_3.data.settings.BackgroundRefreshInterval
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackgroundSyncScheduler @Inject constructor(
    private val workManager: WorkManager,
) {
    fun update(
        enabled: Boolean,
        wifiOnly: Boolean,
        interval: BackgroundRefreshInterval,
    ) {
        if (!enabled) {
            workManager.cancelUniqueWork(WorkNames.SYNC_USER_CONTENT)
            enqueueCleanup()
            return
        }

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(if (wifiOnly) NetworkType.UNMETERED else NetworkType.CONNECTED)
            .build()

        val intervalHours = interval.duration.inWholeHours.coerceAtLeast(6)

        val request =
            PeriodicWorkRequestBuilder<SyncUserContentWorker>(intervalHours, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

        workManager.enqueueUniquePeriodicWork(
            WorkNames.SYNC_USER_CONTENT,
            ExistingPeriodicWorkPolicy.UPDATE,
            request,
        )

        enqueueCleanup()
    }

    private fun enqueueCleanup() {
        val cleanupRequest =
            PeriodicWorkRequestBuilder<CleanupWorker>(24, TimeUnit.HOURS)
                .build()

        workManager.enqueueUniquePeriodicWork(
            WorkNames.CLEANUP_LOCAL_DATA,
            ExistingPeriodicWorkPolicy.UPDATE,
            cleanupRequest,
        )
    }
}

