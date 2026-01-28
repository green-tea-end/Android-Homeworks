package com.example.quiztrainer

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

<<<<<<< Updated upstream
=======
// Cостояние теперь содержит все необходимые данные
>>>>>>> Stashed changes
data class QuizUiState(
    val currentQuestionIndex: Int = 0,
    val selectedAnswerIndex: Int? = null,
    val score: Int = 0,
<<<<<<< Updated upstream
=======
    val totalQuestions: Int = 0,
    val currentQuestion: Question? = null,
    val progress: Float = 0f,
    val buttonText: String = "Дальше",
    val percentage: Int = 0,
    val resultComment: String = "",
>>>>>>> Stashed changes
    val isQuizFinished: Boolean = false,
    val isQuizStarted: Boolean = false
)

class QuizStateHolder : ViewModel() {

    var uiState by mutableStateOf(QuizUiState())
        private set

    private val questions = QuizRepository.questions

<<<<<<< Updated upstream
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
=======
    init {
        // Инициализация состояния при создании
        updateUIState()
    }

    // Начать квиз
    fun startQuiz() {
        uiState = QuizUiState(
            isQuizStarted = true,
            totalQuestions = questions.size,
            currentQuestion = questions.firstOrNull(),
            currentQuestionIndex = 0,
            score = 0,
            selectedAnswerIndex = null,
            isQuizFinished = false
        )
        updateUIState()
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
        val currentQuestion = currentQuestion()

        // Проверить правильность ответа
        val isCorrect = currentQuestion?.correctAnswerIndex == currentState.selectedAnswerIndex
=======
        val currentQuestion = currentState.currentQuestion
        val selectedAnswer = currentState.selectedAnswerIndex

        // Явная проверка null, если что-то не то - выходим из метода
        if (currentQuestion == null || selectedAnswer == null) {
            return
        }

        // Проверить правильность ответа
        val isCorrect = currentQuestion.correctAnswerIndex == selectedAnswer
>>>>>>> Stashed changes
        val newScore = if (isCorrect) currentState.score + 1 else currentState.score

        val nextIndex = currentState.currentQuestionIndex + 1

        if (nextIndex < questions.size) {
            uiState = currentState.copy(
                currentQuestionIndex = nextIndex,
                selectedAnswerIndex = null,
<<<<<<< Updated upstream
                score = newScore
=======
                score = newScore,
                currentQuestion = questions.getOrNull(nextIndex)
>>>>>>> Stashed changes
            )
        } else {
            // Квиз завершен
            uiState = currentState.copy(
                isQuizFinished = true,
<<<<<<< Updated upstream
                score = newScore
            )
        }
=======
                score = newScore,
                percentage = calculatePercentage(),
                resultComment = getResultComment()
            )
        }

        updateUIState()
    }

    // Обновить вычисляемые поля UI состояния
    private fun updateUIState() {
        val total = uiState.totalQuestions
        val currentIndex = uiState.currentQuestionIndex

        val progress = if (total > 0) {
            (currentIndex + 1).toFloat() / total
        } else 0f

        val buttonText = if (currentIndex < total - 1) {
            "Дальше"
        } else {
            "Завершить"
        }

        uiState = uiState.copy(
            progress = progress,
            buttonText = buttonText
        )
>>>>>>> Stashed changes
    }

    // Начать заново
    fun restartQuiz() {
<<<<<<< Updated upstream
        uiState = QuizUiState()
    }

    // Получить итоговый процент
    fun getPercentage(): Int {
=======
        val total = questions.size
        uiState = QuizUiState(
            totalQuestions = total,
            currentQuestion = questions.firstOrNull(),
            progress = if (total > 0) 1f / total else 0f,
            buttonText = if (total > 1) "Дальше" else "Завершить"
        )
    }

    // Получить итоговый процент
    private fun calculatePercentage(): Int {
>>>>>>> Stashed changes
        return if (questions.isNotEmpty()) {
            (uiState.score * 100) / questions.size
        } else {
            0
        }
    }

    // Получить комментарий по результату
<<<<<<< Updated upstream
    fun getResultComment(): String {
        val percentage = getPercentage()
=======
    private fun getResultComment(): String {
        val percentage = calculatePercentage()
>>>>>>> Stashed changes
        return when {
            percentage < 50 -> "Нужно повторить материал!"
            percentage <= 80 -> "Хороший результат!"
            else -> "Отлично! Вы знаток Kotlin!"
        }
    }
}