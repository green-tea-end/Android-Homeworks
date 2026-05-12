package com.example.homework_3.data

import com.example.homework_3.model.Character
import kotlinx.coroutines.flow.Flow

interface CharactersRepository {
    fun observeCharacters(query: String): Flow<List<Character>>
    fun observeCharacter(id: String): Flow<Character?>
    suspend fun refreshCharacters(query: String, force: Boolean = false)
    suspend fun refreshCharacterById(id: String, force: Boolean = false)

    suspend fun getCharacterByUrl(url: String): Character?
    suspend fun addFavorite(character: Character)
    suspend fun removeFavorite(id: String)
    suspend fun getFavorites(): List<Character>
    fun observeFavorites(): Flow<List<Character>>
}