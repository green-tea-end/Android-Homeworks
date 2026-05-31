package com.example.homework_3.di

import androidx.room.Room
import com.example.homework_3.data.local.CharacterDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import android.content.Context
import com.example.homework_3.data.CharactersRepository
import com.example.homework_3.data.local.CachedCharacterDao
import com.example.homework_3.data.local.CharacterQueryCacheMetaDao
import com.example.homework_3.data.local.FavoriteCharacterDao
import com.example.homework_3.data.local.RecentViewDao
import com.example.homework_3.data.sync.UserContentSyncRunner

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CharacterDatabase =
        Room.databaseBuilder(
            context,
            CharacterDatabase::class.java,
            "character_browser.db"
        )
            .addMigrations(CharacterDatabase.MIGRATION_2_3)
            .addMigrations(CharacterDatabase.MIGRATION_3_4)
            .addMigrations(CharacterDatabase.MIGRATION_4_5)
            .build()

    @Provides
    fun providesFavoriteDao(db: CharacterDatabase): FavoriteCharacterDao =
        db.favoriteCharacterDao()

    @Provides
    fun providesRecentViewDao(db: CharacterDatabase): RecentViewDao =
        db.recentViewDao()

    @Provides
    fun providesCachedCharacterDao(db: CharacterDatabase): CachedCharacterDao =
        db.cachedCharacterDao()

    @Provides
    fun providesCharacterQueryCacheMetaDao(db: CharacterDatabase): CharacterQueryCacheMetaDao =
        db.characterQueryCacheMetaDao()

    @Provides
    @Singleton
    fun provideUserContentSyncRunner(
        charactersRepository: CharactersRepository,
        favoriteDao: FavoriteCharacterDao,
        recentViewDao: RecentViewDao,
    ): UserContentSyncRunner = UserContentSyncRunner(
        charactersRepository = charactersRepository,
        favoriteDao = favoriteDao,
        recentViewDao = recentViewDao,
    )
}