package com.sporttracker.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sporttracker.ui.screens.ActivityListScreen
import com.sporttracker.ui.screens.AddEditActivityScreen
import com.sporttracker.ui.theme.SportTrackerTheme

@Composable
fun SportTrackerApp() {
    SportTrackerTheme {
        val navController = rememberNavController()
        SportTrackerNavHost(navController)
    }
}

@Composable
fun SportTrackerNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "activity_list"
    ) {
        composable("activity_list") {
            ActivityListScreen(
                onNavigateToAdd = {
                    navController.navigate("add_edit_activity")
                },
                onNavigateToEdit = { activityId ->
                    navController.navigate("add_edit_activity?activityId=$activityId")
                }
            )
        }

        composable("add_edit_activity?activityId={activityId}") { backStackEntry ->
            val activityId = backStackEntry.arguments?.getString("activityId")
            AddEditActivityScreen(
                activityId = activityId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("add_edit_activity") {
            AddEditActivityScreen(
                activityId = null,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}