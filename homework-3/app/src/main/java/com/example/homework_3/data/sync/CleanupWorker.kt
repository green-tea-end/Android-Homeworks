package com.example.homework_3.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.homework_3.data.local.RecentViewDao
import com.example.homework_3.data.recent.RecentRetentionPolicy
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CleanupWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val recentViewDao: RecentViewDao,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val now = System.currentTimeMillis()
        recentViewDao.deleteOlderThan(now - RecentRetentionPolicy.maxAgeMs)
        recentViewDao.trimToLimit(RecentRetentionPolicy.MAX_ENTRIES)
        return Result.success()
    }
}
