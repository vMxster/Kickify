package it.unibo.kickify

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import it.unibo.kickify.data.models.Theme
import it.unibo.kickify.ui.KickifyNavGraph
import it.unibo.kickify.ui.screens.settings.SettingsViewModel
import it.unibo.kickify.ui.theme.KickifyTheme
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        try {
            Log.d("MainActivity", "onCreate: inizializzazione")
            setContent {
                val settingsViewModel: SettingsViewModel = koinViewModel<SettingsViewModel>()
                val themeState by settingsViewModel.theme.collectAsStateWithLifecycle()
                val appLanguageId by settingsViewModel.appLanguage.collectAsStateWithLifecycle()

                LaunchedEffect(appLanguageId){
                    if(appLanguageId.isNotEmpty()){
                        setAppLocale(this@MainActivity, appLanguageId)
                    }
                }

                KickifyTheme(
                    darkTheme = when(themeState){
                        Theme.Light -> false
                        Theme.Dark -> true
                        Theme.System -> isSystemInDarkTheme()
                    }
                ) {
                    val navController = rememberNavController()
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

    // Function to set app locale
    private fun setAppLocale(context: Context, languageCode: String) {
        if (languageCode.isEmpty()) return

        val locale = Locale(languageCode)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales =
                LocaleList(locale)
        } else {
            val config = context.resources.configuration
            config.setLocale(locale)
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
    }
}
