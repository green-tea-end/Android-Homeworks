package com.example.homework_3.data.sync

import com.example.homework_3.data.CharactersRepository
import com.example.homework_3.data.local.FavoriteCharacterDao
import com.example.homework_3.data.local.RecentViewDao
import retrofit2.HttpException
import java.io.IOException

class UserContentSyncRunner(
    private val charactersRepository: CharactersRepository,
    private val favoriteDao: FavoriteCharacterDao,
    private val recentViewDao: RecentViewDao,
) {
    suspend fun syncUserContent(): SyncUserContentResult {
        val favoriteIds = favoriteDao.getAll().map { it.id }
        val recentIds = recentViewDao.getRecentCharacterIds(limit = 50)
        val ids = (favoriteIds + recentIds).distinct()

        val updatedIds = mutableListOf<String>()
        val skippedNotFoundIds = mutableListOf<String>()
        var shouldRetry = false

        for (id in ids) {
            try {
                charactersRepository.refreshCharacterById(id = id, force = false)
                updatedIds += id
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    skippedNotFoundIds += id
                } else {
                    shouldRetry = true
                }
            } catch (e: IOException) {
                shouldRetry = true
            } catch (e: Exception) {
                shouldRetry = true
            }
        }

        return SyncUserContentResult(
            updatedIds = updatedIds,
            skippedNotFoundIds = skippedNotFoundIds,
            shouldRetry = shouldRetry,
        )
    }
}

data class SyncUserContentResult(
    val updatedIds: List<String>,
    val skippedNotFoundIds: List<String>,
    val shouldRetry: Boolean,
)
