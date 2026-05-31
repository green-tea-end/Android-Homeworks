package com.example.homework_3.data.sync

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.example.homework_3.data.settings.SettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SyncUserContentWorkerTest {

    @Test
    fun `disabled background refresh - skips sync`() = runTest {
        val settingsRepository = mockk<SettingsRepository>()
        every { settingsRepository.isBackgroundRefreshEnabled } returns flowOf(false)

        val worker = createWorker(
            userContentSyncRunner = mockk(relaxed = true),
            settingsRepository = settingsRepository,
        )

        assertEquals(ListenableWorker.Result.success(), worker.doWork())
        coVerify(exactly = 0) { settingsRepository.setLastBackgroundRefreshSuccessAt(any()) }
    }

    @Test
    fun `successful sync - writes last success timestamp`() = runTest {
        val settingsRepository = mockk<SettingsRepository>(relaxed = true)
        every { settingsRepository.isBackgroundRefreshEnabled } returns flowOf(true)

        val runner = mockk<UserContentSyncRunner>()
        coEvery { runner.syncUserContent() } returns SyncUserContentResult(
            updatedIds = listOf("1"),
            skippedNotFoundIds = emptyList(),
            shouldRetry = false,
        )

        val worker = createWorker(runner, settingsRepository)
        assertEquals(ListenableWorker.Result.success(), worker.doWork())

        coVerify(exactly = 1) { settingsRepository.setLastBackgroundRefreshSuccessAt(any()) }
    }

    @Test
    fun `sync requests retry - does not write last success`() = runTest {
        val settingsRepository = mockk<SettingsRepository>(relaxed = true)
        every { settingsRepository.isBackgroundRefreshEnabled } returns flowOf(true)

        val runner = mockk<UserContentSyncRunner>()
        coEvery { runner.syncUserContent() } returns SyncUserContentResult(
            updatedIds = emptyList(),
            skippedNotFoundIds = emptyList(),
            shouldRetry = true,
        )

        val worker = createWorker(runner, settingsRepository)
        assertEquals(ListenableWorker.Result.retry(), worker.doWork())

        coVerify(exactly = 0) { settingsRepository.setLastBackgroundRefreshSuccessAt(any()) }
    }

    private fun createWorker(
        userContentSyncRunner: UserContentSyncRunner,
        settingsRepository: SettingsRepository,
    ): SyncUserContentWorker {
        val context = mockk<Context>(relaxed = true)
        val params = mockk<WorkerParameters>(relaxed = true)
        return SyncUserContentWorker(context, params, userContentSyncRunner, settingsRepository)
    }
}
