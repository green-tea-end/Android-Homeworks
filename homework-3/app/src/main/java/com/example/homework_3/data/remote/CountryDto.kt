package com.example.homework_3.data.remote

import com.google.gson.annotations.SerializedName

data class CountryResponse(
    val name: CountryNameDto,
    val region: String,
    val subregion: String? = null,
    val population: Long,
    val area: Double? = null,
    val flags: CountryFlagsDto,
    val capital: List<String>? = emptyList(),
    val currencies: Map<String, CurrencyDto>? = null,
    val languages: Map<String, String>? = null
)

data class CountryNameDto(
    val common: String,
    val official: String
)

data class CountryFlagsDto(
    val png: String,
    val svg: String,
    val alt: String? = null
)

data class CurrencyDto(
    val name: String,
    val symbol: String? = null
)