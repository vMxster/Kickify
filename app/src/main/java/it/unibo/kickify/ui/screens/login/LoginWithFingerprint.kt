package it.unibo.kickify.ui.screens.login

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.unibo.kickify.AuthActivity

@Composable
fun BiometricAuthScreen(navController: NavController) {
    val ctx = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Accedi con impronta digitale", fontSize = 20.sp)

        Button(onClick = {
            ctx.startActivity(Intent(ctx, AuthActivity::class.java))
        }) {
            Text("Autenticati")
        }
    }
}

/*
@Composable
fun LoginWithFingerPrintScreen(
    navController: NavController,
    activity: ComponentActivity
) {
    val context = LocalContext.current
    val isAuthenticated = rememberSaveable { mutableStateOf(false) }
    val executor = ContextCompat.getMainExecutor(context)

    Scaffold { contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Button(onClick = {

            }) {
                Text("Accedi con impronta digitale")
            }

        }
    }
}
*/