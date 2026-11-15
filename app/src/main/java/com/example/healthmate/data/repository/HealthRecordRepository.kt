package com.example.healthmate.data.repository

import com.example.healthmate.data.local.DailySummary
import com.example.healthmate.data.local.HealthRecord
import com.example.healthmate.data.local.HealthRecordDao
import kotlinx.coroutines.flow.Flow

class HealthRecordRepository(private val dao: HealthRecordDao) {

    val allRecords: Flow<List<HealthRecord>> = dao.getAllRecords()

    fun searchRecordsByDate(query: String): Flow<List<HealthRecord>> {
        return dao.searchRecordsByDate(query)
    }

    fun getDailySummary(date: String): Flow<DailySummary> {
        return dao.getDailySummary(date)
    }

    suspend fun getRecordById(id: Int): HealthRecord? {
        return dao.getRecordById(id)
    }

    suspend fun insertRecord(record: HealthRecord): Long {
        return dao.insertRecord(record)
    }

    suspend fun updateRecord(record: HealthRecord) {
        dao.updateRecord(record)
    }

    suspend fun deleteRecord(record: HealthRecord) {
        dao.deleteRecord(record)
    }
}