package com.example.homework_3.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.homework_3.data.CharactersRepository
import com.example.homework_3.model.Character
import com.example.homework_3.model.CharacterFilter

class CharactersViewModel(
    private val repository: CharactersRepository = CharactersRepository()
) : ViewModel() {

    var uiState by mutableStateOf(CharactersUiState())
        private set

    private var allCharacters by mutableStateOf(emptyList<Character>())
    private var loadedCharactersById by mutableStateOf(mapOf<String, Character>())

    init {
        loadCharacters()
    }

    fun onQueryChange(query: String) {
        uiState = uiState.copy(query = query, errorMessage = null)

        searchJob?.cancel()
        if (query.isNotBlank()) {
            uiState = uiState.copy(isLoading = true)

            searchJob = viewModelScope.launch {
                delay(500)
                if (query == uiState.query) {
                    performSearch(query)
                } else {
                    uiState = uiState.copy(isLoading = false)
                }
            }
        } else {
            uiState = uiState.copy(isLoading = true)
            loadCharacters()
        }
    }

    private var searchJob: Job? = null

    private suspend fun performSearch(query: String) {
        try {
            val result = repository.searchCharacters(query)
            allCharacters = result
            updateCharactersMap(result)
            uiState = uiState.copy(isLoading = false)
        } catch (ex: Exception) {
            uiState = uiState.copy(
                isLoading = false,
                errorMessage = "Failed to search: ${ex.message}"
            )
        }
    }

    fun onFilterChange(filter: CharacterFilter) {
        uiState = uiState.copy(filter = filter)
    }

    fun onToggleFavourite(id: String) {
        val favourites = uiState.favourites
        uiState = uiState.copy(
            favourites = if (id in favourites) favourites - id else favourites + id
        )
    }

    fun loadCharacters(page: Int = 1) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val results = mutableListOf<Character>()
                for (currentPage in 1..2) {
                    val pageResult = repository.getCharacters(currentPage)
                    results.addAll(pageResult)
                }

                allCharacters = results
                updateCharactersMap(results)
                uiState = uiState.copy(isLoading = false, currentPage = page)
            } catch (ex: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Failed to load characters: ${ex.message}"
                )
            }
        }
    }

    private fun updateCharactersMap(newCharacters: List<Character>) {
        val newMap = loadedCharactersById.toMutableMap()
        newCharacters.forEach { character ->
            newMap[character.id] = character
        }
        loadedCharactersById = newMap
    }

    fun loadCharacter(id: String) {
        val existing = loadedCharactersById[id]
        if (existing != null) {
            uiState = uiState.copy(
                selectedCharacter = existing,
                isLoadingDetail = false,
                errorDetail = null
            )
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(
                selectedCharacter = null,
                isLoadingDetail = true,
                errorDetail = null
            )

            try {
                val character = repository.getCharacterByUrl("https://swapi.dev/api/people/$id/")
                if (character != null) {
                    updateCharactersMap(listOf(character))
                    uiState = uiState.copy(
                        selectedCharacter = character,
                        isLoadingDetail = false,
                        errorDetail = null
                    )
                } else {
                    uiState = uiState.copy(
                        selectedCharacter = null,
                        isLoadingDetail = false,
                        errorDetail = "Character not found"
                    )
                }
            } catch (ex: Exception) {
                uiState = uiState.copy(
                    selectedCharacter = null,
                    isLoadingDetail = false,
                    errorDetail = "Failed to load character: ${ex.message}"
                )
            }
        }
    }

    fun clearDetailState() {
        uiState = uiState.copy(
            selectedCharacter = null,
            isLoadingDetail = false,
            errorDetail = null
        )
    }

    val visibleCharacters: List<Character>
        get() {
            val byQuery = if (uiState.query.isBlank()) {
                allCharacters
            } else {
                allCharacters.filter { character ->
                    character.name.contains(uiState.query, ignoreCase = true)
                }
            }

            return when (uiState.filter) {
                CharacterFilter.ALL -> byQuery
                CharacterFilter.FAVOURITES -> byQuery.filter { character ->
                    character.id in uiState.favourites
                }
            }
        }
}