package com.example.homework_2.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.homework_2.model.Calculation

data class SplitUiState(
    val totalInput: String = "",
    val peopleInput: String = "",
    val calculations: List<Calculation> = emptyList(),
    val isInputValid: Boolean = false,
)

class SplitViewModel : ViewModel() {
    var uiState by mutableStateOf(SplitUiState())
        private set

    private fun updateInputValidity() {
        val total = uiState.totalInput.toDoubleOrNull()
        val people = uiState.peopleInput.toIntOrNull()
        val isValid = total != null && total > 0 && people != null && people > 0

        uiState = uiState.copy(isInputValid = isValid)
    }
    fun onTotalChange(total: String) {
        uiState = uiState.copy(totalInput = total)
        updateInputValidity()
    }

    fun onPeopleChange(people: String) {
        uiState = uiState.copy(peopleInput = people)
        updateInputValidity()
    }

    fun createCalculation(): Calculation? {
        val total = uiState.totalInput.toDoubleOrNull()
        val people = uiState.peopleInput.toIntOrNull()

        if (total == null || total <= 0 || people == null || people <= 0) {
            return null
        }

        val newCalculation = Calculation(
            totalAmount = total,
            peopleCount = people
        )

        val updatedCalculations = listOf(newCalculation) + uiState.calculations.take(4)

        uiState = uiState.copy(
            calculations = updatedCalculations
        )

        return newCalculation
    }

    fun calculationById(id: String): Calculation? {
        return uiState.calculations.firstOrNull { it.id == id }
    }

    fun resetForNewCalculation() {
        uiState = uiState.copy(
            totalInput = "",
            peopleInput = "",
            isInputValid = false
        )
    }
}