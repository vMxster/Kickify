package it.unibo.kickify.data.models

enum class PaymentMethod (val visibleName: String){
    AMEX("American Express"),
    MAESTRO("Maestro"),
    MASTERCARD("MasterCard"),
    PAYPAL("Paypal"),
    VISA("Visa");

    companion object {
        fun getFromString(str:String): PaymentMethod? {
            return entries.firstOrNull { str.lowercase() == it.visibleName.lowercase() }
        }
    }
}