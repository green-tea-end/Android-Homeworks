package com.example.homework_3.model

data class Country(
    val id: String, // Используем common name как ID
    val name: String,
    val officialName: String,
    val region: String,
    val subregion: String?,
    val population: Long,
    val area: Double?,
    val flagUrl: String,
    val capital: List<String>?,
    val currencies: String,
    val languages: String
)

enum class CountryFilter {
    ALL,
    FAVOURITES
}