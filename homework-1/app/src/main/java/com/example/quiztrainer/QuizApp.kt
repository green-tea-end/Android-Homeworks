package com.example.quiztrainer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun QuizApp() {
    val holder = remember { QuizStateHolder() }
    val state = holder.uiState

    if (!state.isQuizStarted) {
        WelcomeScreen(
            onStartClick = { holder.startQuiz() }
        )
    }
<<<<<<< Updated upstream

    else if (state.isQuizFinished) {
        ResultScreen(
            score = state.score,
            total = QuizRepository.questions.size,
            percentage = holder.getPercentage(),
            comment = holder.getResultComment(),
            onRestart = {
                holder.restartQuiz()
            }
        )
    }

    else {
        val currentQuestion = holder.currentQuestion()
=======
    // UI получает все данные из state
    else if (state.isQuizFinished) {
        ResultScreen(
            score = state.score,
            total = state.totalQuestions,
            percentage = state.percentage,
            comment = state.resultComment,
            onRestart = { holder.restartQuiz() }
        )
    }
    else {
        val currentQuestion = state.currentQuestion
>>>>>>> Stashed changes
        if (currentQuestion != null) {
            QuestionScreen(
                question = currentQuestion,
                currentIndex = state.currentQuestionIndex,
<<<<<<< Updated upstream
                totalQuestions = QuizRepository.questions.size,
                selectedAnswerIndex = state.selectedAnswerIndex,
=======
                totalQuestions = state.totalQuestions,
                selectedAnswerIndex = state.selectedAnswerIndex,
                progress = state.progress,
                buttonText = state.buttonText,
>>>>>>> Stashed changes
                onAnswerSelected = { index -> holder.selectAnswer(index) },
                onNextClicked = { holder.nextQuestion() },
                isNextEnabled = state.selectedAnswerIndex != null
            )
        }
    }
}