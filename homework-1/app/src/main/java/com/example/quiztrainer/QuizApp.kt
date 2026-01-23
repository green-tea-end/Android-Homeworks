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
        if (currentQuestion != null) {
            QuestionScreen(
                question = currentQuestion,
                currentIndex = state.currentQuestionIndex,
                totalQuestions = QuizRepository.questions.size,
                selectedAnswerIndex = state.selectedAnswerIndex,
                onAnswerSelected = { index -> holder.selectAnswer(index) },
                onNextClicked = { holder.nextQuestion() },
                isNextEnabled = state.selectedAnswerIndex != null
            )
        }
    }
}