package com.example.homework_3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework_3.data.CharactersRepository
import com.example.homework_3.model.Character
import com.example.homework_3.model.CharacterFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharactersViewModel @Inject constructor(
    private val repository: CharactersRepository
) : ViewModel() {

    private val queryFlow = MutableStateFlow("")
    private val filterFlow = MutableStateFlow(CharacterFilter.ALL)
    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    private val _detailState = MutableStateFlow(DetailState())
    val detailState: StateFlow<DetailState> = _detailState.asStateFlow()

    private val favouritesFlow: StateFlow<List<Character>> = repository.observeFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val searchFlow: StateFlow<SearchStatus> = combine(
        queryFlow,
        refreshTrigger
    ) { query, _ -> query }
        .debounce(500)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            flow {
                if (query.isNotBlank()) {
                    emit(SearchStatus.Loading)
                    try {
                        val results = repository.searchCharacters(query)
                        emit(SearchStatus.Success(results))
                    } catch (e: Exception) {
                        emit(SearchStatus.Error(e.message ?: "Search failed"))
                    }
                } else {
                    emit(SearchStatus.Loading)
                    try {
                        val allCharacters = mutableListOf<Character>()
                        for (page in 1..2) {
                            allCharacters.addAll(repository.getCharacters(page))
                        }
                        emit(SearchStatus.Success(allCharacters))
                    } catch (e: Exception) {
                        emit(SearchStatus.Error(e.message ?: "Failed to load characters"))
                    }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SearchStatus.Idle
        )

    private val _charactersCache = MutableStateFlow<Map<String, Character>>(emptyMap())

    val uiState: StateFlow<CharactersUiState> = combine(
        queryFlow,
        filterFlow,
        favouritesFlow,
        searchFlow
    ) { query, filter, favourites, searchState ->
        val searchResults = when (searchState) {
            is SearchStatus.Success -> searchState.items
            else -> emptyList()
        }

        val isLoading = searchState is SearchStatus.Loading
        val errorMessage = (searchState as? SearchStatus.Error)?.message

        val visibleCharacters = when (filter) {
            CharacterFilter.ALL -> searchResults
            CharacterFilter.FAVOURITES -> {
                if (query.isBlank()) {
                    favourites
                } else {
                    favourites.filter { it.name.contains(query, ignoreCase = true) }
                }
            }
        }

        CharactersUiState(
            query = query,
            filter = filter,
            favourites = favourites.map { it.id }.toSet(),
            visibleCharacters = visibleCharacters,
            isLoading = isLoading,
            errorMessage = errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CharactersUiState()
    )

    init {
        viewModelScope.launch {
            searchFlow.collect { searchState ->
                if (searchState is SearchStatus.Success) {
                    val newCache = _charactersCache.value.toMutableMap()
                    searchState.items.forEach { character ->
                        newCache[character.id] = character
                    }
                    _charactersCache.value = newCache
                }
            }
        }

        viewModelScope.launch {
            favouritesFlow.collect { favourites ->
                val newCache = _charactersCache.value.toMutableMap()
                favourites.forEach { character ->
                    newCache[character.id] = character
                }
                _charactersCache.value = newCache
            }
        }
    }

    fun onQueryChange(query: String) {
        queryFlow.value = query
    }

    fun onFilterChange(filter: CharacterFilter) {
        filterFlow.value = filter
    }

    fun onRefresh() {
        viewModelScope.launch {
            refreshTrigger.emit(Unit)
        }
    }

    fun onToggleFavourite(id: String) {
        viewModelScope.launch {
            val currentFavs = favouritesFlow.value
            val isFavourite = currentFavs.any { it.id == id }

            if (isFavourite) {
                repository.removeFavorite(id)
            } else {
                val character = _charactersCache.value[id]
                if (character != null) {
                    repository.addFavorite(character)
                } else {
                    try {
                        val loaded = repository.getCharacterByUrl("https://swapi.dev/api/people/$id/")
                        loaded?.let {
                            repository.addFavorite(it)
                        }
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }

    fun loadCharacter(id: String) {
        viewModelScope.launch {
            _detailState.value = DetailState(isLoading = true)

            val cached = _charactersCache.value[id]
            if (cached != null) {
                _detailState.value = DetailState(
                    character = cached,
                    isLoading = false,
                    error = null
                )
                return@launch
            }

            try {
                val character = repository.getCharacterByUrl("https://swapi.dev/api/people/$id/")
                if (character != null) {
                    val newCache = _charactersCache.value.toMutableMap()
                    newCache[id] = character
                    _charactersCache.value = newCache

                    _detailState.value = DetailState(
                        character = character,
                        isLoading = false,
                        error = null
                    )
                } else {
                    _detailState.value = DetailState(
                        character = null,
                        isLoading = false,
                        error = "Character not found"
                    )
                }
            } catch (e: Exception) {
                _detailState.value = DetailState(
                    character = null,
                    isLoading = false,
                    error = "Failed to load character: ${e.message}"
                )
            }
        }
    }

    fun clearDetailState() {
        _detailState.value = DetailState()
    }

    sealed class SearchStatus {
        data object Idle : SearchStatus()
        data object Loading : SearchStatus()
        data class Success(val items: List<Character>) : SearchStatus()
        data class Error(val message: String) : SearchStatus()
    }

    data class DetailState(
        val character: Character? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )
}