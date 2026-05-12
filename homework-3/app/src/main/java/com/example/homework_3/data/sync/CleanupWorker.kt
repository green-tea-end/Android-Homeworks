package com.example.homework_3.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.homework_3.data.local.RecentViewDao
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
        val thirtyDaysMs = 30L * 24L * 60L * 60L * 1000L

        recentViewDao.deleteOlderThan(now - thirtyDaysMs)
        recentViewDao.trimToLimit(200)

        return Result.success()
    }
}

