package com.example.homework_3.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.homework_3.data.settings.BackgroundRefreshInterval
import com.example.homework_3.data.settings.CacheTtlPreset
import com.example.homework_3.data.settings.ThemeMode
import com.example.homework_3.ui.viewmodel.SettingsUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onBack: () -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
    onCacheTtlPresetChange: (CacheTtlPreset) -> Unit,
    onBackgroundRefreshEnabledChange: (Boolean) -> Unit,
    onBackgroundRefreshWifiOnlyChange: (Boolean) -> Unit,
    onBackgroundRefreshIntervalChange: (BackgroundRefreshInterval) -> Unit,
) {
    val isSystemDark = isSystemInDarkTheme()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    Button(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Theme")
            Spacer(modifier = Modifier.height(8.dp))

            ThemeModeOptionRow(
                title = "System",
                description = if (isSystemDark) "Follow device (currently Dark)" else "Follow device (currently Light)",
                selected = uiState.themeMode == ThemeMode.SYSTEM,
                onClick = { onThemeModeChange(ThemeMode.SYSTEM) }
            )
            ThemeModeOptionRow(
                title = "Light",
                description = "Always Light",
                selected = uiState.themeMode == ThemeMode.LIGHT,
                onClick = { onThemeModeChange(ThemeMode.LIGHT) }
            )
            ThemeModeOptionRow(
                title = "Dark",
                description = "Always Dark",
                selected = uiState.themeMode == ThemeMode.DARK,
                onClick = { onThemeModeChange(ThemeMode.DARK) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Cache freshness (TTL)")
            Spacer(modifier = Modifier.height(8.dp))

            SettingsOptionRow(
                title = "1 hour",
                description = "More network requests, fresher cache",
                selected = uiState.cacheTtlPreset == CacheTtlPreset.ONE_HOUR,
                onClick = { onCacheTtlPresetChange(CacheTtlPreset.ONE_HOUR) }
            )
            SettingsOptionRow(
                title = "6 hours",
                description = "Balanced default",
                selected = uiState.cacheTtlPreset == CacheTtlPreset.SIX_HOURS,
                onClick = { onCacheTtlPresetChange(CacheTtlPreset.SIX_HOURS) }
            )
            SettingsOptionRow(
                title = "24 hours",
                description = "Fewer requests, cache can be stale",
                selected = uiState.cacheTtlPreset == CacheTtlPreset.TWENTY_FOUR_HOURS,
                onClick = { onCacheTtlPresetChange(CacheTtlPreset.TWENTY_FOUR_HOURS) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Background refresh")
            Spacer(modifier = Modifier.height(8.dp))

            SettingsToggleRow(
                title = "Enable background refresh",
                description = "Auto-update cached data when possible",
                checked = uiState.isBackgroundRefreshEnabled,
                onCheckedChange = onBackgroundRefreshEnabledChange,
            )

            SettingsToggleRow(
                title = "Wi‑Fi only",
                description = "Avoid using mobile data",
                checked = uiState.isBackgroundRefreshWifiOnly,
                enabled = uiState.isBackgroundRefreshEnabled,
                onCheckedChange = onBackgroundRefreshWifiOnlyChange,
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text("Refresh interval")
            Spacer(modifier = Modifier.height(8.dp))

            SettingsOptionRow(
                title = "6 hours",
                description = "More frequent updates",
                selected = uiState.backgroundRefreshInterval == BackgroundRefreshInterval.SIX_HOURS,
                onClick = { onBackgroundRefreshIntervalChange(BackgroundRefreshInterval.SIX_HOURS) }
            )
            SettingsOptionRow(
                title = "12 hours",
                description = "Recommended default",
                selected = uiState.backgroundRefreshInterval == BackgroundRefreshInterval.TWELVE_HOURS,
                onClick = { onBackgroundRefreshIntervalChange(BackgroundRefreshInterval.TWELVE_HOURS) }
            )
            SettingsOptionRow(
                title = "24 hours",
                description = "Least frequent updates",
                selected = uiState.backgroundRefreshInterval == BackgroundRefreshInterval.TWENTY_FOUR_HOURS,
                onClick = { onBackgroundRefreshIntervalChange(BackgroundRefreshInterval.TWENTY_FOUR_HOURS) }
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text("Last successful background refresh: ${uiState.lastBackgroundRefreshSuccessLabel}")
        }
    }
}

@Composable
private fun ThemeModeOptionRow(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    SettingsOptionRow(
        title = title,
        description = description,
        selected = selected,
        onClick = onClick,
    )
}

@Composable
private fun SettingsOptionRow(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        headlineContent = { Text(title) },
        supportingContent = { Text(description) },
        leadingContent = { RadioButton(selected = selected, onClick = onClick) },
    )
}

@Composable
private fun SettingsToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
) {
    ListItem(
        modifier = Modifier.fillMaxWidth(),
        headlineContent = { Text(title) },
        supportingContent = { Text(description) },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
            )
        },
    )
}
