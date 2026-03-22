package com.example.homework_3.data

import com.example.homework_3.model.Character

interface CharactersRepository {
    suspend fun getCharacters(page: Int): List<Character>
    suspend fun searchCharacters(query: String): List<Character>
    suspend fun getCharacterByUrl(url: String): Character?
    suspend fun addFavorite(character: Character)
    suspend fun removeFavorite(id: String)
    suspend fun getFavorites(): List<Character>
}