package com.example.homework_3.ui.viewmodel

import com.example.homework_3.model.Character
import com.example.homework_3.model.CharacterFilter

data class CharactersUiState(
    val query: String = "",
    val filter: CharacterFilter = CharacterFilter.ALL,
    val favourites: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedCharacter: Character? = null,
    val isLoadingDetail: Boolean = false,
    val errorDetail: String? = null
)