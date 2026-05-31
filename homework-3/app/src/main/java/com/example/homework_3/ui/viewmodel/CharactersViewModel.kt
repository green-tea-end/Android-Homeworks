package com.example.homework_3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework_3.data.CharactersRepository
import com.example.homework_3.data.recent.RecentRepository
import com.example.homework_3.model.Character
import com.example.homework_3.model.CharacterFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharactersViewModel @Inject constructor(
    private val repository: CharactersRepository,
    private val recentRepository: RecentRepository,
) : ViewModel() {

    private val queryFlow = MutableStateFlow("")
    private val filterFlow = MutableStateFlow(CharacterFilter.ALL)
    private val refreshTrigger = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)

    private val _detailState = MutableStateFlow(DetailState())
    val detailState: StateFlow<DetailState> = _detailState.asStateFlow()

    private val favouritesFlow: StateFlow<List<Character>> = repository.observeFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val queryForCacheFlow: StateFlow<String> =
        queryFlow
            .debounce(500)
            .distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    private val charactersFromCacheFlow: StateFlow<List<Character>> =
        queryForCacheFlow
            .flatMapLatest { query -> repository.observeCharacters(query) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val refreshStateFlow: StateFlow<SearchStatus> =
        combine(
            queryForCacheFlow,
            refreshTrigger.onStart { emit(false) }
        ) { query, force -> query to force }
            .flatMapLatest { (query, force) ->
                flow {
                    emit(SearchStatus.Loading)
                    try {
                        repository.refreshCharacters(query = query, force = force)
                        emit(SearchStatus.Success)
                    } catch (e: Exception) {
                        emit(SearchStatus.Error(mapErrorToMessage(e, fallback = "Failed to refresh")))
                    }
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SearchStatus.Loading)

    val uiState: StateFlow<CharactersUiState> = combine(
        queryFlow,
        filterFlow,
        favouritesFlow,
        charactersFromCacheFlow,
        refreshStateFlow,
    ) { query, filter, favourites, cachedCharacters, refreshState ->
        val isLoading = refreshState is SearchStatus.Loading && cachedCharacters.isEmpty()
        val errorMessage = (refreshState as? SearchStatus.Error)?.message

        val visibleCharacters = when (filter) {
            CharacterFilter.ALL -> cachedCharacters
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
            errorMessage = errorMessage,
            showEmptyState = refreshState is SearchStatus.Success && visibleCharacters.isEmpty()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CharactersUiState()
    )

    fun onQueryChange(query: String) {
        queryFlow.value = query
    }

    fun onFilterChange(filter: CharacterFilter) {
        filterFlow.value = filter
    }

    fun onRefresh() {
        viewModelScope.launch {
            refreshTrigger.emit(true)
        }
    }

    fun onToggleFavourite(id: String) {
        viewModelScope.launch {
            val currentFavs = favouritesFlow.value
            val isFavourite = currentFavs.any { it.id == id }

            if (isFavourite) {
                repository.removeFavorite(id)
            } else {
                val fromCache = repository.observeCharacter(id).first()
                if (fromCache != null) {
                    repository.addFavorite(fromCache)
                    return@launch
                }

                runCatching { repository.refreshCharacterById(id, force = true) }
                repository.observeCharacter(id).first()?.let { repository.addFavorite(it) }
            }
        }
    }

    private var detailJob: kotlinx.coroutines.Job? = null

    fun loadCharacter(id: String) {
        detailJob?.cancel()
        detailJob = viewModelScope.launch {
            _detailState.value = DetailState(isLoading = true)

            val cached = repository.observeCharacter(id).first()
            if (cached != null) {
                _detailState.value = DetailState(character = cached, isLoading = false, error = null)
                runCatching { recentRepository.addView(cached) }
            }

            val refreshResult = runCatching { repository.refreshCharacterById(id, force = false) }
            refreshResult.exceptionOrNull()?.let { e ->
                if (cached == null) {
                    _detailState.value = DetailState(character = null, isLoading = false, error = mapErrorToMessage(e, "Failed to load character"))
                }
            }

            repository.observeCharacter(id).collect { updated ->
                if (updated != null) {
                    _detailState.value = DetailState(character = updated, isLoading = false, error = null)
                } else if (cached == null) {
                    _detailState.value = DetailState(character = null, isLoading = false, error = "Character not found")
                }
            }
        }
    }

    fun clearDetailState() {
        _detailState.value = DetailState()
        detailJob?.cancel()
        detailJob = null
    }

    sealed class SearchStatus {
        data object Idle : SearchStatus()
        data object Loading : SearchStatus()
        data object Success : SearchStatus()
        data class Error(val message: String) : SearchStatus()
    }

    data class DetailState(
        val character: Character? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private fun mapErrorToMessage(e: Throwable, fallback: String): String {
        return when (e) {
            is java.io.IOException -> "Network error. Check your connection and try again."
            is retrofit2.HttpException -> "Server error (${e.code()}). Please try again."
            else -> e.message?.takeIf { it.isNotBlank() } ?: fallback
        }
    }
}