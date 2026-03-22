package com.example.homework_3.ui.screen

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.homework_3.FakeCharacterRepository
import com.example.homework_3.model.Character
import com.example.homework_3.ui.viewmodel.CharactersViewModel
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CharacterListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: CharactersViewModel
    private lateinit var repository: FakeCharacterRepository

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

    @Before
    fun setup() {
        repository = FakeCharacterRepository()
        repository.characters = listOf(luke)
        repository.searchResult = listOf(luke)
        viewModel = CharactersViewModel(repository)
    }

    @Test
    fun `click_on_character_navigates_to_detail_screen`() = runTest {
        repository = FakeCharacterRepository()
        repository.characters = listOf(luke)
        repository.searchResult = listOf(luke)
        viewModel = CharactersViewModel(repository)

        advanceUntilIdle()

        val uniqueIds = viewModel.visibleCharacters.map { it.id }.distinct()
        println("Visible characters: ${viewModel.visibleCharacters.size}, unique ids: ${uniqueIds.size}")

        composeTestRule.setContent {
            CharacterListScreen(
                state = viewModel.uiState,
                characters = viewModel.visibleCharacters.distinctBy { it.id },
                onSearchChange = viewModel::onQueryChange,
                onFilterChange = viewModel::onFilterChange,
                onToggleFavourite = viewModel::onToggleFavourite,
                onRefresh = { viewModel.loadCharacters() },
                onCharacterClick = { characterId ->
                    viewModel.loadCharacter(characterId)
                }
            )
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Luke Skywalker").assertExists()
        composeTestRule.onNodeWithText("Luke Skywalker").performClick()
        assert(viewModel.uiState.selectedCharacter?.name == "Luke Skywalker")
    }

    @Test
    fun `after_loading_displays_character_list_correctly`() = runTest {
        // arrange
        repository = FakeCharacterRepository()
        repository.characters = listOf(luke, vader)
        repository.searchResult = listOf(luke, vader)
        viewModel = CharactersViewModel(repository)

        advanceUntilIdle()

        composeTestRule.setContent {
            CharacterListScreen(
                state = viewModel.uiState,
                characters = viewModel.visibleCharacters.distinctBy { it.id },
                onSearchChange = viewModel::onQueryChange,
                onFilterChange = viewModel::onFilterChange,
                onToggleFavourite = viewModel::onToggleFavourite,
                onRefresh = { viewModel.loadCharacters() },
                onCharacterClick = { }
            )
        }

        composeTestRule.waitForIdle()

        // assert
        composeTestRule.onNodeWithText("Luke Skywalker").assertExists()
        composeTestRule.onNodeWithText("Darth Vader").assertExists()

        composeTestRule.onNodeWithText("Loading characters").assertDoesNotExist()

        composeTestRule.onNodeWithText("Retry").assertDoesNotExist()
    }

    @Test
    fun `retry_initiates_new_request_after_error`() = runTest {
        // arrange
        repository = FakeCharacterRepository()
        repository.failGetCharacters = true
        viewModel = CharactersViewModel(repository)

        advanceUntilIdle()

        composeTestRule.setContent {
            CharacterListScreen(
                state = viewModel.uiState,
                characters = viewModel.visibleCharacters.distinctBy { it.id },
                onSearchChange = viewModel::onQueryChange,
                onFilterChange = viewModel::onFilterChange,
                onToggleFavourite = viewModel::onToggleFavourite,
                onRefresh = { viewModel.loadCharacters() },
                onCharacterClick = { }
            )
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Retry").assertExists()

        // act
        repository.failGetCharacters = false
        repository.characters = listOf(luke)
        composeTestRule.onNodeWithText("Retry").performClick()
        advanceUntilIdle()
        composeTestRule.waitForIdle()

        // assert
        composeTestRule.onNodeWithText("Luke Skywalker").assertExists()
        composeTestRule.onNodeWithText("Retry").assertDoesNotExist()
    }

    @Test
    fun `state_transitions_correctly_from_error_to_success_after_retry`() = runTest {
        // arrange
        repository = FakeCharacterRepository()
        repository.failGetCharacters = true
        viewModel = CharactersViewModel(repository)

        advanceUntilIdle()

        composeTestRule.setContent {
            CharacterListScreen(
                state = viewModel.uiState,
                characters = viewModel.visibleCharacters.distinctBy { it.id },
                onSearchChange = viewModel::onQueryChange,
                onFilterChange = viewModel::onFilterChange,
                onToggleFavourite = viewModel::onToggleFavourite,
                onRefresh = { viewModel.loadCharacters() },
                onCharacterClick = { }
            )
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Retry").assertExists()
        composeTestRule.onNodeWithText("Luke Skywalker").assertDoesNotExist()

        // act
        repository.failGetCharacters = false
        repository.characters = listOf(luke, vader)
        composeTestRule.onNodeWithText("Retry").performClick()

        advanceUntilIdle()
        composeTestRule.waitForIdle()

        // assert
        composeTestRule.onNodeWithText("Luke Skywalker").assertExists()
        composeTestRule.onNodeWithText("Darth Vader").assertExists()
        composeTestRule.onNodeWithText("Retry").assertDoesNotExist()
    }

}