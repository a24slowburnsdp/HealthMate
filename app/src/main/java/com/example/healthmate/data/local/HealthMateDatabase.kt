package com.example.healthmate.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

@Database(
    entities = [HealthRecord::class],
    version = 1,
    exportSchema = false
)
abstract class HealthMateDatabase : RoomDatabase() {

    abstract fun healthRecordDao(): HealthRecordDao

    companion object {
        @Volatile
        private var INSTANCE: HealthMateDatabase? = null

        fun getDatabase(context: Context): HealthMateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HealthMateDatabase::class.java,
                    "health_mate_db"
                )
                    .addCallback(SeedDatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class SeedDatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    INSTANCE?.healthRecordDao()?.insertRecords(createDummyRecords())
                }
            }
        }

        private fun createDummyRecords(): List<HealthRecord> {
            val today = LocalDate.now()
            return listOf(
                HealthRecord(date = today.toString(), steps = 6450, calories = 340, water = 1500),
                HealthRecord(date = today.minusDays(1).toString(), steps = 9200, calories = 480, water = 2300),
                HealthRecord(date = today.minusDays(2).toString(), steps = 7850, calories = 420, water = 2000),
                HealthRecord(date = today.minusDays(3).toString(), steps = 5400, calories = 290, water = 1750),
                HealthRecord(date = today.minusDays(4).toString(), steps = 10450, calories = 530, water = 2600),
                HealthRecord(date = today.minusDays(5).toString(), steps = 6800, calories = 360, water = 1800),
                HealthRecord(date = today.minusDays(6).toString(), steps = 8300, calories = 410, water = 2100)
            )
        }
    }
}