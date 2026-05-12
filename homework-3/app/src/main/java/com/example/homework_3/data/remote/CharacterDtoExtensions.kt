package com.example.homework_3.data.remote

import com.example.homework_3.model.Character

fun CharacterDto.toDomain(): Character {

    val safeUrl = url.orEmpty()
    val id = safeUrl.trimEnd('/').substringAfterLast("/").ifBlank { safeUrl }

    return Character(
        id = id,
        name = name ?: "Unknown",
        height = height ?: "unknown",
        mass = mass ?: "unknown",
        hairColor = hairColor ?: "unknown",
        skinColor = skinColor ?: "unknown",
        eyeColor = eyeColor ?: "unknown",
        birthYear = birthYear ?: "unknown",
        gender = gender ?: "unknown",
        homeworld = homeworld ?: "",
        films = films.orEmpty(),
        species = species.orEmpty(),
        vehicles = vehicles.orEmpty(),
        starships = starships.orEmpty(),
        created = created ?: "",
        edited = edited ?: "",
        url = safeUrl
    )
}
