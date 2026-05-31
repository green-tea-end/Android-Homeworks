package com.example.homework_3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework_3.data.settings.BackgroundRefreshInterval
import com.example.homework_3.data.settings.CacheTtlPreset
import com.example.homework_3.data.settings.SettingsRepository
import com.example.homework_3.data.settings.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        combine(
            settingsRepository.themeMode,
            settingsRepository.cacheTtlPreset,
            settingsRepository.isBackgroundRefreshEnabled,
            settingsRepository.isBackgroundRefreshWifiOnly,
            settingsRepository.backgroundRefreshInterval,
        ) { themeMode, cacheTtlPreset, bgEnabled, bgWifiOnly, bgInterval ->
            SettingsUiState(
                themeMode = themeMode,
                cacheTtlPreset = cacheTtlPreset,
                isBackgroundRefreshEnabled = bgEnabled,
                isBackgroundRefreshWifiOnly = bgWifiOnly,
                backgroundRefreshInterval = bgInterval,
            )
        },
        settingsRepository.lastBackgroundRefreshSuccessAt,
    ) { state, lastBgSuccessAt ->
        state.copy(lastBackgroundRefreshSuccessLabel = formatLastSync(lastBgSuccessAt))
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        SettingsUiState(),
    )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    fun setCacheTtlPreset(preset: CacheTtlPreset) {
        viewModelScope.launch { settingsRepository.setCacheTtlPreset(preset) }
    }

    fun setBackgroundRefreshEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setBackgroundRefreshEnabled(enabled) }
    }

    fun setBackgroundRefreshWifiOnly(wifiOnly: Boolean) {
        viewModelScope.launch { settingsRepository.setBackgroundRefreshWifiOnly(wifiOnly) }
    }

    fun setBackgroundRefreshInterval(interval: BackgroundRefreshInterval) {
        viewModelScope.launch { settingsRepository.setBackgroundRefreshInterval(interval) }
    }

    private fun formatLastSync(timestampMs: Long?): String {
        return timestampMs?.let {
            DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(it))
        } ?: "Never"
    }
}
