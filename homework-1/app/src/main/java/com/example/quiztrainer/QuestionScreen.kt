package com.example.quiztrainer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

<<<<<<< Updated upstream
=======
// Прогресс и текст кнопки вычисляются в StateHolder и передаются сюда
>>>>>>> Stashed changes
@Composable
fun QuestionScreen(
    question: Question,
    currentIndex: Int,
    totalQuestions: Int,
    selectedAnswerIndex: Int?,
<<<<<<< Updated upstream
=======
    progress: Float,
    buttonText: String,
>>>>>>> Stashed changes
    onAnswerSelected: (Int) -> Unit,
    onNextClicked: () -> Unit,
    isNextEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Заголовок с номером вопроса
        Text(
            text = "Вопрос ${currentIndex + 1} из $totalQuestions",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Прогресс бар
<<<<<<< Updated upstream
        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / totalQuestions },
=======
        // Получаем готовое значение progress из параметров
        LinearProgressIndicator(
            progress = { progress },
>>>>>>> Stashed changes
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Текст вопроса
        Text(
            text = question.text,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Варианты ответов
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            question.answers.forEachIndexed { index, answer ->
                // Карточка ответа
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (index == selectedAnswerIndex),
                            onClick = { onAnswerSelected(index) }
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (index == selectedAnswerIndex) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    )
                ) {
                    Text(
                        text = answer,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

<<<<<<< Updated upstream
        // Кнопка
=======
        // Кнопка получает готовый текст buttonText из параметров
>>>>>>> Stashed changes
        Button(
            onClick = onNextClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = isNextEnabled
        ) {
            Text(
<<<<<<< Updated upstream
                text = if (currentIndex < totalQuestions - 1) "Дальше" else "Завершить",
=======
                text = buttonText,
>>>>>>> Stashed changes
                fontSize = 18.sp
            )
        }
    }
}