package com.example.homework_3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework_3.data.recent.RecentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RecentViewModel @Inject constructor(
    private val recentRepository: RecentRepository,
) : ViewModel() {

    val recent: StateFlow<List<RecentItem>> =
        recentRepository.observeRecent()
            .map { entries ->
                entries.map { entry ->
                    RecentItem(
                        characterId = entry.characterId,
                        characterName = entry.characterName,
                        viewedAtLabel = DateFormat.getDateTimeInstance().format(Date(entry.viewedAt)),
                    )
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun clear() {
        viewModelScope.launch {
            recentRepository.clear()
        }
    }
}
