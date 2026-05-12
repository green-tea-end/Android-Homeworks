package com.example.homework_3.data.sync

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.Operation
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.homework_3.data.settings.BackgroundRefreshInterval
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

class BackgroundSyncSchedulerTest {

    @Test
    fun `disabled - cancels sync and still enqueues cleanup`() {
        val workManager = mockk<WorkManager>(relaxed = true)
        val scheduler = BackgroundSyncScheduler(workManager)

        scheduler.update(
            enabled = false,
            wifiOnly = true,
            interval = BackgroundRefreshInterval.SIX_HOURS
        )

        verify(exactly = 1) { workManager.cancelUniqueWork(WorkNames.SYNC_USER_CONTENT) }
        verify(exactly = 1) {
            workManager.enqueueUniquePeriodicWork(
                WorkNames.CLEANUP_LOCAL_DATA,
                ExistingPeriodicWorkPolicy.UPDATE,
                any()
            )
        }
    }

    @Test
    fun `enabled - enqueues periodic sync with constraints and min interval`() {
        val workManager = mockk<WorkManager>()

        val syncSlot = slot<PeriodicWorkRequest>()
        val cleanupSlot = slot<PeriodicWorkRequest>()
        val op = mockk<Operation>(relaxed = true)

        every {
            workManager.enqueueUniquePeriodicWork(
                WorkNames.SYNC_USER_CONTENT,
                ExistingPeriodicWorkPolicy.UPDATE,
                capture(syncSlot)
            )
        } returns op

        every {
            workManager.enqueueUniquePeriodicWork(
                WorkNames.CLEANUP_LOCAL_DATA,
                ExistingPeriodicWorkPolicy.UPDATE,
                capture(cleanupSlot)
            )
        } returns op

        every { workManager.cancelUniqueWork(any()) } returns op

        val scheduler = BackgroundSyncScheduler(workManager)

        scheduler.update(
            enabled = true,
            wifiOnly = true,
            interval = BackgroundRefreshInterval.SIX_HOURS
        )

        assertEquals(NetworkType.UNMETERED, syncSlot.captured.workSpec.constraints.requiredNetworkType)

        val intervalMs = syncSlot.captured.workSpec.intervalDuration
        assertTrue(intervalMs >= TimeUnit.HOURS.toMillis(6))

        verify(exactly = 1) {
            workManager.enqueueUniquePeriodicWork(
                WorkNames.SYNC_USER_CONTENT,
                ExistingPeriodicWorkPolicy.UPDATE,
                any()
            )
        }
        verify(exactly = 1) {
            workManager.enqueueUniquePeriodicWork(
                WorkNames.CLEANUP_LOCAL_DATA,
                ExistingPeriodicWorkPolicy.UPDATE,
                any()
            )
        }
    }
}

