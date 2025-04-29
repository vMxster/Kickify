package it.unibo.kickify

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import androidx.core.content.ContextCompat

class AuthActivityOLD : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authenticate()
    }

    private fun authenticate() {
        val executor = ContextCompat.getMainExecutor(this)
        val authenticators = if(Build.VERSION.SDK_INT >= 30) {
            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
        } else { BIOMETRIC_STRONG }

        val biometricPrompt = BiometricPrompt(
            this, // FragmentActivity
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    navigateBackToMain(result = true, msg = "AUTH SUCCESS")
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    navigateBackToMain(result = false, msg = "AUTH ERROR code $errorCode: $errString")
                }

                override fun onAuthenticationFailed() {
                    navigateBackToMain(result = false, "AUTH FAILED: incorrect fingerprint")
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticazione biometrica")
            .setSubtitle("Usa la tua impronta digitale per continuare")
            .setDescription("Description")
            .setConfirmationRequired(true)
            .setAllowedAuthenticators(authenticators)

        if(Build.VERSION.SDK_INT < 30){
            promptInfo.setNegativeButtonText("Annulla")
        }

        biometricPrompt.authenticate(promptInfo.build())
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