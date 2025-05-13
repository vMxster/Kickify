package it.unibo.kickify.utils

import android.util.Patterns

class LoginRegisterUtils {
    companion object {
        fun isValidEmail(email: String): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }
}