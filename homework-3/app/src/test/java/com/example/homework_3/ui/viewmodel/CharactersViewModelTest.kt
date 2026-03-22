package com.example.homework_3.ui.viewmodel

import com.example.homework_3.FakeCharacterRepository
import com.example.homework_3.MainDispatcherRule
import com.example.homework_3.data.CharactersRepository
import com.example.homework_3.model.Character
import com.example.homework_3.model.CharacterFilter
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test


class CharactersViewModelTest {

    @get: Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val luke = Character(
        id = "1",
        name = "Luke Skywalker",
        height = "172",
        mass = "77",
        hairColor = "blond",
        skinColor = "fair",
        eyeColor = "blue",
        birthYear = "19BBY",
        gender = "male",
        homeworld = "https://swapi.dev/api/planets/1/",
        films = emptyList(),
        species = emptyList(),
        vehicles = emptyList(),
        starships = emptyList(),
        created = "2014-12-09T13:50:51.644000Z",
        edited = "2014-12-20T21:17:56.891000Z",
        url = "https://swapi.dev/api/people/1/"
    )

    private val vader = Character(
        id = "4",
        name = "Darth Vader",
        height = "202",
        mass = "136",
        hairColor = "none",
        skinColor = "white",
        eyeColor = "yellow",
        birthYear = "41.9BBY",
        gender = "male",
        homeworld = "https://swapi.dev/api/planets/1/",
        films = emptyList(),
        species = emptyList(),
        vehicles = emptyList(),
        starships = emptyList(),
        created = "2014-12-10T15:18:20.704000Z",
        edited = "2014-12-20T21:17:50.313000Z",
        url = "https://swapi.dev/api/people/4/"
    )

    @Test
    fun `onQueryChange updates query and clear error`() = runTest {
        // arrange
        val repository = FakeCharacterRepository()
        val viewModel = CharactersViewModel(repository)
        advanceUntilIdle()
        repository.failGetCharacters = true
        viewModel.loadCharacters()
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.errorMessage)

        // act
        viewModel.onQueryChange("luke")
        advanceUntilIdle()

        // assert
        assertEquals("luke", viewModel.uiState.query)
        assertNull(viewModel.uiState.errorMessage)
    }

    @Test
    fun `clear search query loads all characters`() = runTest {
        // arrange
        val repository = FakeCharacterRepository()
        repository.characters = listOf(luke, vader)
        repository.searchResult = listOf(luke)

        val viewModel = CharactersViewModel(repository)
        advanceUntilIdle()

        // act
        viewModel.onQueryChange("luke")
        advanceUntilIdle()
        val searchSize = viewModel.visibleCharacters.size
        viewModel.onQueryChange("")
        advanceUntilIdle()

        // assert
        assertEquals("", viewModel.uiState.query)
        assertTrue(viewModel.visibleCharacters.size > searchSize)
        assertTrue(viewModel.visibleCharacters.isNotEmpty())
    }

    @Test
    fun `search success updates visibleCharacter and calls repository`() = runTest {
        val repository = mockk<CharactersRepository>()
        coEvery { repository.getFavorites() } returns emptyList()
        coEvery { repository.searchCharacters("a") } returns listOf(luke, vader)

        val viewModel = CharactersViewModel(repository)
        advanceUntilIdle()

        viewModel.onQueryChange("a")
        advanceUntilIdle()

        assertFalse(viewModel.uiState.isLoading)
        assertNull(viewModel.uiState.errorMessage)
        assertEquals(listOf(luke, vader), viewModel.visibleCharacters)
        coVerify(exactly = 1) { repository.searchCharacters("a") }
    }

    @Test
    fun `load characters error sets error message and stops loading`() = runTest {
        // arrange
        val repository = FakeCharacterRepository()
        repository.failGetCharacters = true
        val viewModel = CharactersViewModel(repository)

        // act
        advanceUntilIdle()

        // assert
        assertFalse(viewModel.uiState.isLoading)
        assertNotNull(viewModel.uiState.errorMessage)
        assertTrue(viewModel.visibleCharacters.isEmpty())
    }

    @Test
    fun `initial state is correct`() = runTest {
        // arrange
        val repository = FakeCharacterRepository()

        // act
        val viewModel = CharactersViewModel(repository)

        // assert
        assertEquals("", viewModel.uiState.query)
        assertEquals(CharacterFilter.ALL, viewModel.uiState.filter)
        assertTrue(viewModel.uiState.favourites.isEmpty())
        assertNull(viewModel.uiState.errorMessage)
        assertNull(viewModel.uiState.selectedCharacter)
        assertFalse(viewModel.uiState.isLoadingDetail)
        assertNull(viewModel.uiState.errorDetail)
    }

    @Test
    fun `retry after error clears error and loads characters`() = runTest {
        // arrange
        val repository = FakeCharacterRepository()
        repository.characters = listOf(luke, vader)
        repository.failGetCharacters = true
        val viewModel = CharactersViewModel(repository)
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.errorMessage)

        // act
        repository.failGetCharacters = false
        viewModel.loadCharacters()
        advanceUntilIdle()

        // assert
        assertNull(viewModel.uiState.errorMessage)
        assertFalse(viewModel.uiState.isLoading)
        assertTrue(viewModel.visibleCharacters.isNotEmpty())
    }

    @Test
    fun `search with no results returns empty list`() = runTest {
        // arrange
        val repository = FakeCharacterRepository()
        repository.searchResult = emptyList()
        val viewModel = CharactersViewModel(repository)
        advanceUntilIdle()

        // act
        viewModel.onQueryChange("nonexistent")
        advanceUntilIdle()

        // assert
        assertFalse(viewModel.uiState.isLoading)
        assertNull(viewModel.uiState.errorMessage)
        assertTrue(viewModel.visibleCharacters.isEmpty())
    }

    @Test
    fun `adding same character to favourites does not create duplicate`() = runTest {
        // arrange
        val repository = mockk<CharactersRepository>(relaxed = true)
        coEvery { repository.getFavorites() } returns emptyList()
        coEvery { repository.getCharacters(any()) } returns listOf(luke)
        coEvery { repository.searchCharacters(any()) } returns emptyList()

        val viewModel = CharactersViewModel(repository)
        advanceUntilIdle()

        // act
        viewModel.onToggleFavourite(luke.id)
        advanceUntilIdle()

        // assert
        coVerify(exactly = 1) { repository.addFavorite(luke) }
        assertTrue(viewModel.uiState.favourites.contains(luke.id))
        assertEquals(1, viewModel.uiState.favourites.size)
    }
}