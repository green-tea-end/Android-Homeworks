package com.example.homework_3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework_3.data.settings.SettingsRepository
import com.example.homework_3.data.settings.BackgroundRefreshInterval
import com.example.homework_3.data.settings.CacheTtlPreset
import com.example.homework_3.data.settings.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> =
        settingsRepository.themeMode
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemeMode.SYSTEM)

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }

    val cacheTtlPreset: StateFlow<CacheTtlPreset> =
        settingsRepository.cacheTtlPreset
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CacheTtlPreset.SIX_HOURS)

    fun setCacheTtlPreset(preset: CacheTtlPreset) {
        viewModelScope.launch {
            settingsRepository.setCacheTtlPreset(preset)
        }
    }

    val isBackgroundRefreshEnabled: StateFlow<Boolean> =
        settingsRepository.isBackgroundRefreshEnabled
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun setBackgroundRefreshEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setBackgroundRefreshEnabled(enabled)
        }
    }

    val isBackgroundRefreshWifiOnly: StateFlow<Boolean> =
        settingsRepository.isBackgroundRefreshWifiOnly
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    fun setBackgroundRefreshWifiOnly(wifiOnly: Boolean) {
        viewModelScope.launch {
            settingsRepository.setBackgroundRefreshWifiOnly(wifiOnly)
        }
    }

    val backgroundRefreshInterval: StateFlow<BackgroundRefreshInterval> =
        settingsRepository.backgroundRefreshInterval
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BackgroundRefreshInterval.TWELVE_HOURS)

    fun setBackgroundRefreshInterval(interval: BackgroundRefreshInterval) {
        viewModelScope.launch {
            settingsRepository.setBackgroundRefreshInterval(interval)
        }
    }

    val lastBackgroundRefreshSuccessAt: StateFlow<Long?> =
        settingsRepository.lastBackgroundRefreshSuccessAt
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}

