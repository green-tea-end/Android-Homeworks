package com.example.homework_2.model

import java.util.UUID

data class Calculation(
    val id: String = UUID.randomUUID().toString(),
    val totalAmount: Double,
    val peopleCount: Int,
    val tipPercentage: Int = 10
) {
    val tipAmount: Double get() = totalAmount * tipPercentage / 100
    val totalWithTip: Double get() = totalAmount + tipAmount
    val perPerson: Double get() = if (peopleCount > 0) totalWithTip / peopleCount else 0.0
}