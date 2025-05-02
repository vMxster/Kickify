package it.unibo.kickify

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import it.unibo.kickify.ui.KickifyNavGraph
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.theme.KickifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        try {
            Log.d("MainActivity", "onCreate: inizializzazione")

            setContent {
                val ctx = LocalContext.current
                val pushNotificationMgr = PushNotificationManager(ctx)
                if(pushNotificationMgr.checkNotificationPermission()){
                    pushNotificationMgr.sendNotificationNoAction(
                        notificationTitle = "Title",
                        notificationMessage = "Message"
                    )
                }

                KickifyTheme {
                    val noNotificationPermissionMessage = stringResource(R.string.notificationManager_NOpermissionMessage)
                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission(),
                        onResult = { granted ->
                            if (!granted) {
                                Toast.makeText(ctx,
                                    noNotificationPermissionMessage,
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    if(Build.VERSION.SDK_INT >= 33){
                        LaunchedEffect(Unit) {
                            launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }

                    val navController = rememberNavController()
                    val value = intent.getBooleanExtra("AUTH_SUCCESS", false)
                    LaunchedEffect(value) {
                        if (value) {
                            navController.navigate(KickifyRoute.Home) {
                                popUpTo(KickifyRoute.Home) { inclusive = true }
                            }
                        }
                    }

                    KickifyNavGraph(navController, this)
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Errore fatale", e)
        }
    }
}
