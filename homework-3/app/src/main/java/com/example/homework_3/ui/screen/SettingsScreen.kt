package com.example.homework_3.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.homework_3.data.settings.ThemeMode
import com.example.homework_3.ui.viewmodel.SettingsViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.homework_3.data.settings.CacheTtlPreset
import com.example.homework_3.data.settings.BackgroundRefreshInterval
import java.text.DateFormat
import java.util.Date
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val cacheTtlPreset by viewModel.cacheTtlPreset.collectAsState()
    val isBackgroundRefreshEnabled by viewModel.isBackgroundRefreshEnabled.collectAsState()
    val isBackgroundRefreshWifiOnly by viewModel.isBackgroundRefreshWifiOnly.collectAsState()
    val backgroundRefreshInterval by viewModel.backgroundRefreshInterval.collectAsState()
    val lastBgSuccessAt by viewModel.lastBackgroundRefreshSuccessAt.collectAsState()
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
                selected = themeMode == ThemeMode.SYSTEM,
                onClick = { viewModel.setThemeMode(ThemeMode.SYSTEM) }
            )
            ThemeModeOptionRow(
                title = "Light",
                description = "Always Light",
                selected = themeMode == ThemeMode.LIGHT,
                onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) }
            )
            ThemeModeOptionRow(
                title = "Dark",
                description = "Always Dark",
                selected = themeMode == ThemeMode.DARK,
                onClick = { viewModel.setThemeMode(ThemeMode.DARK) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Cache freshness (TTL)")
            Spacer(modifier = Modifier.height(8.dp))

            SettingsOptionRow(
                title = "1 hour",
                description = "More network requests, fresher cache",
                selected = cacheTtlPreset == CacheTtlPreset.ONE_HOUR,
                onClick = { viewModel.setCacheTtlPreset(CacheTtlPreset.ONE_HOUR) }
            )
            SettingsOptionRow(
                title = "6 hours",
                description = "Balanced default",
                selected = cacheTtlPreset == CacheTtlPreset.SIX_HOURS,
                onClick = { viewModel.setCacheTtlPreset(CacheTtlPreset.SIX_HOURS) }
            )
            SettingsOptionRow(
                title = "24 hours",
                description = "Fewer requests, cache can be stale",
                selected = cacheTtlPreset == CacheTtlPreset.TWENTY_FOUR_HOURS,
                onClick = { viewModel.setCacheTtlPreset(CacheTtlPreset.TWENTY_FOUR_HOURS) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Background refresh")
            Spacer(modifier = Modifier.height(8.dp))

            SettingsToggleRow(
                title = "Enable background refresh",
                description = "Auto-update cached data when possible",
                checked = isBackgroundRefreshEnabled,
                onCheckedChange = viewModel::setBackgroundRefreshEnabled,
            )

            SettingsToggleRow(
                title = "Wi‑Fi only",
                description = "Avoid using mobile data",
                checked = isBackgroundRefreshWifiOnly,
                enabled = isBackgroundRefreshEnabled,
                onCheckedChange = viewModel::setBackgroundRefreshWifiOnly,
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text("Refresh interval")
            Spacer(modifier = Modifier.height(8.dp))

            SettingsOptionRow(
                title = "6 hours",
                description = "More frequent updates",
                selected = backgroundRefreshInterval == BackgroundRefreshInterval.SIX_HOURS,
                onClick = { viewModel.setBackgroundRefreshInterval(BackgroundRefreshInterval.SIX_HOURS) }
            )
            SettingsOptionRow(
                title = "12 hours",
                description = "Recommended default",
                selected = backgroundRefreshInterval == BackgroundRefreshInterval.TWELVE_HOURS,
                onClick = { viewModel.setBackgroundRefreshInterval(BackgroundRefreshInterval.TWELVE_HOURS) }
            )
            SettingsOptionRow(
                title = "24 hours",
                description = "Least frequent updates",
                selected = backgroundRefreshInterval == BackgroundRefreshInterval.TWENTY_FOUR_HOURS,
                onClick = { viewModel.setBackgroundRefreshInterval(BackgroundRefreshInterval.TWENTY_FOUR_HOURS) }
            )

            Spacer(modifier = Modifier.height(12.dp))
            val lastSyncText = lastBgSuccessAt?.let {
                DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(it))
            } ?: "Never"
            Text("Last successful background refresh: $lastSyncText")
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

