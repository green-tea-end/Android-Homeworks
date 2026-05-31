package com.example.homework_3.data

import com.example.homework_3.data.local.CachedCharacterDao
import com.example.homework_3.data.local.CharacterQueryCacheMetaDao
import com.example.homework_3.data.local.CharacterQueryCacheMetaEntity
import com.example.homework_3.data.local.FavoriteCharacterDao
import com.example.homework_3.data.local.FavoriteCharacterEntity
import com.example.homework_3.data.local.toDomain
import com.example.homework_3.data.local.toCachedEntity
import com.example.homework_3.data.remote.SwapiApi
import com.example.homework_3.data.remote.toDomain
import com.example.homework_3.data.settings.SettingsRepository
import com.example.homework_3.model.Character
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class CharactersRepositoryImpl @Inject constructor(
    private val api: SwapiApi,
    private val favoriteDao: FavoriteCharacterDao,
    private val cachedDao: CachedCharacterDao,
    private val cacheMetaDao: CharacterQueryCacheMetaDao,
    private val settingsRepository: SettingsRepository,
) : CharactersRepository {

    override fun observeCharacters(query: String): Flow<List<Character>> {
        return cachedDao.observeByQuery(query)
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()
    }

    override fun observeCharacter(id: String): Flow<Character?> {
        return cachedDao.observeById(id)
            .map { it?.toDomain() }
            .distinctUntilChanged()
    }

    override suspend fun getCharacterByUrl(url: String): Character? = withContext(Dispatchers.IO) {
        try {
            val domain = api.getCharacterByUrl(url).toDomain()
            cachedDao.upsert(domain.toCachedEntity(updatedAt = System.currentTimeMillis()))
            domain
        } catch (e: HttpException) {
            if (e.code() == 404) null else throw e
        }
    }

    override suspend fun refreshCharacters(query: String, force: Boolean) = withContext(Dispatchers.IO) {
        val ttl = currentTtl()
        if (!force && isCacheFresh(query, ttl)) return@withContext

        val now = System.currentTimeMillis()
        val results = if (query.isBlank()) {
            val all = mutableListOf<Character>()
            for (page in 1..2) {
                all.addAll(api.getCharacters(page = page).results.map { it.toDomain() })
            }
            all
        } else {
            api.getCharacters(search = query).results.map { it.toDomain() }
        }

        cachedDao.upsertAll(results.map { it.toCachedEntity(updatedAt = now) })
        cacheMetaDao.upsert(
            CharacterQueryCacheMetaEntity(
                queryKey = queryKeyFor(query),
                refreshedAt = now,
            )
        )
    }

    override suspend fun refreshCharacterById(id: String, force: Boolean) = withContext(Dispatchers.IO) {
        val ttl = currentTtl()
        val cached = cachedDao.observeById(id).first()
        val isFresh = cached?.updatedAt?.let { (System.currentTimeMillis() - it).milliseconds < ttl } ?: false
        if (!force && isFresh) return@withContext

        val now = System.currentTimeMillis()
        val loaded = api.getCharacterByUrl("people/$id/").toDomain()
        cachedDao.upsert(loaded.toCachedEntity(updatedAt = now))
    }

    override suspend fun addFavorite(character: Character) = withContext(Dispatchers.IO) {
        val entity = FavoriteCharacterEntity(
            id = character.id,
            name = character.name,
            height = character.height,
            mass = character.mass,
            hairColor = character.hairColor,
            skinColor = character.skinColor,
            eyeColor = character.eyeColor,
            birthYear = character.birthYear,
            gender = character.gender,
            homeworld = character.homeworld,
            films = character.films.joinToString(","),
            species = character.species.joinToString(","),
            vehicles = character.vehicles.joinToString(","),
            starships = character.starships.joinToString(","),
            created = character.created,
            edited = character.edited,
            url = character.url
        )
        favoriteDao.insert(entity)

        cachedDao.upsert(character.toCachedEntity(updatedAt = System.currentTimeMillis()))
    }

    override suspend fun removeFavorite(id: String) = withContext(Dispatchers.IO) {
        favoriteDao.deleteById(id)
    }

    override suspend fun getFavorites(): List<Character> = withContext(Dispatchers.IO) {
        resolveFavorites(favoriteDao.getAll())
    }

    override fun observeFavorites(): Flow<List<Character>> {
        return favoriteDao.observeAll()
            .flatMapLatest { favorites ->
                if (favorites.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    val ids = favorites.map { it.id }
                    cachedDao.observeByIds(ids).map { cachedEntities ->
                        resolveFavorites(favorites, cachedEntities)
                    }
                }
            }
    }

    private suspend fun currentTtl(): Duration {
        val preset = settingsRepository.cacheTtlPreset.first()
        return preset.duration
    }

    private suspend fun isCacheFresh(query: String, ttl: Duration): Boolean {
        val refreshedAt = cacheMetaDao.getRefreshedAt(queryKeyFor(query)) ?: return false
        val age = (System.currentTimeMillis() - refreshedAt).milliseconds
        return age < ttl
    }

    private fun queryKeyFor(query: String): String = query.trim().lowercase()

    private fun resolveFavorites(
        favorites: List<FavoriteCharacterEntity>,
        cachedEntities: List<com.example.homework_3.data.local.CachedCharacterEntity> = emptyList(),
    ): List<Character> {
        val cachedById = cachedEntities.associateBy { it.id }
        return favorites.map { favorite ->
            cachedById[favorite.id]?.toDomain() ?: favorite.toDomain()
        }
    }
}
