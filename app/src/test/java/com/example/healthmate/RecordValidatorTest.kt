package com.example.healthmate

import com.example.healthmate.util.RecordValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class RecordValidatorTest {

    private val fixedToday = LocalDate.of(2026, 8, 18)

    @Test
    fun validInput_returnsIsValidTrue() {
        val result = RecordValidator.validate(
            dateInput = "2026-08-18",
            stepsInput = "7850",
            caloriesInput = "420",
            waterInput = "2000",
            today = fixedToday
        )
        assertTrue(result.isValid)
        assertNull(result.dateError)
        assertNull(result.stepsError)
        assertNull(result.caloriesError)
        assertNull(result.waterError)
    }

    @Test
    fun emptyFields_returnsValidationErrorsForAllFields() {
        val result = RecordValidator.validate(
            dateInput = "",
            stepsInput = "",
            caloriesInput = "",
            waterInput = "",
            today = fixedToday
        )
        assertFalse(result.isValid)
        assertNotNull(result.dateError)
        assertNotNull(result.stepsError)
        assertNotNull(result.caloriesError)
        assertNotNull(result.waterError)
    }

    @Test
    fun futureDate_returnsDateError() {
        val result = RecordValidator.validate(
            dateInput = "2026-08-19",
            stepsInput = "5000",
            caloriesInput = "300",
            waterInput = "1500",
            today = fixedToday
        )
        assertFalse(result.isValid)
        assertEquals("Date cannot be in the future", result.dateError)
    }

    @Test
    fun invalidDateFormat_returnsDateError() {
        val result = RecordValidator.validate(
            dateInput = "18/08/2026",
            stepsInput = "5000",
            caloriesInput = "300",
            waterInput = "1500",
            today = fixedToday
        )
        assertFalse(result.isValid)
        assertNotNull(result.dateError)
    }

    @Test
    fun negativeSteps_returnsStepsError() {
        val result = RecordValidator.validate(
            dateInput = "2026-08-18",
            stepsInput = "-150",
            caloriesInput = "300",
            waterInput = "1500",
            today = fixedToday
        )
        assertFalse(result.isValid)
        assertNotNull(result.stepsError)
    }

    @Test
    fun nonNumericCalories_returnsCaloriesError() {
        val result = RecordValidator.validate(
            dateInput = "2026-08-18",
            stepsInput = "5000",
            caloriesInput = "abc",
            waterInput = "1500",
            today = fixedToday
        )
        assertFalse(result.isValid)
        assertNotNull(result.caloriesError)
    }

    @Test
    fun waterExceedingLimit_returnsWaterError() {
        val result = RecordValidator.validate(
            dateInput = "2026-08-18",
            stepsInput = "5000",
            caloriesInput = "300",
            waterInput = "20000",
            today = fixedToday
        )
        assertFalse(result.isValid)
        assertNotNull(result.waterError)
    }
}