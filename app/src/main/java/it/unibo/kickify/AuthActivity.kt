package it.unibo.kickify

import android.content.Intent
import android.os.Bundle
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import androidx.core.content.ContextCompat

class AuthActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authenticate()
    }

    private fun authenticate() {
        val executor = ContextCompat.getMainExecutor(this)
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
            .setNegativeButtonText("Annulla")
            .build()

        biometricPrompt.authenticate(promptInfo)
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