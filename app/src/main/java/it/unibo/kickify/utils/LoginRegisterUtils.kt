package it.unibo.kickify.utils

import android.util.Patterns

class LoginRegisterUtils {
    companion object {
        fun isValidEmail(email: String): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun isValidUsername(username: String):Boolean{
            return !username.contains(" ") && username.length >= 4
        }

        fun isValidPassword(psw: String): Boolean{
            return psw.length >= 10
        }
    }
}