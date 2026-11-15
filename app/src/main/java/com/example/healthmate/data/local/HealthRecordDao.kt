package com.example.healthmate.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

data class DailySummary(
    val totalSteps: Int,
    val totalCalories: Int,
    val totalWater: Int
)

@Dao
interface HealthRecordDao {

    @Query("SELECT * FROM health_records ORDER BY date DESC, id DESC")
    fun getAllRecords(): Flow<List<HealthRecord>>

    @Query("SELECT * FROM health_records WHERE date LIKE '%' || :query || '%' ORDER BY date DESC, id DESC")
    fun searchRecordsByDate(query: String): Flow<List<HealthRecord>>

    @Query(
        """
        SELECT 
            COALESCE(SUM(steps), 0) AS totalSteps,
            COALESCE(SUM(calories), 0) AS totalCalories,
            COALESCE(SUM(water), 0) AS totalWater
        FROM health_records
        WHERE date = :date
        """
    )
    fun getDailySummary(date: String): Flow<DailySummary>

    @Query("SELECT * FROM health_records WHERE id = :id")
    suspend fun getRecordById(id: Int): HealthRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: HealthRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<HealthRecord>)

    @Update
    suspend fun updateRecord(record: HealthRecord)

    @Delete
    suspend fun deleteRecord(record: HealthRecord)
}