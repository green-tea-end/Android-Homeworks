package com.example.homework_3.data.settings

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val themeMode: Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)

    val cacheTtlPreset: Flow<CacheTtlPreset>
    suspend fun setCacheTtlPreset(preset: CacheTtlPreset)

    val isBackgroundRefreshEnabled: Flow<Boolean>
    suspend fun setBackgroundRefreshEnabled(enabled: Boolean)

    val isBackgroundRefreshWifiOnly: Flow<Boolean>
    suspend fun setBackgroundRefreshWifiOnly(wifiOnly: Boolean)

    val backgroundRefreshInterval: Flow<BackgroundRefreshInterval>
    suspend fun setBackgroundRefreshInterval(interval: BackgroundRefreshInterval)

    val lastBackgroundRefreshSuccessAt: Flow<Long?>
    suspend fun setLastBackgroundRefreshSuccessAt(timestampMs: Long)
}

