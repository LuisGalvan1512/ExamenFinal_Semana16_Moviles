package com.ejemplo.galvanmorales.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ejemplo.galvanmorales.ui.screens.*
import com.ejemplo.galvanmorales.viewmodel.EventViewModel

object AppDestinations {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val EVENTS = "events"
    const val ADD_EDIT_EVENT = "add_edit_event"
    const val ADD_EDIT_EVENT_WITH_ID = "add_edit_event?eventId={eventId}"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val sharedViewModel: EventViewModel = viewModel()
    AppNavHost(navController, sharedViewModel)
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: EventViewModel
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.LOGIN
    ) {
        composable(AppDestinations.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(AppDestinations.REGISTER)
                },
                onLoginSuccess = {
                    navController.navigate(AppDestinations.EVENTS) {
                        popUpTo(AppDestinations.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(AppDestinations.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(AppDestinations.EVENTS) {
                        popUpTo(AppDestinations.LOGIN) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppDestinations.EVENTS) {
            EventsScreen(
                onLogout = {
                    navController.navigate(AppDestinations.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToAddEdit = { eventId ->
                    if (eventId != null) {
                        navController.navigate("${AppDestinations.ADD_EDIT_EVENT}?eventId=$eventId")
                    } else {
                        navController.navigate(AppDestinations.ADD_EDIT_EVENT)
                    }
                },
                viewModel = viewModel
            )
        }

        composable(
            route = AppDestinations.ADD_EDIT_EVENT_WITH_ID,
            arguments = listOf(
                navArgument("eventId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            AddEditEventScreen(
                eventId = eventId,
                onNavigateBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
    }
}
