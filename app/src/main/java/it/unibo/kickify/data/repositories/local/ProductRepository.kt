package it.unibo.kickify.data.repositories.local

import it.unibo.kickify.data.database.*

class ProductRepository(private val productDao: ProductDao) {

    suspend fun getProductWithVariants(productId: Int): CompleteProduct? =
        productDao.getProductWithVariants(productId)

    suspend fun getVariantsByProductId(productId: Int): List<Version> =
        productDao.getVariantsByProductId(productId)

    suspend fun getProductImages(productId: Int): List<Image> =
        productDao.getProductImages(productId)

    suspend fun getProductsWithImage(): Map<Product, Image> =
        productDao.getProductsWithImage()

    suspend fun getProductsByGenreAndType(genre: String, type: String): List<Product> =
        productDao.getProductsByGenreAndType(genre, type)

    suspend fun getColorsBySize(productId: Int, size: Double): List<String> =
        productDao.getColorsBySize(productId, size)

    suspend fun getSizesByColor(productId: Int, color: String): List<Double> =
        productDao.getSizesByColor(productId, color)

    suspend fun getProductData(productId: Int, userEmail: String): ProductDetail? =
        productDao.getProductData(productId, userEmail)

    suspend fun insertProduct(product: Product) {
        productDao.insertProduct(product)
    }

    suspend fun getProductsHistory(): List<HistoryProduct> =
        productDao.getProductsHistory()

    suspend fun insertProductsHistory(remoteHistory: List<HistoryProduct>) {
        productDao.insertProductsHistory(remoteHistory)
    }

    suspend fun getPopularProducts(): List<Product> =
        productDao.getPopularProducts()

    suspend fun getNewProducts(): List<Product> =
        productDao.getNewProducts()

    suspend fun getDiscountedProducts(): List<Product> =
        productDao.getDiscountedProducts()

    suspend fun searchProducts(query: String): List<ProductWithImage> =
        productDao.searchProducts(query)
}