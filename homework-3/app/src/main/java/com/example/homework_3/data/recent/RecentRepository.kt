package com.example.homework_3.data.recent

import com.example.homework_3.data.local.RecentViewEntity
import com.example.homework_3.model.Character
import kotlinx.coroutines.flow.Flow

interface RecentRepository {
    fun observeRecent(): Flow<List<RecentViewEntity>>
    suspend fun addView(character: Character)
    suspend fun clear()
}

