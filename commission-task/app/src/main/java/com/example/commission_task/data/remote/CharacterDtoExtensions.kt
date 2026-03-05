package com.example.commission_task.data.remote

import com.example.commission_task.model.Character

fun CharacterDto.toDomain(): Character {

    val id = url.trimEnd('/').substringAfterLast("/")

    return Character(
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
        films = films,
        species = species,
        vehicles = vehicles,
        starships = starships,
        created = created,
        edited = edited,
        url = url
    )
}
