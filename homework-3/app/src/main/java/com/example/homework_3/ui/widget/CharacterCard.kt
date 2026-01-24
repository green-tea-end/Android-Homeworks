package com.example.homework_3.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.homework_3.model.Character

@Composable
fun CharacterCard(
    character: Character,
    isFavourite: Boolean,
    onToggleFavourite: () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = character.name, fontWeight = FontWeight.Bold)
            Text("Gender: ${character.gender}")
            Text("Birth year: ${character.birthYear}")
            Text("Height: ${character.height} cm, Mass: ${character.mass} kg")
        }
        Text(
            text = if (isFavourite) "★" else "☆",
            modifier = Modifier.clickable(onClick = onToggleFavourite)
        )
    }
}