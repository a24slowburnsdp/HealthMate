package com.example.healthmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.healthmate.ui.theme.HealthMateTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthmate.data.local.HealthMateDatabase
import com.example.healthmate.data.repository.HealthRecordRepository
import com.example.healthmate.ui.navigation.HealthMateNavHost
import com.example.healthmate.ui.viewmodel.HealthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthMateTheme {
                val database = HealthMateDatabase.getDatabase(applicationContext)
                val repository = HealthRecordRepository(database.healthRecordDao())
                val healthViewModel: HealthViewModel = viewModel(
                    factory = HealthViewModel.provideFactory(repository)
                )
                HealthMateNavHost(viewModel = healthViewModel)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HealthMateTheme {
        Greeting("Android")
    }
}