package com.example.quiztrainer

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class QuizUiState(
    val currentQuestionIndex: Int = 0,
    val selectedAnswerIndex: Int? = null,
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val currentQuestion: Question? = null,
    val isQuizFinished: Boolean = false,
    val isQuizStarted: Boolean = false,
    val percentage: Int = 0,
    val resultComment: String = ""
)

class QuizStateHolder : ViewModel() {

    var uiState by mutableStateOf(
        QuizUiState(
            totalQuestions = QuizRepository.questions.size
        )
    )
        private set

    private val questions = QuizRepository.questions

    // Начать квиз
    fun startQuiz() {
        uiState = QuizUiState(
            isQuizStarted = true,
            currentQuestionIndex = 0,
            totalQuestions = questions.size,
            currentQuestion = questions.firstOrNull(),
            score = 0,
            isQuizFinished = false,
            selectedAnswerIndex = null
        )
    }

    // Выбрать ответ
    fun selectAnswer(index: Int) {
        uiState = uiState.copy(selectedAnswerIndex = index)
    }

    // Перейти к следующему вопросу
    fun nextQuestion() {
        val currentState = uiState
        val currentQuestion = currentState.currentQuestion
        val selectedAnswer = currentState.selectedAnswerIndex

        // Проверяем, что текущий вопрос и выбранный ответ не null
        if (currentQuestion == null || selectedAnswer == null) {
            return
        }

        // Проверить правильность ответа
        val isCorrect = currentQuestion.correctAnswerIndex == selectedAnswer
        val newScore = if (isCorrect) currentState.score + 1 else currentState.score
        val nextIndex = currentState.currentQuestionIndex + 1

        if (nextIndex < questions.size) {
            uiState = currentState.copy(
                currentQuestionIndex = nextIndex,
                selectedAnswerIndex = null,
                score = newScore,
                currentQuestion = questions.getOrNull(nextIndex)
            )
        } else {
            // Квиз завершен
            val percentage = if (questions.isNotEmpty()) {
                (newScore * 100) / questions.size
            } else 0

            val comment = when {
                percentage < 50 -> "Нужно повторить материал!"
                percentage <= 80 -> "Хороший результат!"
                else -> "Отлично! Вы знаток Kotlin!"
            }

            uiState = currentState.copy(
                isQuizFinished = true,
                score = newScore,
                percentage = percentage,
                resultComment = comment
            )
        }
    }

    // Начать заново
    fun restartQuiz() {
        uiState = QuizUiState(
            totalQuestions = questions.size
        )
    }
}