package com.example.homework_3.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.homework_3.NetworkModule
import com.example.homework_3.data.remote.SwapiApi
import com.example.homework_3.data.remote.toDomain
import com.example.homework_3.model.Character

class CharactersRepository(private val api: SwapiApi = NetworkModule.api) {

    suspend fun getCharacters(page: Int = 1): List<Character> =
        withContext(Dispatchers.IO) {
            api.getCharacters(page = page).results.map { it.toDomain() }
        }

    suspend fun searchCharacters(query: String): List<Character> =
        withContext(Dispatchers.IO) {
            api.getCharacters(search = query).results.map { it.toDomain() }
        }

    suspend fun getCharacterByUrl(url: String): Character? =
        withContext(Dispatchers.IO) {
            try {
                api.getCharacterByUrl(url).toDomain()
            } catch (e: Exception) {
                null
            }
        }
}