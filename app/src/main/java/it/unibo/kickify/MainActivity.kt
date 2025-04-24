package it.unibo.kickify

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import it.unibo.kickify.ui.KickifyNavGraph
import it.unibo.kickify.ui.theme.KickifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        try {
            Log.d("MainActivity", "onCreate: inizializzazione")
            setContent {
                KickifyTheme {
                    val navController = rememberNavController()
                    KickifyNavGraph(navController)
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Errore fatale", e)
        }
    }
}
