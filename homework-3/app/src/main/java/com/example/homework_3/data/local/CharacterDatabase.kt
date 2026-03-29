package com.example.homework_3.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FavoriteCharacterEntity::class],
    version = 2,
    exportSchema = false
)
abstract class CharacterDatabase : RoomDatabase() {

    abstract fun favoriteCharacterDao(): FavoriteCharacterDao
}