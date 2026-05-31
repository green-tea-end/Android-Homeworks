package com.example.homework_3.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CharacterQueryCacheMetaDao {
    @Query("SELECT refreshedAt FROM character_query_cache_meta WHERE queryKey = :queryKey LIMIT 1")
    suspend fun getRefreshedAt(queryKey: String): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CharacterQueryCacheMetaEntity)
}
