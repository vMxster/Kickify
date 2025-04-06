package it.unibo.kickify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import it.unibo.kickify.ui.navigation.AppNavGraph
import it.unibo.kickify.ui.theme.UrbanKicksTheme
import it.unibo.kickify.ui.viewmodel.HomeViewModel

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
