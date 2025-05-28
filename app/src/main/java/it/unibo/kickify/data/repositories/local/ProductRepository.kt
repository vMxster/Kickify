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

    suspend fun insertProducts(remoteProducts: List<Product>) {
        productDao.insertProducts(remoteProducts)
    }

    suspend fun getProductHistory(productId: Int): List<HistoryProduct> =
        productDao.getProductHistory(productId)

    suspend fun insertProductHistory(remoteHistory: List<HistoryProduct>) {
        productDao.insertProductHistory(remoteHistory)
    }
}