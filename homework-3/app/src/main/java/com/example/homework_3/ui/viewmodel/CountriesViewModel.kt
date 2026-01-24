package com.example.homework_3.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.homework_3.data.CountriesRepository
import com.example.homework_3.model.Country
import com.example.homework_3.model.CountryFilter

data class CountriesUiState(
    val query: String = "",
    val filter: CountryFilter = CountryFilter.ALL,
    val favourites: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class CountriesViewModel(
    private val repository: CountriesRepository = CountriesRepository()
) : ViewModel() {

    var uiState by mutableStateOf(CountriesUiState())
        private set

    private var items by mutableStateOf(emptyList<Country>())

    init {
//        loadAllCountries()
    }
    fun retryLoad() {
        // Просто очищаем ошибку и показываем начальный экран
        uiState = uiState.copy(errorMessage = null)
        }

    private fun loadAllCountries() {
        uiState = uiState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val result = repository.getAllCountries()
                items = result
                uiState = uiState.copy(isLoading = false)
            } catch (ex: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Failed to load countries"
                )
            }
        }
    }

    fun onQueryChange(query: String) {
        uiState = uiState.copy(query = query, errorMessage = null)

        // Debounce поиска
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            search()
        }
    }

    fun onFilterChange(filter: CountryFilter) {
        uiState = uiState.copy(filter = filter)
    }

    fun onToggleFavourite(id: String) {
        val favourites = uiState.favourites
        uiState = uiState.copy(
            favourites = if (id in favourites) favourites - id else favourites + id
        )
    }

    private var searchJob: Job? = null

    private fun search() {
        val query = uiState.query.trim()
        if (query.isBlank()) {
            loadAllCountries()
            return
        }

        uiState = uiState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val result = repository.searchCountries(query)
                items = result
                uiState = uiState.copy(isLoading = false)
            } catch (ex: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Search failed: ${ex.message}"
                )
            }
        }
    }

    val visibleCountries: List<Country>
        get() = when (uiState.filter) {
            CountryFilter.ALL -> items
            CountryFilter.FAVOURITES -> items.filter { it.id in uiState.favourites }
        }
}