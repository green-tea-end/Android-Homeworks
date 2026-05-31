package com.example.homework_3.ui.viewmodel

import com.example.homework_3.data.settings.BackgroundRefreshInterval
import com.example.homework_3.data.settings.CacheTtlPreset
import com.example.homework_3.data.settings.ThemeMode

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val cacheTtlPreset: CacheTtlPreset = CacheTtlPreset.SIX_HOURS,
    val isBackgroundRefreshEnabled: Boolean = false,
    val isBackgroundRefreshWifiOnly: Boolean = true,
    val backgroundRefreshInterval: BackgroundRefreshInterval = BackgroundRefreshInterval.TWELVE_HOURS,
    val lastBackgroundRefreshSuccessLabel: String = "Never",
)
