package com.example.homework_3.ui.screen

import java.text.DateFormat
import java.util.Date
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.homework_3.data.local.RecentViewEntity
import com.example.homework_3.ui.viewmodel.RecentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentScreen(
    viewModel: RecentViewModel,
    onOpenCharacter: (String) -> Unit,
    onBack: () -> Unit,
) {
    val recent by viewModel.recent.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recent") },
                navigationIcon = { Button(onClick = onBack) { Text("Back") } },
                actions = {
                    Button(onClick = viewModel::clear, enabled = recent.isNotEmpty()) {
                        Text("Clear")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (recent.isEmpty()) {
                Text("No recent views yet.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items = recent, key = { it.characterId }) { item ->
                        RecentRow(
                            item = item,
                            onClick = { onOpenCharacter(item.characterId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentRow(
    item: RecentViewEntity,
    onClick: () -> Unit,
) {
    val formattedTime = DateFormat.getDateTimeInstance().format(Date(item.viewedAt))
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        headlineContent = { Text(item.characterName) },
        supportingContent = { Text(formattedTime) },
        trailingContent = { Button(onClick = onClick) { Text("Open") } },
    )
}

