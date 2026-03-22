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
import com.example.homework_3.data.local.FavoriteCharacterDao

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
        ).build()

    @Provides
    fun providesFavoriteDao(db: CharacterDatabase): FavoriteCharacterDao =
        db.favoriteCharacterDao()
}