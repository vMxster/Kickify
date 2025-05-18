package it.unibo.kickify.utils

import android.util.Patterns

class LoginRegisterUtils {
    companion object {
        fun isValidEmail(email: String): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun isValidUsername(username: String):Boolean{
            return !username.contains(" ") && username.length >= 8
        }

        /**
         * Return true if the password is at least 10 characters long and contains:
         *  - at least 1 lowercase character
         *  - at least 1 uppercase character
         *  - at least 1 digit
         *  - at least 1 symbol
         */
        fun isValidPassword(psw: String): Boolean{
            return psw.length >= 10 &&
                    psw.any { it.isLowerCase() } &&
                    psw.any { it.isUpperCase() } &&
                    psw.any { it.isDigit() } &&
                    psw.any { !it.isLetterOrDigit() }
        }
    }
}