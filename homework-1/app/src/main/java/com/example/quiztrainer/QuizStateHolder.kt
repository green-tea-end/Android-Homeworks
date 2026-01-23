package com.example.quiztrainer

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class QuizUiState(
    val currentQuestionIndex: Int = 0,
    val selectedAnswerIndex: Int? = null,
    val score: Int = 0,
    val isQuizFinished: Boolean = false,
    val isQuizStarted: Boolean = false
)

class QuizStateHolder : ViewModel() {

    var uiState by mutableStateOf(QuizUiState())
        private set

    private val questions = QuizRepository.questions

    // Начать квиз
    fun startQuiz() {
        uiState = uiState.copy(
            isQuizStarted = true,
            currentQuestionIndex = 0,
            score = 0,
            isQuizFinished = false,
            selectedAnswerIndex = null
        )
    }

    // Получить текущий вопрос
    fun currentQuestion(): Question? {
        return questions.getOrNull(uiState.currentQuestionIndex)
    }

    // Выбрать ответ
    fun selectAnswer(index: Int) {
        uiState = uiState.copy(
            selectedAnswerIndex = index
        )
    }

    // Перейти к следующему вопросу
    fun nextQuestion() {
        val currentState = uiState
        val currentQuestion = currentQuestion()

        // Проверить правильность ответа
        val isCorrect = currentQuestion?.correctAnswerIndex == currentState.selectedAnswerIndex
        val newScore = if (isCorrect) currentState.score + 1 else currentState.score

        val nextIndex = currentState.currentQuestionIndex + 1

        if (nextIndex < questions.size) {
            uiState = currentState.copy(
                currentQuestionIndex = nextIndex,
                selectedAnswerIndex = null,
                score = newScore
            )
        } else {
            // Квиз завершен
            uiState = currentState.copy(
                isQuizFinished = true,
                score = newScore
            )
        }
    }

    // Начать заново
    fun restartQuiz() {
        uiState = QuizUiState()
    }

    // Получить итоговый процент
    fun getPercentage(): Int {
        return if (questions.isNotEmpty()) {
            (uiState.score * 100) / questions.size
        } else {
            0
        }
    }

    // Получить комментарий по результату
    fun getResultComment(): String {
        val percentage = getPercentage()
        return when {
            percentage < 50 -> "Нужно повторить материал!"
            percentage <= 80 -> "Хороший результат!"
            else -> "Отлично! Вы знаток Kotlin!"
        }
    }
}