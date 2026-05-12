package com.example.homework_3.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.homework_3.data.CharactersRepository
import com.example.homework_3.data.local.FavoriteCharacterDao
import com.example.homework_3.data.local.RecentViewDao
import com.example.homework_3.data.settings.SettingsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException

@HiltWorker
class SyncUserContentWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val charactersRepository: CharactersRepository,
    private val favoriteDao: FavoriteCharacterDao,
    private val recentViewDao: RecentViewDao,
    private val settingsRepository: SettingsRepository,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val enabled = settingsRepository.isBackgroundRefreshEnabled.first()
        if (!enabled) return Result.success()

        return try {
            val favoriteIds = favoriteDao.getAll().map { it.id }
            val recentIds = recentViewDao.getRecentCharacterIds(limit = 50)
            val ids = (favoriteIds + recentIds).distinct()

            for (id in ids) {
                charactersRepository.refreshCharacterById(id = id, force = false)
            }

            settingsRepository.setLastBackgroundRefreshSuccessAt(System.currentTimeMillis())
            Result.success()
        } catch (e: HttpException) {
            if (e.code() == 404) Result.success() else Result.retry()
        } catch (e: IOException) {
            Result.retry()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

