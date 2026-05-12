package com.example.homework_3.di

import com.example.homework_3.data.recent.RecentRepository
import com.example.homework_3.data.recent.RecentRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RecentModule {
    @Binds
    @Singleton
    abstract fun bindRecentRepository(
        impl: RecentRepositoryImpl,
    ): RecentRepository
}

