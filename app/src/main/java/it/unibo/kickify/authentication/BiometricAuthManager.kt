package it.unibo.kickify.authentication

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import it.unibo.kickify.R

interface BiometricAuthListener {
    fun onBiometricAuthSuccess()
    fun onBiometricAuthError(errorCode: Int, errString: CharSequence)
    fun onBiometricAuthFailed()
}

class BiometricAuthManager(
    private val activity: FragmentActivity,
    private val listener: BiometricAuthListener
) {
    private val executor = ContextCompat.getMainExecutor(activity)
    private var biometricPrompt: BiometricPrompt = BiometricPrompt(activity, executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                listener.onBiometricAuthError(errorCode, errString)
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                listener.onBiometricAuthSuccess()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                listener.onBiometricAuthFailed()
            }
        })

    private fun buildPrompt(): BiometricPrompt.PromptInfo {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(activity.applicationContext.getString(R.string.biometricAuthTitle))
            .setSubtitle(activity.applicationContext.getString(R.string.biometricAuthSubtitle))
            .setDescription(activity.applicationContext.getString(R.string.biometricAuthDescription))
            .setAllowedAuthenticators(ALLOWED_AUTHENTICATORS)

        if(Build.VERSION.SDK_INT < 30){
            promptInfo.setNegativeButtonText(activity.applicationContext.getString(R.string.settings_cancel))
        }
        return promptInfo.build()
    }

    fun authenticate() {
        if (canAuthenticate(activity.applicationContext)) {
            biometricPrompt.authenticate(buildPrompt())
        } else {
            listener.onBiometricAuthError(-1, "Biometric auth unavailable or not configured.")
        }
    }

    var isAuthAvailable = false
        private set

    var isAuthEnrolled = false
        private set

    companion object {
        val ALLOWED_AUTHENTICATORS = if(Build.VERSION.SDK_INT >= 30) {
            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
        } else { BIOMETRIC_STRONG }

        /**
         * Return false if biometric login cannot be enrolled,
         * true otherwise
         */
        @Composable
        fun enrollBiometricLoginIfAvailable(context: Context): Boolean {
            if(getCanAuthenticateStatus(context) == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
                && Build.VERSION.SDK_INT >= 30) {
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                    )
                }
                val enrollLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult(),
                    onResult = {
                        println("Activity result: $it")
                    }
                )
                enrollLauncher.launch(enrollIntent)
                return true
            }
            return false
        }

        fun getCanAuthenticateStatus(context: Context): Int {
            val biometricManager = BiometricManager.from(context)
            return biometricManager.canAuthenticate(ALLOWED_AUTHENTICATORS)
        }

        fun canAuthenticate(context: Context): Boolean {
            val biometricManager = BiometricManager.from(context)
            when (biometricManager.canAuthenticate(ALLOWED_AUTHENTICATORS)) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    Log.d("BiometricAuthManager", "App can authenticate using biometrics.")
                    return true
                }
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    Log.e("BiometricAuthManager", "No biometric features available on this device.")
                    return false
                }
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                    Log.e("BiometricAuthManager", "Biometric features are currently unavailable.")
                    return false
                }
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    Log.e("BiometricAuthManager", "The user hasn't enrolled any biometric credentials.")
                    return false
                }
                else -> {
                    Log.e("BiometricAuthManager", "Other biometric error.")
                    return false
                }
            }
        }
    }
}

@Composable
fun rememberBiometricAuthManager(listener: BiometricAuthListener): BiometricAuthManager {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
        ?: throw IllegalStateException("BiometricPrompt requires a FragmentActivity")

    return remember(activity, listener) {
        BiometricAuthManager(activity, listener)
    }
}