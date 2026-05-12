package com.example.homework_3.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.homework_3.model.Character

@Entity(
    tableName = "cached_character",
    indices = [
        Index(value = ["name"], name = "index_cached_character_name"),
        Index(value = ["updatedAt"], name = "index_cached_character_updatedAt"),
    ]
)
data class CachedCharacterEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val height: String,
    val mass: String,
    val hairColor: String,
    val skinColor: String,
    val eyeColor: String,
    val birthYear: String,
    val gender: String,
    val homeworld: String,
    val films: String = "",
    val species: String = "",
    val vehicles: String = "",
    val starships: String = "",
    val created: String,
    val edited: String,
    val url: String,
    val updatedAt: Long,
)

fun CachedCharacterEntity.toDomain(): Character = Character(
    id = id,
    name = name,
    height = height,
    mass = mass,
    hairColor = hairColor,
    skinColor = skinColor,
    eyeColor = eyeColor,
    birthYear = birthYear,
    gender = gender,
    homeworld = homeworld,
    films = if (films.isEmpty()) emptyList() else films.split(","),
    species = if (species.isEmpty()) emptyList() else species.split(","),
    vehicles = if (vehicles.isEmpty()) emptyList() else vehicles.split(","),
    starships = if (starships.isEmpty()) emptyList() else starships.split(","),
    created = created,
    edited = edited,
    url = url,
)

fun Character.toCachedEntity(updatedAt: Long = System.currentTimeMillis()): CachedCharacterEntity =
    CachedCharacterEntity(
        id = id,
        name = name,
        height = height,
        mass = mass,
        hairColor = hairColor,
        skinColor = skinColor,
        eyeColor = eyeColor,
        birthYear = birthYear,
        gender = gender,
        homeworld = homeworld,
        films = films.joinToString(","),
        species = species.joinToString(","),
        vehicles = vehicles.joinToString(","),
        starships = starships.joinToString(","),
        created = created,
        edited = edited,
        url = url,
        updatedAt = updatedAt,
    )

