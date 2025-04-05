package com.example.urbankicks.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.urbankicks.ui.screens.HomeScreen
import com.example.urbankicks.viewmodel.HomeUiState

sealed class Screen(val route: String) {
    object Home : Screen("home")
    // object Detail : Screen("detail/{brandId}")
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    homeUiState: HomeUiState,
    onExploreClicked: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                uiState = homeUiState,
                onExploreClicked = onExploreClicked
            )
        }
        // Altre composable per altre schermate
    }
}
