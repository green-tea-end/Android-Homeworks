package com.example.quiztrainer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun QuizApp() {
    val holder = remember { QuizStateHolder() }
    val state = holder.uiState

    when {
        !state.isQuizStarted -> {
            WelcomeScreen(
                onStartClick = { holder.startQuiz() }
            )
        }
        state.isQuizFinished -> {
            ResultScreen(
                score = state.score,
                total = state.totalQuestions,
                percentage = state.percentage,
                comment = state.resultComment,
                onRestart = { holder.restartQuiz() }
            )
        }
        else -> {
            val currentQuestion = state.currentQuestion
            if (currentQuestion != null) {
                QuestionScreen(
                    question = currentQuestion,
                    currentIndex = state.currentQuestionIndex,
                    totalQuestions = state.totalQuestions,
                    selectedAnswerIndex = state.selectedAnswerIndex,
                    onAnswerSelected = { index -> holder.selectAnswer(index) },
                    onNextClicked = { holder.nextQuestion() },
                    isNextEnabled = state.selectedAnswerIndex != null
                )
            }
        }
    }
}