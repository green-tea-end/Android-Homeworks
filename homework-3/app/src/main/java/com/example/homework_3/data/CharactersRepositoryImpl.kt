package com.example.homework_3.data

import com.example.homework_3.data.local.FavoriteCharacterDao
import com.example.homework_3.data.local.toDomain
import com.example.homework_3.data.remote.SwapiApi
import com.example.homework_3.data.remote.toDomain
import com.example.homework_3.model.Character
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharactersRepositoryImpl @Inject constructor(
    private val api: SwapiApi,
    private val favoriteDao: FavoriteCharacterDao
) : CharactersRepository {

    override suspend fun getCharacters(page: Int): List<Character> = withContext(Dispatchers.IO) {
        api.getCharacters(page = page).results.map { it.toDomain() }
    }

    override suspend fun searchCharacters(query: String): List<Character> = withContext(Dispatchers.IO) {
        api.getCharacters(search = query).results.map { it.toDomain() }
    }

    override suspend fun getCharacterByUrl(url: String): Character? = withContext(Dispatchers.IO) {
        try {
            api.getCharacterByUrl(url).toDomain()
        } catch (e: Exception) {
            android.util.Log.e("CharactersRepository", "Error fetching character by url: $url", e)
            null
        }
    }

    override suspend fun addFavorite(character: Character) = withContext(Dispatchers.IO) {
        val entity = com.example.homework_3.data.local.FavoriteCharacterEntity(
            id = character.id,
            name = character.name,
            gender = character.gender,
            birthYear = character.birthYear,
            height = character.height,
            mass = character.mass,
            url = character.url
        )
        favoriteDao.insert(entity)
    }

    override suspend fun removeFavorite(id: String) = withContext(Dispatchers.IO) {
        favoriteDao.deleteById(id)
    }

    override suspend fun getFavorites(): List<Character> = withContext(Dispatchers.IO) {
        favoriteDao.getAll().map { it.toDomain() }
    }
}