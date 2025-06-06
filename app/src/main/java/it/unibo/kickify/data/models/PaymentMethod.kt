package it.unibo.kickify.data.models

enum class PaymentMethods (val visibleName: String){
    AMEX("American Express"),
    MAESTRO("Maestro"),
    MASTERCARD("MasterCard"),
    PAYPAL("Paypal"),
    VISA("Visa");

    companion object {
        fun getFromString(str:String): PaymentMethods? {
            return entries.firstOrNull { str.lowercase() == it.visibleName.lowercase() }
        }
    }
}

sealed class PaymentMethodInfo {
    data class CreditCard(
        val brand: String, val last4: String,
        val expirationMonth: Int, val expirationYear: Int,
    ) : PaymentMethodInfo()

    data class PayPal(val email: String) : PaymentMethodInfo()
}