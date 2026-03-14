package com.example.homework_3.data.local
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.homework_3.model.Character
import kotlin.String

@Entity(tableName = "favorite_character")
data class FavoriteCharacterEntity (
    @PrimaryKey
    val id: String,
    val name: String,
    val gender: String,
    val birthYear: String,
    val height: String,
    val mass: String,
    val url: String,
    val addedAt: Long = System.currentTimeMillis()
)

fun FavoriteCharacterEntity.toDomain(): Character =
    Character(
        id,
        name,
        height,
        mass,
        hairColor = "",
        skinColor = "",
        eyeColor = "",
        birthYear,
        gender,
        homeworld = "",
        films = emptyList(),
        species = emptyList(),
        vehicles = emptyList(),
        starships = emptyList(),
        created = "",
        edited = "",
        url
    )