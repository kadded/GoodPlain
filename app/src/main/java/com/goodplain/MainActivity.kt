package com.goodplan.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.goodplan.app.ui.navigation.NavGraph
import com.goodplan.app.ui.navigation.Routes
import com.goodplan.app.ui.theme.GoodPlanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GoodPlanTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (currentRoute != Routes.INSTRUCTION) {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = currentRoute == Routes.WEEK,
                                    onClick = {
                                        navController.navigate(Routes.WEEK) {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Неделя") },
                                    label = { Text("Неделя") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Routes.HABITS,
                                    onClick = {
                                        navController.navigate(Routes.HABITS) {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Привычки") },
                                    label = { Text("Привычки") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Routes.INSTRUCTION,
                                    onClick = {
                                        navController.navigate(Routes.INSTRUCTION)
                                    },
                                    icon = { Icon(Icons.Default.Info, contentDescription = "Инструкция") },
                                    label = { Text("Методика") }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}