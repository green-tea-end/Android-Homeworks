package com.example.homework_3

import com.example.homework_3.data.CharactersRepository
import com.example.homework_3.model.Character

class FakeCharacterRepository : CharactersRepository {
    val favorites: MutableList<Character> = mutableListOf()
    var searchResult: List<Character> = emptyList()
    var characters: List<Character> = emptyList()

    var failGetFavorites = false
    var failSearch = false
    var failGetCharacters = false
    var failGetCharacterByUrl = false


    override suspend fun getCharacters(page: Int): List<Character> {
        if (failGetCharacters) error("getCharacters failed")
        return characters
    }

    override suspend fun searchCharacters(query: String): List<Character> {
        if (failSearch) error("search failed")
        return searchResult.filter { it.name.contains(query, ignoreCase = true) }
    }

    override suspend fun getCharacterByUrl(url: String): Character? {
        if (failGetCharacterByUrl) error("getCharacterByUrl failed")
        val id = url.trimEnd('/').substringAfterLast("/")
        return characters.find { it.id == id }
            ?: searchResult.find { it.id == id }
    }

    override suspend fun addFavorite(character: Character) {
        favorites.removeAll { it.id == character.id }
        favorites.add(0, character)
    }

    override suspend fun removeFavorite(id: String) {
        favorites.removeAll { it.id == id }
    }

    override suspend fun getFavorites(): List<Character> {
        if (failGetFavorites) error("getFavorites failed")
        return favorites.toList()
    }

}