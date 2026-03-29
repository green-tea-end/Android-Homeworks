package com.example.homework_3.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCharacterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FavoriteCharacterEntity)

    @Query("DELETE FROM favorite_character WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM favorite_character ORDER BY addedAt DESC")
    suspend fun getAll(): List<FavoriteCharacterEntity>

    @Query("SELECT * FROM favorite_character ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<FavoriteCharacterEntity>>
}