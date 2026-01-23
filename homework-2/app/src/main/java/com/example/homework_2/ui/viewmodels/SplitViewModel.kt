package com.example.homework_2.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.homework_2.model.Calculation

data class SplitUiState(
    val totalInput: String = "",
    val peopleInput: String = "",
    val calculations: List<Calculation> = emptyList()
)

class SplitViewModel : ViewModel() {
    var uiState by mutableStateOf(SplitUiState())
        private set

    fun onTotalChange(total: String) {
        uiState = uiState.copy(totalInput = total)
    }

    fun onPeopleChange(people: String) {
        uiState = uiState.copy(peopleInput = people)
    }

    val isInputValid: Boolean
        get() {
            val total = uiState.totalInput.toDoubleOrNull()
            val people = uiState.peopleInput.toIntOrNull()
            return total != null && total > 0 && people != null && people > 0
        }

    fun createCalculation(): Calculation {
        val total = uiState.totalInput.toDouble()
        val people = uiState.peopleInput.toInt()

        val newCalculation = Calculation(
            totalAmount = total,
            peopleCount = people
        )

        val updatedCalculations = listOf(newCalculation) + uiState.calculations.take(4)

        uiState = uiState.copy(
            calculations = updatedCalculations,
            totalInput = "",
            peopleInput = ""
        )

        return newCalculation
    }

    fun calculationById(id: String): Calculation? {
        return uiState.calculations.firstOrNull { it.id == id }
    }

    fun resetForNewCalculation() {
        uiState = SplitUiState()
    }
}