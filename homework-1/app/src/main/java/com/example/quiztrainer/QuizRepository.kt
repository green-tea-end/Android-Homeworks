package com.example.quiztrainer

object QuizRepository {
    val questions = listOf(
        Question(
            id = 1,
            text = "Какой тип данных в Kotlin используется для целых чисел?",
            answers = listOf("Int", "String", "Boolean", "Float"),
            correctAnswerIndex = 0
        ),
        Question(
            id = 2,
            text = "Как объявить неизменяемую переменную в Kotlin?",
            answers = listOf("val", "var", "let", "const"),
            correctAnswerIndex = 0
        ),
        Question(
            id = 3,
            text = "Что вернет выражение: 'Hello'.length?",
            answers = listOf("5", "6", "4", "Ошибку"),
            correctAnswerIndex = 0
        ),
        Question(
            id = 4,
            text = "Какой тип функции не возвращает значение?",
            answers = listOf("Unit", "Nothing", "Void", "Null"),
            correctAnswerIndex = 0
        ),
        Question(
            id = 5,
            text = "Как создать список в Kotlin?",
            answers = listOf("listOf()", "List()", "new List()", "ArrayList()"),
            correctAnswerIndex = 0
        )
    )
}