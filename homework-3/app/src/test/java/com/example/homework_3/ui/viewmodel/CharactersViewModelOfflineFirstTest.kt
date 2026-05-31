package com.example.homework_3.ui.viewmodel

import com.example.homework_3.MainDispatcherRule
import com.example.homework_3.data.CharactersRepository
import com.example.homework_3.data.local.RecentViewEntity
import com.example.homework_3.data.recent.RecentRepository
import com.example.homework_3.model.Character
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class CharactersViewModelOfflineFirstTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `cache present + refresh fails - keeps cached list and exposes error message`() = runTest {
        val cached = listOf(sampleCharacter(id = "1", name = "Luke"))

        val repository = object : CharactersRepository {
            override fun observeCharacters(query: String): Flow<List<Character>> = flowOf(cached)
            override fun observeCharacter(id: String): Flow<Character?> = flowOf(cached.firstOrNull { it.id == id })

            override suspend fun refreshCharacters(query: String, force: Boolean) {
                throw IOException("boom")
            }

            override suspend fun refreshCharacterById(id: String, force: Boolean) = Unit
            override suspend fun getCharacterByUrl(url: String): Character? = null
            override suspend fun addFavorite(character: Character) = Unit
            override suspend fun removeFavorite(id: String) = Unit
            override suspend fun getFavorites(): List<Character> = emptyList()
            override fun observeFavorites(): Flow<List<Character>> = flowOf(emptyList())
        }

        val recentRepository = object : RecentRepository {
            override fun observeRecent(): Flow<List<RecentViewEntity>> = flowOf(emptyList())
            override suspend fun addView(character: Character) = Unit
            override suspend fun clear() = Unit
        }

        val vm = CharactersViewModel(repository, recentRepository)

        val collectJob = launch { vm.uiState.collect { /* keep active */ } }
        try {
            advanceTimeBy(600) // debounce for queryForCacheFlow
            advanceUntilIdle()

            val state = vm.uiState.value
            assertEquals(cached, state.visibleCharacters)
            assertFalse(state.isLoading)
            assertNotNull(state.errorMessage)
        } finally {
            collectJob.cancel()
        }
    }

    private fun sampleCharacter(id: String, name: String): Character =
        Character(
            id = id,
            name = name,
            height = "unknown",
            mass = "unknown",
            hairColor = "unknown",
            skinColor = "unknown",
            eyeColor = "unknown",
            birthYear = "unknown",
            gender = "unknown",
            homeworld = "",
            created = "",
            edited = "",
            url = "people/$id/"
        )
}

