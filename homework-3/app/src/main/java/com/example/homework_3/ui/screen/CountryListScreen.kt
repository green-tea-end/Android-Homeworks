package com.example.homework_3.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.homework_3.model.Country
import com.example.homework_3.model.CountryFilter
import com.example.homework_3.ui.viewmodel.CountriesUiState
import com.example.homework_3.ui.widget.CountryCard
import com.example.homework_3.ui.widget.FilterRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryListScreen(
    state: CountriesUiState,
    countries: List<Country>,
    onSearchChange: (String) -> Unit,
    onFilterChange: (CountryFilter) -> Unit,
    onToggleFavourite: (String) -> Unit,
    onCountryClick: (String) -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Countries Browser",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    ) { innerPadding ->
        // Используем remember для оптимизации
        val modifier = remember(innerPadding) {
            Modifier
                .padding(innerPadding)
                .padding(16.dp)
        }

        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Заголовок поиска
            item {
                Column {
                    OutlinedTextField(
                        value = state.query,
                        onValueChange = onSearchChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Search country...") },
                        singleLine = true,
                        trailingIcon = {
                            if (state.query.isNotBlank()) {
                                IconButton(
                                    onClick = { onSearchChange("") },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, "Clear")
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Показываем ошибку если есть
                    state.errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    FilterRow(
                        filter = state.filter,
                        onFilterChange = onFilterChange
                    )
                }
            }

            // Индикатор загрузки
            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            // Пустой экран при старте
            if (!state.isLoading && countries.isEmpty() && state.query.isBlank()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillParentMaxHeight(0.7f)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Search for countries",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Try: usa, germany, france, japan",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Результаты поиска
            if (countries.isNotEmpty()) {
                items(
                    items = countries,
                    key = { it.id }
                ) { country ->
                    CountryCard(
                        country = country,
                        isFavourite = country.id in state.favourites,
                        onToggleFavourite = { onToggleFavourite(country.id) },
                        onClick = { onCountryClick(country.id) }
                    )
                }
            }

            // Сообщение "ничего не найдено"
            if (!state.isLoading && countries.isEmpty() && state.query.isNotBlank()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No countries found for '${state.query}'")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onRetry) {
                            Text("Try again")
                        }
                    }
                }
            }
        }
    }
}