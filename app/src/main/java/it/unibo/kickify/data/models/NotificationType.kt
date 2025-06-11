package it.unibo.kickify.data.models

enum class NotificationType(val internalName: String) {
    ProductBackinStock("Product Back In Stock"),
    OrderShipped("Order Shipped"),
    FlashSale("Flash Sale"),
    ItemsInCart("Items In Cart"),
    OrderPlaced("Order Placed"),
    RequestedProductReview("Requested Product Review");

    companion object {
        fun getTypeFromString(str: String): NotificationType {
            if (str.contains("shipped", ignoreCase = true)) {
                return OrderShipped

            } else if (str.contains("sale", ignoreCase = true)) {
                return FlashSale

            } else if (str.contains("cart", ignoreCase = true)) {
                return ItemsInCart

            } else if (str.contains("placed", ignoreCase = true)) {
                return OrderPlaced

            } else if (str.contains("review", ignoreCase = true)) {
                return RequestedProductReview
            }
            return ProductBackinStock
        }
    }
}