package com.example.healthmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.healthmate.data.local.DailySummary
import com.example.healthmate.data.local.HealthRecord
import com.example.healthmate.data.repository.HealthRecordRepository
import com.example.healthmate.util.DateUtils
import com.example.healthmate.util.RecordValidator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class RecordFormState(
    val editingRecordId: Int? = null,
    val date: String = DateUtils.todayIso(),
    val stepsInput: String = "",
    val caloriesInput: String = "",
    val waterInput: String = "",
    val dateError: String? = null,
    val stepsError: String? = null,
    val caloriesError: String? = null,
    val waterError: String? = null,
    val isSaving: Boolean = false
) {
    val isEditMode: Boolean get() = editingRecordId != null
}

data class WeeklyStats(
    val avgSteps: Int = 0,
    val avgCalories: Int = 0,
    val avgWater: Int = 0,
    val totalRecords: Int = 0
)

class HealthViewModel(private val repository: HealthRecordRepository) : ViewModel() {

    val todayDate: String = DateUtils.todayIso()

    val todaySummary: StateFlow<DailySummary> = repository
        .getDailySummary(todayDate)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DailySummary(0, 0, 0)
        )

    val weeklyStats: StateFlow<WeeklyStats> = repository.allRecords
        .map { allRecords ->
            val cutoff = LocalDate.now().minusDays(6).toString()
            val recent = allRecords.filter { it.date >= cutoff }
            if (recent.isEmpty()) {
                WeeklyStats(totalRecords = allRecords.size)
            } else {
                val distinctDays = recent.map { it.date }.distinct().size.coerceAtLeast(1)
                WeeklyStats(
                    avgSteps = recent.sumOf { it.steps } / distinctDays,
                    avgCalories = recent.sumOf { it.calories } / distinctDays,
                    avgWater = recent.sumOf { it.water } / distinctDays,
                    totalRecords = allRecords.size
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WeeklyStats()
        )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val records: StateFlow<List<HealthRecord>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.allRecords
            } else {
                repository.searchRecordsByDate(query.trim())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _formState = MutableStateFlow(RecordFormState())
    val formState: StateFlow<RecordFormState> = _formState.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun prepareNewRecord() {
        _formState.value = RecordFormState(date = DateUtils.todayIso())
    }

    fun loadRecordForEdit(recordId: Int) {
        viewModelScope.launch {
            try {
                val record = repository.getRecordById(recordId)
                if (record != null) {
                    _formState.value = RecordFormState(
                        editingRecordId = record.id,
                        date = record.date,
                        stepsInput = record.steps.toString(),
                        caloriesInput = record.calories.toString(),
                        waterInput = record.water.toString()
                    )
                } else {
                    _snackbarMessage.emit("Record not found")
                }
            } catch (e: Exception) {
                _snackbarMessage.emit("Failed to load record: ${e.message}")
            }
        }
    }

    fun onDateChange(newDate: String) {
        _formState.update { it.copy(date = newDate, dateError = null) }
    }

    fun onStepsChange(newSteps: String) {
        _formState.update { it.copy(stepsInput = newSteps, stepsError = null) }
    }

    fun onCaloriesChange(newCalories: String) {
        _formState.update { it.copy(caloriesInput = newCalories, caloriesError = null) }
    }

    fun onWaterChange(newWater: String) {
        _formState.update { it.copy(waterInput = newWater, waterError = null) }
    }

    fun saveRecord(onSuccess: () -> Unit) {
        val current = _formState.value
        val validation = RecordValidator.validate(
            dateInput = current.date,
            stepsInput = current.stepsInput,
            caloriesInput = current.caloriesInput,
            waterInput = current.waterInput
        )

        if (!validation.isValid) {
            _formState.update {
                it.copy(
                    dateError = validation.dateError,
                    stepsError = validation.stepsError,
                    caloriesError = validation.caloriesError,
                    waterError = validation.waterError
                )
            }
            return
        }

        _formState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            try {
                val record = HealthRecord(
                    id = current.editingRecordId ?: 0,
                    date = current.date.trim(),
                    steps = current.stepsInput.trim().toInt(),
                    calories = current.caloriesInput.trim().toInt(),
                    water = current.waterInput.trim().toInt()
                )

                if (current.isEditMode) {
                    repository.updateRecord(record)
                    _snackbarMessage.emit("Health record updated")
                } else {
                    repository.insertRecord(record)
                    _snackbarMessage.emit("Health record saved")
                }
                _formState.update { it.copy(isSaving = false) }
                onSuccess()
            } catch (e: Exception) {
                _formState.update { it.copy(isSaving = false) }
                _snackbarMessage.emit("Could not save record: ${e.message}")
            }
        }
    }

    fun deleteRecord(record: HealthRecord) {
        viewModelScope.launch {
            try {
                repository.deleteRecord(record)
                _snackbarMessage.emit("Record deleted")
            } catch (e: Exception) {
                _snackbarMessage.emit("Could not delete record: ${e.message}")
            }
        }
    }

    companion object {
        fun provideFactory(repository: HealthRecordRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(HealthViewModel::class.java)) {
                        return HealthViewModel(repository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
        }
    }
}