package com.example.homework_3.data.local

import androidx.room.Database
import androidx.room.migration.Migration
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [FavoriteCharacterEntity::class, RecentViewEntity::class, CachedCharacterEntity::class],
    version = 4,
    exportSchema = false
)
abstract class CharacterDatabase : RoomDatabase() {

    abstract fun favoriteCharacterDao(): FavoriteCharacterDao
    abstract fun recentViewDao(): RecentViewDao
    abstract fun cachedCharacterDao(): CachedCharacterDao

    companion object {
        val MIGRATION_2_3: Migration =
            object : Migration(2, 3) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `recent_view` (
                            `characterId` TEXT NOT NULL,
                            `characterName` TEXT NOT NULL,
                            `viewedAt` INTEGER NOT NULL,
                            PRIMARY KEY(`characterId`)
                        )
                        """.trimIndent()
                    )
                }
            }

        val MIGRATION_3_4: Migration =
            object : Migration(3, 4) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `cached_character` (
                            `id` TEXT NOT NULL,
                            `name` TEXT NOT NULL,
                            `height` TEXT NOT NULL,
                            `mass` TEXT NOT NULL,
                            `hairColor` TEXT NOT NULL,
                            `skinColor` TEXT NOT NULL,
                            `eyeColor` TEXT NOT NULL,
                            `birthYear` TEXT NOT NULL,
                            `gender` TEXT NOT NULL,
                            `homeworld` TEXT NOT NULL,
                            `films` TEXT NOT NULL,
                            `species` TEXT NOT NULL,
                            `vehicles` TEXT NOT NULL,
                            `starships` TEXT NOT NULL,
                            `created` TEXT NOT NULL,
                            `edited` TEXT NOT NULL,
                            `url` TEXT NOT NULL,
                            `updatedAt` INTEGER NOT NULL,
                            PRIMARY KEY(`id`)
                        )
                        """.trimIndent()
                    )
                    db.execSQL("CREATE INDEX IF NOT EXISTS `index_cached_character_name` ON `cached_character` (`name`)")
                    db.execSQL("CREATE INDEX IF NOT EXISTS `index_cached_character_updatedAt` ON `cached_character` (`updatedAt`)")
                }
            }
    }
}