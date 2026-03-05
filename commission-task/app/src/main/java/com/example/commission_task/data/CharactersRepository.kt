package com.example.commission_task.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.commission_task.NetworkModule
import com.example.commission_task.data.remote.SwapiApi
import com.example.commission_task.data.remote.toDomain
import com.example.commission_task.model.Character

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
