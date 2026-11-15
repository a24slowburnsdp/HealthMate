package com.example.healthmate.ui.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.DirectionsWalk
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.healthmate.data.local.DailySummary
import com.example.healthmate.ui.theme.HealthColors
import com.example.healthmate.ui.viewmodel.WeeklyStats
import com.example.healthmate.util.DateUtils
import java.text.NumberFormat
import java.util.Locale

private const val DAILY_STEPS_GOAL = 10_000
private const val DAILY_CALORIES_GOAL = 500
private const val DAILY_WATER_GOAL = 2_500

@Composable
fun DashboardScreen(
    todayDate: String,
    todaySummary: DailySummary,
    weeklyStats: WeeklyStats,
    onAddRecordClick: () -> Unit,
    onViewHistoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val numberFormat = NumberFormat.getIntegerInstance(Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header banner with today's date
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = HealthColors.WellnessTealContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.CalendarToday,
                    contentDescription = "Today's date",
                    tint = HealthColors.WellnessTealDark
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Daily Wellness Summary",
                        style = MaterialTheme.typography.labelMedium,
                        color = HealthColors.WellnessTealDark
                    )
                    Text(
                        text = DateUtils.formatDashboardDate(todayDate),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = HealthColors.WellnessTealDark
                    )
                }
            }
        }

        // Empty today prompt if no activity logged yet today
        if (todaySummary.totalSteps == 0 &&
            todaySummary.totalCalories == 0 &&
            todaySummary.totalWater == 0
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No entries logged for today yet. Tap 'Log Entry' below to record your steps, calories, and water intake.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(14.dp)
                )
            }
        }

        // Metric 1: Steps (green)
        HealthMetricCard(
            title = "Steps Walked",
            valueText = "${numberFormat.format(todaySummary.totalSteps)} steps",
            goalText = "Goal: ${numberFormat.format(DAILY_STEPS_GOAL)} steps",
            progress = (todaySummary.totalSteps.toFloat() / DAILY_STEPS_GOAL).coerceIn(0f, 1f),
            icon = Icons.Rounded.DirectionsWalk,
            accentColor = HealthColors.StepsGreen,
            containerColor = HealthColors.StepsGreenContainer
        )

        // Metric 2: Calories Burned (red)
        HealthMetricCard(
            title = "Calories Burned",
            valueText = "${numberFormat.format(todaySummary.totalCalories)} kcal",
            goalText = "Goal: ${numberFormat.format(DAILY_CALORIES_GOAL)} kcal",
            progress = (todaySummary.totalCalories.toFloat() / DAILY_CALORIES_GOAL).coerceIn(0f, 1f),
            icon = Icons.Rounded.LocalFireDepartment,
            accentColor = HealthColors.CaloriesRed,
            containerColor = HealthColors.CaloriesRedContainer
        )

        // Metric 3: Water Intake (blue)
        HealthMetricCard(
            title = "Water Intake",
            valueText = "${numberFormat.format(todaySummary.totalWater)} ml",
            goalText = "Goal: ${numberFormat.format(DAILY_WATER_GOAL)} ml",
            progress = (todaySummary.totalWater.toFloat() / DAILY_WATER_GOAL).coerceIn(0f, 1f),
            icon = Icons.Rounded.WaterDrop,
            accentColor = HealthColors.WaterBlue,
            containerColor = HealthColors.WaterBlueContainer
        )

        // Quick action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onAddRecordClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = HealthColors.WellnessTeal,
                    contentColor = Color.White
                ),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Log Entry")
            }

            OutlinedButton(
                onClick = onViewHistoryClick,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Rounded.History, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("View History")
            }
        }

        // 7 day averages overview
        ElevatedCard(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "7-Day Daily Averages",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Based on ${weeklyStats.totalRecords} total saved record(s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AverageStatColumn(
                        label = "Avg Steps",
                        value = numberFormat.format(weeklyStats.avgSteps),
                        color = HealthColors.StepsGreen
                    )
                    AverageStatColumn(
                        label = "Avg Calories",
                        value = "${numberFormat.format(weeklyStats.avgCalories)} kcal",
                        color = HealthColors.CaloriesRed
                    )
                    AverageStatColumn(
                        label = "Avg Water",
                        value = "${numberFormat.format(weeklyStats.avgWater)} ml",
                        color = HealthColors.WaterBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun HealthMetricCard(
    title: String,
    valueText: String,
    goalText: String,
    progress: Float,
    icon: ImageVector,
    accentColor: Color,
    containerColor: Color
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        label = "metricProgress"
    )
    val percent = (progress * 100).toInt()

    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(containerColor)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = valueText,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "$percent%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                color = accentColor,
                trackColor = containerColor,
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = goalText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AverageStatColumn(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}