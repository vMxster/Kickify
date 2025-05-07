package it.unibo.kickify

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.unibo.kickify.authentication.AuthManager
import it.unibo.kickify.authentication.BiometricResult
import it.unibo.kickify.ui.theme.KickifyTheme

class AuthActivity : AppCompatActivity() {
    private val promptMgr by lazy {
        AuthManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KickifyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val biometricResult by promptMgr.promptResults.collectAsStateWithLifecycle(
                        initialValue = null
                    )
                    val enrollLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult(),
                        onResult = {
                            println("Activity result: $it")
                        }
                    )
                    LaunchedEffect(biometricResult) {
                        if (biometricResult is BiometricResult.AuthenticationNotSet) {
                            if (Build.VERSION.SDK_INT >= 30) {
                                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                    putExtra(
                                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                                    )
                                }
                                enrollLauncher.launch(enrollIntent)
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = {
                            promptMgr.showBiometricPrompt(
                                title = "Title",
                                subtitle = "Subtitle",
                                description = "Description"
                            )
                        }) {
                            Text(text = "Authenticate")
                        }
                        biometricResult?.let { result ->
                            Text(
                                text = when (result) {
                                    is BiometricResult.AuthenticationError -> {
                                        result.error
                                    }

                                    BiometricResult.AuthenticationFailed -> {
                                        "Authentication failed"
                                    }

                                    BiometricResult.AuthenticationNotSet -> {
                                        "Authentication not set"
                                    }

                                    BiometricResult.AuthenticationSuccess -> {
                                        "Authentication success"
                                    }

                                    BiometricResult.FeatureUnavailable -> {
                                        "Feature unavailable"
                                    }

                                    BiometricResult.HardwareUnavailable -> {
                                        "Hardware unavailable"
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun navigateBackToMain(result: Boolean, msg: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("AUTH_SUCCESS", result) // send result to mainActivity
            putExtra("AUTH_MSG", msg) // send msg for details
        }
        startActivity(intent)
        finish() // close this activity
    }
}