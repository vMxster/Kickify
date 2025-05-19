package it.unibo.kickify.data.repositories

class AppRepository(
    private val remoteRepository: RemoteRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val wishlistRepository: WishlistRepository,
    private val reviewRepository: ReviewRepository,
    private val notificationRepository: NotificationRepository
) {

}