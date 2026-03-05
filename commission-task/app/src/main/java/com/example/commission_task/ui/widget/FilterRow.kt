package com.example.commission_task.ui.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.commission_task.model.CharacterFilter

@Composable
fun FilterRow(
    filter: CharacterFilter,
    onFilterChange: (CharacterFilter) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TextButton(
            onClick = { onFilterChange(CharacterFilter.ALL) },
            enabled = filter != CharacterFilter.ALL
        ) {
            Text(if (filter == CharacterFilter.ALL) "• All" else "All")
        }

        TextButton(
            onClick = { onFilterChange(CharacterFilter.FAVOURITES) },
            enabled = filter != CharacterFilter.FAVOURITES
        ) {
            Text(if (filter == CharacterFilter.FAVOURITES) "• Favourites" else "Favourites")
        }
    }
}
