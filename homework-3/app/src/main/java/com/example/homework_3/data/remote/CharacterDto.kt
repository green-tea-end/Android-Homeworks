package com.example.homework_3.data.remote

import com.google.gson.annotations.SerializedName

data class CharacterDto(
    val name: String? = null,
    val height: String? = null,
    val mass: String? = null,

    @SerializedName("hair_color")
    val hairColor: String? = null,

    @SerializedName("skin_color")
    val skinColor: String? = null,

    @SerializedName("eye_color")
    val eyeColor: String? = null,

    @SerializedName("birth_year")
    val birthYear: String? = null,

    val gender: String? = null,
    val homeworld: String? = null,
    val films: List<String>? = null,
    val species: List<String>? = null,
    val vehicles: List<String>? = null,
    val starships: List<String>? = null,
    val created: String? = null,
    val edited: String? = null,
    val url: String? = null
)
