package com.example.commission_task.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.commission_task.model.Character
import com.example.commission_task.ui.viewmodel.CharactersUiState
import com.example.commission_task.ui.widget.CharacterCard
import com.example.commission_task.ui.widget.FilterRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.background

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CharacterListScreen(
    state: CharactersUiState,
    characters: List<Character>,
    onSearchChange: (String) -> Unit,
    onFilterChange: (com.example.commission_task.model.CharacterFilter) -> Unit,
    onToggleFavourite: (String) -> Unit,
    onRefresh: () -> Unit,
    onCharacterClick: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Star Wars Characters") },
                actions = {
                    Button(onClick = onRefresh) {
                        Text("Refresh")
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
            OutlinedTextField(
                value = state.query,
                onValueChange = onSearchChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search characters by name") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            FilterRow(
                filter = state.filter,
                onFilterChange = onFilterChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                state.isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading characters from a galaxy far, far away...")
                    }
                }

                state.errorMessage != null -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Error: ${state.errorMessage}")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onRefresh) {
                            Text("Retry")
                        }
                    }
                }

                characters.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No characters found. The Force is not with us.")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onRefresh) {
                            Text("Try Again")
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Перебираем группы (по буквам)
                        state.groupedCharacters.forEach { (header, charactersInGroup) ->
                            // Липкий заголовок для группы
                            stickyHeader {
                                Text(
                                    text = header.toString(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(8.dp),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            // Элементы в группе
                            items(
                                items = charactersInGroup,
                                key = { it.id }
                            ) { character ->
                                CharacterCard(
                                    character = character,
                                    isFavourite = character.id in state.favourites,
                                    onToggleFavourite = { onToggleFavourite(character.id) },
                                    onClick = { onCharacterClick(character.id) }
                                )
                            }
                        }
                    }
                }
            }

            // Добавить VariantCode внизу экрана
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Variant: SWAPI-PEOPLE-MOD_A1_STICKY_HEADERS",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
