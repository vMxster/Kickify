package it.unibo.kickify.data.models

import kotlinx.serialization.Serializable

enum class PaymentMethods (val visibleName: String){
    AMEX("American Express"),
    MAESTRO("Maestro"),
    MASTERCARD("MasterCard"),
    PAYPAL("PayPal"),
    VISA("Visa");

    companion object {
        fun getFromString(str:String): PaymentMethods? {
            return entries.firstOrNull { str.lowercase() == it.visibleName.lowercase() }
        }
    }
}

@Serializable
sealed class PaymentMethodInfo {
    @Serializable
    data class CreditCard(
        val id: Int,
        val brand: String, val last4: String,
        val expirationMonth: Int, val expirationYear: Int
    ) : PaymentMethodInfo()

    @Serializable
    data class PayPal(val id: Int, val email: String) : PaymentMethodInfo()

    companion object{
        private fun isValidEmail(email: String): Boolean {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        private fun isValidCardBrand(brand: String): Boolean {
            return PaymentMethods.entries.filter { it != PaymentMethods.PAYPAL }.any { it.visibleName == brand }
        }

        private fun isValidLast4(last4: String): Boolean {
            return last4.length == 4 && last4.all { it.isDigit() }
        }

        private fun isValidMonth(month: Int): Boolean {
            return month in 1..12
        }

        private fun isValidYear(year: Int): Boolean {
            val currentYear = java.time.Year.now().value
            return year >= currentYear && year <= currentYear + 10 // valid for next 10 years
        }

        fun validateCreditCard(
            brand: String, last4: String,
            expirationMonth: Int, expirationYear: Int
        ): Boolean {
            return brand.isNotBlank() && isValidCardBrand(brand) && isValidLast4(last4)
                    && expirationYear > 0 && isValidYear(expirationYear)
                    && expirationMonth > 0 && isValidMonth(expirationMonth)
        }

        fun validatePayPal(email: String): Boolean {
            return (isValidEmail(email))
        }
    }
}