package com.example.commission_task.ui.viewmodel

import com.example.commission_task.model.Character
import com.example.commission_task.model.CharacterFilter

data class CharactersUiState(
    val query: String = "",
    val filter: CharacterFilter = CharacterFilter.ALL,
    val favourites: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentPage: Int = 1,
    val selectedCharacter: Character? = null,
    val isLoadingDetail: Boolean = false,
    val errorDetail: String? = null,
    val groupedCharacters: Map<Char, List<Character>> = emptyMap()
)
