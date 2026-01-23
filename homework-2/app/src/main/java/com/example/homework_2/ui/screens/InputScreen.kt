package com.example.homework_2.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.homework_2.ui.viewmodel.SplitUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(
    state: SplitUiState,
    onTotalChange: (String) -> Unit,
    onPeopleChange: (String) -> Unit,
    onCalculate: () -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Введите данные") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.totalInput,
                onValueChange = onTotalChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Сумма счёта") },
                singleLine = true
            )

            OutlinedTextField(
                value = state.peopleInput,
                onValueChange = onPeopleChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Количество людей") },
                singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onCalculate,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = state.totalInput.isNotEmpty() &&
                        state.peopleInput.isNotEmpty() &&
                        state.totalInput.toDoubleOrNull() != null &&
                        state.totalInput.toDouble() > 0 &&
                        state.peopleInput.toIntOrNull() != null &&
                        state.peopleInput.toInt() > 0
            ) {
                Text("Рассчитать")
            }
        }
    }
}