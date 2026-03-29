package com.example.homework_3.data

import com.example.homework_3.model.Character
import kotlinx.coroutines.flow.Flow

interface CharactersRepository {
    suspend fun getCharacters(page: Int): List<Character>
    suspend fun searchCharacters(query: String): List<Character>
    suspend fun getCharacterByUrl(url: String): Character?
    suspend fun addFavorite(character: Character)
    suspend fun removeFavorite(id: String)
    suspend fun getFavorites(): List<Character>
    fun observeFavorites(): Flow<List<Character>>
}