package com.example.urbankicks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.example.urbankicks.navigation.AppNavGraph
import com.example.urbankicks.ui.theme.UrbanKicksTheme
import com.example.urbankicks.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UrbanKicksTheme {
                val navController = rememberNavController()
                val uiState by homeViewModel.uiState.collectAsState()

                AppNavGraph(
                    navController = navController,
                    homeUiState = uiState,
                    onExploreClicked = {
                        // Esempio: potresti navigare a una schermata di dettaglio
                        // navController.navigate("detail/123")
                    }
                )
            }
        }
    }
}
