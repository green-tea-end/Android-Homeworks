package com.example.homework_3.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_view")
data class RecentViewEntity(
    @PrimaryKey
    val characterId: String,
    val characterName: String,
    val viewedAt: Long,
)

