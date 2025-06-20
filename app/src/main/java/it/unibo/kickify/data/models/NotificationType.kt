package it.unibo.kickify.data.models

enum class NotificationType(val internalName: String) {
    ProductBackinStock("Product Back In Stock"),
    FlashSale("Flash Sale"),
    ItemsInCart("Items In Cart"),
    OrderPlaced("Order Placed"),
    OrderShipped("Order Shipped"),
    OrderDelivered("Order Delivered"),
    RequestedProductReview("Requested Product Review");

    companion object {
        fun getTypeFromString(type: String, msg: String): NotificationType {
            if (type.contains("Stock Product", ignoreCase = true)) {
                return ProductBackinStock

            } else if (type.contains("Flash Sale", ignoreCase = true)) {
                return FlashSale

            } else if (type.contains("Cart Reminder", ignoreCase = true)) {
                return ItemsInCart

            } else if (type.contains("Review Request", ignoreCase = true)) {
                return RequestedProductReview

            } else if(type.contains("Order Status", ignoreCase = true)){

                if(msg.contains("placed", ignoreCase = true)){
                    return OrderPlaced
                } else if(msg.contains("in progress", ignoreCase = true)){
                    return OrderPlaced
                } else if(msg.contains("shipped", ignoreCase = true)){
                    OrderShipped
                } else if(msg.contains("delivered", ignoreCase = true)){
                    OrderDelivered
                }
            }

            return OrderPlaced
        }
    }
}