package com.example.healthmate.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {
    private val fullFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault())
    private val shortFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())

    fun todayIso(): String = LocalDate.now().toString()

    fun formatDashboardDate(isoDate: String): String {
        return try {
            val parsed = LocalDate.parse(isoDate)
            val today = LocalDate.now()
            val formatted = parsed.format(fullFormatter)
            when (parsed) {
                today -> "Today – $formatted"
                today.minusDays(1) -> "Yesterday – $formatted"
                else -> formatted
            }
        } catch (_: Exception) {
            isoDate
        }
    }

    fun formatListDate(isoDate: String): String {
        return try {
            val parsed = LocalDate.parse(isoDate)
            val today = LocalDate.now()
            val formatted = parsed.format(shortFormatter)
            when (parsed) {
                today -> "Today ($formatted)"
                today.minusDays(1) -> "Yesterday ($formatted)"
                else -> formatted
            }
        } catch (_: Exception) {
            isoDate
        }
    }
}