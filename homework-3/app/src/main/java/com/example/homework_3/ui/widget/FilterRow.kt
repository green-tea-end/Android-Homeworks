package com.example.homework_3.ui.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.homework_3.model.CountryFilter

@Composable
fun FilterRow(
    filter: CountryFilter,
    onFilterChange: (CountryFilter) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TextButton(
            onClick = { onFilterChange(CountryFilter.ALL) },
            enabled = filter != CountryFilter.ALL
        ) {
            Text(if (filter == CountryFilter.ALL) "• All" else "All")
        }
        TextButton(
            onClick = { onFilterChange(CountryFilter.FAVOURITES) },
            enabled = filter != CountryFilter.FAVOURITES
        ) {
            Text(if (filter == CountryFilter.FAVOURITES) "• Favourites" else "Favourites")
        }
    }
}