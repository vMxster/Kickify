package it.unibo.kickify.ui.screens.profile

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import it.unibo.kickify.camerax.CameraXutils
import it.unibo.kickify.camerax.TakePhotoCameraScreen

@Composable
fun TakePhotoScreen(
    navController: NavController, mainActivity: ComponentActivity,
    cameraXutils: CameraXutils
){

    if (!cameraXutils.hasRequiredPermissions()) {
        ActivityCompat.requestPermissions(
            mainActivity, cameraXutils.permissions, 0
        )
    }

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TakePhotoCameraScreen(navController, cameraXutils)
        }
    }
}