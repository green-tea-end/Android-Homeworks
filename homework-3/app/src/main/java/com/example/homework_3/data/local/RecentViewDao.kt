package com.example.homework_3.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentViewDao {
    @Query("SELECT * FROM recent_view ORDER BY viewedAt DESC")
    fun observeRecent(): Flow<List<RecentViewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: RecentViewEntity)

    @Query("DELETE FROM recent_view")
    suspend fun clear()

    @Query("DELETE FROM recent_view WHERE viewedAt < :minViewedAt")
    suspend fun deleteOlderThan(minViewedAt: Long)

    @Query("SELECT characterId FROM recent_view ORDER BY viewedAt DESC LIMIT :limit")
    suspend fun getRecentCharacterIds(limit: Int): List<String>

    @Query(
        """
        DELETE FROM recent_view
        WHERE characterId NOT IN (
            SELECT characterId FROM recent_view
            ORDER BY viewedAt DESC
            LIMIT :limit
        )
        """
    )
    suspend fun trimToLimit(limit: Int)
}

