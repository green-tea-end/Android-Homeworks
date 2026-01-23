package com.example.homework_2.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    onStartClick: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("SplitMate") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Делим счёт",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Простой калькулятор для разделения счёта",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onStartClick,
                modifier = Modifier.width(200.dp)
            ) {
                Text("Начать")
            }
        }
    }
}