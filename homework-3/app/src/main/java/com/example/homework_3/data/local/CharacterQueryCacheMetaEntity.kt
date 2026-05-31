package com.example.homework_3.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "character_query_cache_meta")
data class CharacterQueryCacheMetaEntity(
    @PrimaryKey val queryKey: String,
    val refreshedAt: Long,
)
