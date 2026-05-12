package com.example.homework_3.data.recent

import com.example.homework_3.data.local.RecentViewDao
import com.example.homework_3.data.local.RecentViewEntity
import com.example.homework_3.model.Character
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecentRepositoryImpl @Inject constructor(
    private val dao: RecentViewDao,
) : RecentRepository {

    override fun observeRecent(): Flow<List<RecentViewEntity>> = dao.observeRecent()

    override suspend fun addView(character: Character) {
        val now = System.currentTimeMillis()
        dao.upsert(
            RecentViewEntity(
                characterId = character.id,
                characterName = character.name,
                viewedAt = now,
            )
        )

        val thirtyDaysMs = 30L * 24L * 60L * 60L * 1000L
        dao.deleteOlderThan(now - thirtyDaysMs)
        dao.trimToLimit(200)
    }

    override suspend fun clear() {
        dao.clear()
    }
}

