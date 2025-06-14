package it.unibo.kickify.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.authentication.BiometricAuthListener
import it.unibo.kickify.authentication.BiometricAuthManager
import it.unibo.kickify.authentication.rememberBiometricAuthManager
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.ui.screens.achievements.AchievementsViewModel

@Composable
fun BiometricLoginScreen(
    navController: NavController,
    achievementsViewModel: AchievementsViewModel
) {
    var authenticationStatus by remember { mutableStateOf(BiometricAuthStatus.IDLE) }
    val context = LocalContext.current

    val biometricAuthListener = remember {
        object : BiometricAuthListener {
            override fun onBiometricAuthSuccess() {
                authenticationStatus = BiometricAuthStatus.SUCCESS

                navController.navigate(KickifyRoute.Home) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            }

            override fun onBiometricAuthError(errorCode: Int, errString: CharSequence) {
                authenticationStatus = BiometricAuthStatus.ERROR
                // an error occurs
            }

            override fun onBiometricAuthFailed() {
                authenticationStatus = BiometricAuthStatus.FAILED
                // auth fails when too much attempts
            }
        }
    }

    val biometricAuthManager = rememberBiometricAuthManager(listener = biometricAuthListener)

    LaunchedEffect(Unit) {
        if (BiometricAuthManager.canAuthenticate(context)) {
            authenticationStatus = BiometricAuthStatus.PENDING
            biometricAuthManager.authenticate()
        } else {
            navController.navigate(KickifyRoute.Login) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    ScreenTemplate(
        screenTitle = "",
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { },
        showModalDrawer = false,
        achievementsViewModel = achievementsViewModel
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (authenticationStatus) {
                BiometricAuthStatus.IDLE, BiometricAuthStatus.PENDING -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.biometricAuthInProgress),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                BiometricAuthStatus.SUCCESS -> {
                    Text(
                        stringResource(R.string.biometricAuthCompleted),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                BiometricAuthStatus.ERROR, BiometricAuthStatus.FAILED -> {
                    Text(
                        stringResource(R.string.biometricAuthFailedOrCancelled),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        navController.navigate(KickifyRoute.Login) { launchSingleTop = true }
                    }) {
                        Text(stringResource(R.string.biometricAuthLoginWithEmailAndPsw))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        if (BiometricAuthManager.canAuthenticate(context)) {
                            authenticationStatus = BiometricAuthStatus.PENDING
                            biometricAuthManager.authenticate()
                        }
                    }) {
                        Text(stringResource(R.string.biometricAuthRetry))
                    }
                }
            }
        }
    }
}

enum class BiometricAuthStatus {
    IDLE, PENDING, SUCCESS, ERROR, FAILED
}