package com.example.homework_3.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedCharacterDao {
    @Query(
        """
        SELECT * FROM cached_character
        WHERE (:query IS NULL OR :query = '' OR name LIKE '%' || :query || '%')
        ORDER BY name ASC
        """
    )
    fun observeByQuery(query: String?): Flow<List<CachedCharacterEntity>>

    @Query("SELECT * FROM cached_character WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<CachedCharacterEntity?>

    @Query("SELECT * FROM cached_character WHERE id IN (:ids)")
    fun observeByIds(ids: List<String>): Flow<List<CachedCharacterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<CachedCharacterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CachedCharacterEntity)
}

