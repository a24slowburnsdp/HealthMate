package com.example.healthmate.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.healthmate.ui.add_edit.AddEditRecordScreen
import com.example.healthmate.ui.dashboard.DashboardScreen
import com.example.healthmate.ui.records.RecordListScreen
import com.example.healthmate.ui.theme.HealthColors
import com.example.healthmate.ui.viewmodel.HealthViewModel

private object Routes {
    const val DASHBOARD = "dashboard"
    const val RECORDS = "records"
    const val ADD_RECORD = "add_record"
    const val EDIT_RECORD = "edit_record/{recordId}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthMateNavHost(viewModel: HealthViewModel) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val todaySummary by viewModel.todaySummary.collectAsStateWithLifecycle()
    val weeklyStats by viewModel.weeklyStats.collectAsStateWithLifecycle()
    val records by viewModel.records.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val isMainTab = currentRoute == Routes.DASHBOARD || currentRoute == Routes.RECORDS

    val topBarTitle = when (currentRoute) {
        Routes.DASHBOARD -> "HealthMate"
        Routes.RECORDS -> "Health Records"
        Routes.ADD_RECORD -> "Add Health Record"
        Routes.EDIT_RECORD -> "Edit Health Record"
        else -> "HealthMate"
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = topBarTitle, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (!isMainTab) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HealthColors.WellnessTeal,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            if (isMainTab) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == Routes.DASHBOARD,
                        onClick = {
                            navController.navigate(Routes.DASHBOARD) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Rounded.Home, contentDescription = "Dashboard") },
                        label = { Text("Dashboard") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.RECORDS,
                        onClick = {
                            navController.navigate(Routes.RECORDS) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Rounded.History, contentDescription = "Records") },
                        label = { Text("Records") }
                    )
                }
            }
        },
        floatingActionButton = {
            if (isMainTab) {
                FloatingActionButton(
                    onClick = {
                        viewModel.prepareNewRecord()
                        navController.navigate(Routes.ADD_RECORD)
                    },
                    containerColor = HealthColors.WellnessTeal,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Add health record")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.DASHBOARD,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.DASHBOARD) {
                DashboardScreen(
                    todayDate = viewModel.todayDate,
                    todaySummary = todaySummary,
                    weeklyStats = weeklyStats,
                    onAddRecordClick = {
                        viewModel.prepareNewRecord()
                        navController.navigate(Routes.ADD_RECORD)
                    },
                    onViewHistoryClick = {
                        navController.navigate(Routes.RECORDS) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(Routes.RECORDS) {
                RecordListScreen(
                    records = records,
                    searchQuery = searchQuery,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    onEditClick = { record ->
                        navController.navigate("edit_record/${record.id}")
                    },
                    onDeleteConfirm = viewModel::deleteRecord
                )
            }

            composable(Routes.ADD_RECORD) {
                AddEditRecordScreen(
                    formState = formState,
                    onDateChange = viewModel::onDateChange,
                    onStepsChange = viewModel::onStepsChange,
                    onCaloriesChange = viewModel::onCaloriesChange,
                    onWaterChange = viewModel::onWaterChange,
                    onSaveClick = {
                        viewModel.saveRecord { navController.popBackStack() }
                    },
                    onCancelClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.EDIT_RECORD,
                arguments = listOf(navArgument("recordId") { type = NavType.IntType })
            ) { backStackEntry ->
                val recordId = backStackEntry.arguments?.getInt("recordId") ?: return@composable
                LaunchedEffect(recordId) {
                    viewModel.loadRecordForEdit(recordId)
                }
                AddEditRecordScreen(
                    formState = formState,
                    onDateChange = viewModel::onDateChange,
                    onStepsChange = viewModel::onStepsChange,
                    onCaloriesChange = viewModel::onCaloriesChange,
                    onWaterChange = viewModel::onWaterChange,
                    onSaveClick = {
                        viewModel.saveRecord { navController.popBackStack() }
                    },
                    onCancelClick = { navController.popBackStack() }
                )
            }
        }
    }
}