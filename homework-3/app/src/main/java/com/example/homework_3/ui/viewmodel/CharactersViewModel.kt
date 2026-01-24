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

    private var characters by mutableStateOf(emptyList<Character>())

    fun onQueryChange(query: String) {
        uiState = uiState.copy(query = query, errorMessage = null)

        searchJob?.cancel()
        if (query.isNotBlank()) {
            searchJob = viewModelScope.launch {
                delay(500)
                if (query == uiState.query) {
                    performSearch(query)
                }
            }
        } else {
            loadCharacters()
        }
    }

    private var searchJob: Job? = null

    private suspend fun performSearch(query: String) {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        try {
            val result = repository.searchCharacters(query)
            characters = result
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
                val result = repository.getCharacters(page)
                characters = result
                uiState = uiState.copy(isLoading = false, currentPage = page)
            } catch (ex: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Failed to load characters: ${ex.message}"
                )
            }
        }
    }

    val visibleCharacters: List<Character>
        get() = when (uiState.filter) {
            CharacterFilter.ALL -> characters
            CharacterFilter.FAVOURITES -> characters.filter { it.id in uiState.favourites }
        }

    init {
        loadCharacters()
    }
}
