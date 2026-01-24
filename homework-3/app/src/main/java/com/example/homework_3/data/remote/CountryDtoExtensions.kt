package com.example.homework_3.data.remote

import com.example.homework_3.model.Country

fun CountryResponse.toDomainOrNull(): Country? {
    return try {
        // Базовые проверки
        if (name.common.isNullOrEmpty()) return null
        if (flags.png.isNullOrEmpty()) return null

        val id = name.common
        val currenciesStr = currencies?.entries?.joinToString(", ") {
            "${it.key} - ${it.value.name}"
        }.orEmpty().ifBlank { "Нет данных" }

        val languagesStr = languages?.values?.joinToString(", ").orEmpty()
            .ifBlank { "Нет данных" }

        // Создаем страну, но некоторые поля могут быть null
        Country(
            id = id,
            name = name.common,
            officialName = name.official ?: name.common,
            region = region ?: "Неизвестно",
            subregion = subregion, // Может быть null
            population = population ?: 0,
            area = area, // Может быть null
            flagUrl = flags.png,
            capital = capital,
            currencies = currenciesStr,
            languages = languagesStr
        )
    } catch (e: Exception) {
        null
    }
}