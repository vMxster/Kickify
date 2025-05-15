package it.unibo.kickify

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import it.unibo.kickify.data.models.Theme
import it.unibo.kickify.ui.KickifyNavGraph
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.screens.settings.SettingsViewModel
import it.unibo.kickify.ui.theme.KickifyTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        try {
            Log.d("MainActivity", "onCreate: inizializzazione")
            setContent {
                val settingsViewModel: SettingsViewModel = koinViewModel<SettingsViewModel>()
                val themeState by settingsViewModel.theme.collectAsStateWithLifecycle(initialValue = Theme.System)

                KickifyTheme(
                    darkTheme = when(themeState){
                        Theme.Light -> false
                        Theme.Dark -> true
                        Theme.System -> isSystemInDarkTheme()
                    }
                ) {
                    val navController = rememberNavController()
                    val value = intent.getBooleanExtra("AUTH_SUCCESS", false)
                    LaunchedEffect(value) {
                        if (value) {
                            navController.navigate(KickifyRoute.Home) {
                                popUpTo(KickifyRoute.Home) { inclusive = true }
                            }
                        }
                    }

                    KickifyNavGraph(
                        navController = navController,
                        activity = this,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Errore fatale", e)
        }
    }
}
