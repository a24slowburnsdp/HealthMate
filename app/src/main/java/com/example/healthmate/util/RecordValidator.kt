package com.example.healthmate.util

import java.time.LocalDate

data class ValidationResult(
    val isValid: Boolean,
    val dateError: String? = null,
    val stepsError: String? = null,
    val caloriesError: String? = null,
    val waterError: String? = null
)

object RecordValidator {

    fun validate(
        dateInput: String,
        stepsInput: String,
        caloriesInput: String,
        waterInput: String,
        today: LocalDate = LocalDate.now()
    ): ValidationResult {
        var dateError: String? = null
        var stepsError: String? = null
        var caloriesError: String? = null
        var waterError: String? = null

        // Format is year, month, date
        val trimmedDate = dateInput.trim()
        if (trimmedDate.isEmpty()) {
            dateError = "Please select a date"
        } else {
            try {
                val parsedDate = LocalDate.parse(trimmedDate)
                if (parsedDate.isAfter(today)) {
                    dateError = "Date cannot be in the future"
                }
            } catch (_: Exception) {
                dateError = "Use format YYYY-MM-DD (e.g. 2026-08-18)"
            }
        }

        // Validate steps
        val steps = stepsInput.trim().toIntOrNull()
        if (stepsInput.trim().isEmpty()) {
            stepsError = "Please enter steps walked"
        } else if (steps == null || steps < 0 || steps > 200_000) {
            stepsError = "Enter a whole number between 0 and 200,000"
        }

        // Validate calories
        val calories = caloriesInput.trim().toIntOrNull()
        if (caloriesInput.trim().isEmpty()) {
            caloriesError = "Please enter calories burned"
        } else if (calories == null || calories < 0 || calories > 20_000) {
            caloriesError = "Enter a whole number between 0 and 20,000"
        }

        // Validate water intake
        val water = waterInput.trim().toIntOrNull()
        if (waterInput.trim().isEmpty()) {
            waterError = "Please enter water intake in ml"
        } else if (water == null || water < 0 || water > 15_000) {
            waterError = "Enter a whole number between 0 and 15,000 ml"
        }

        val isValid = dateError == null &&
                stepsError == null &&
                caloriesError == null &&
                waterError == null

        return ValidationResult(
            isValid = isValid,
            dateError = dateError,
            stepsError = stepsError,
            caloriesError = caloriesError,
            waterError = waterError
        )
    }
}