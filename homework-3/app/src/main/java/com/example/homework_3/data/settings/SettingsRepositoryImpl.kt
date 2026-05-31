package com.example.homework_3.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val CACHE_TTL_PRESET = stringPreferencesKey("cache_ttl_preset")
        val BG_REFRESH_ENABLED = booleanPreferencesKey("bg_refresh_enabled")
        val BG_REFRESH_WIFI_ONLY = booleanPreferencesKey("bg_refresh_wifi_only")
        val BG_REFRESH_INTERVAL = stringPreferencesKey("bg_refresh_interval")
        val BG_LAST_SUCCESS_AT = longPreferencesKey("bg_last_success_at")
    }

    override val themeMode: Flow<ThemeMode> =
        dataStore.data
            .map { prefs ->
                when (prefs[Keys.THEME_MODE]) {
                    ThemeMode.LIGHT.name -> ThemeMode.LIGHT
                    ThemeMode.DARK.name -> ThemeMode.DARK
                    else -> ThemeMode.SYSTEM
                }
            }
            .distinctUntilChanged()

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode.name
        }
    }

    override val cacheTtlPreset: Flow<CacheTtlPreset> =
        dataStore.data
            .map { prefs ->
                when (prefs[Keys.CACHE_TTL_PRESET]) {
                    CacheTtlPreset.ONE_HOUR.name -> CacheTtlPreset.ONE_HOUR
                    CacheTtlPreset.TWENTY_FOUR_HOURS.name -> CacheTtlPreset.TWENTY_FOUR_HOURS
                    else -> CacheTtlPreset.SIX_HOURS
                }
            }
            .distinctUntilChanged()

    override suspend fun setCacheTtlPreset(preset: CacheTtlPreset) {
        dataStore.edit { prefs ->
            prefs[Keys.CACHE_TTL_PRESET] = preset.name
        }
    }

    override val isBackgroundRefreshEnabled: Flow<Boolean> =
        dataStore.data
            .map { prefs -> prefs[Keys.BG_REFRESH_ENABLED] ?: false }
            .distinctUntilChanged()

    override suspend fun setBackgroundRefreshEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.BG_REFRESH_ENABLED] = enabled
        }
    }

    override val isBackgroundRefreshWifiOnly: Flow<Boolean> =
        dataStore.data
            .map { prefs -> prefs[Keys.BG_REFRESH_WIFI_ONLY] ?: true }
            .distinctUntilChanged()

    override suspend fun setBackgroundRefreshWifiOnly(wifiOnly: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.BG_REFRESH_WIFI_ONLY] = wifiOnly
        }
    }

    override val backgroundRefreshInterval: Flow<BackgroundRefreshInterval> =
        dataStore.data
            .map { prefs ->
                when (prefs[Keys.BG_REFRESH_INTERVAL]) {
                    BackgroundRefreshInterval.SIX_HOURS.name -> BackgroundRefreshInterval.SIX_HOURS
                    BackgroundRefreshInterval.TWELVE_HOURS.name -> BackgroundRefreshInterval.TWELVE_HOURS
                    BackgroundRefreshInterval.TWENTY_FOUR_HOURS.name -> BackgroundRefreshInterval.TWENTY_FOUR_HOURS
                    else -> BackgroundRefreshInterval.TWELVE_HOURS
                }
            }
            .distinctUntilChanged()

    override suspend fun setBackgroundRefreshInterval(interval: BackgroundRefreshInterval) {
        dataStore.edit { prefs ->
            prefs[Keys.BG_REFRESH_INTERVAL] = interval.name
        }
    }

    override val lastBackgroundRefreshSuccessAt: Flow<Long?> =
        dataStore.data
            .map { prefs -> prefs[Keys.BG_LAST_SUCCESS_AT] }
            .distinctUntilChanged()

    override suspend fun setLastBackgroundRefreshSuccessAt(timestampMs: Long) {
        dataStore.edit { prefs ->
            prefs[Keys.BG_LAST_SUCCESS_AT] = timestampMs
        }
    }
}

