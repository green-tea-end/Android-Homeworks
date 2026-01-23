package com.example.homework_2.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.homework_2.model.Calculation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    calculation: Calculation,
    onBackToEdit: () -> Unit,
    onNewCalculation: () -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Результат") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            Text(text = "Чаевые:", fontWeight = FontWeight.Bold)
            Text("${calculation.tipAmount} ₽ (${calculation.tipPercentage}%)")

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Итого с чаевыми:", fontWeight = FontWeight.Bold)
            Text("${calculation.totalWithTip} ₽")

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "На человека:", fontWeight = FontWeight.Bold)
            Text("${calculation.perPerson} ₽")

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Button(
                    onClick = onBackToEdit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Вернуться к редактированию")
                }

                OutlinedButton(
                    onClick = onNewCalculation,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Новый расчёт")
                }
            }
        }
    }
}