package com.goodplan.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.goodplan.app.ui.screens.HabitsScreen
import com.goodplan.app.ui.screens.InstructionScreen
import com.goodplan.app.ui.screens.WeekScreen
import com.goodplan.app.viewmodel.HabitsViewModel
import com.goodplan.app.viewmodel.WeekViewModel

object Routes {
    const val WEEK = "week"
    const val HABITS = "habits"
    const val INSTRUCTION = "instruction"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.WEEK,
        modifier = modifier
    ) {
        composable(Routes.WEEK) {
            val vm: WeekViewModel = viewModel()
            WeekScreen(
                vm = vm,
                onOpenInstruction = { navController.navigate(Routes.INSTRUCTION) }
            )
        }
        composable(Routes.HABITS) {
            val vm: HabitsViewModel = viewModel()
            HabitsScreen(vm = vm)
        }
        composable(Routes.INSTRUCTION) {
            InstructionScreen(onBack = { navController.popBackStack() })
        }
    }
}