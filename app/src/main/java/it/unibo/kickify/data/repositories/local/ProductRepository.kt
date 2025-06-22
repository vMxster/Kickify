package it.unibo.kickify.data.repositories.local

import it.unibo.kickify.data.database.*

class ProductRepository(private val productDao: ProductDao) {

    suspend fun getProductWithVariants(productId: Int): CompleteProduct? =
        productDao.getProductWithVariants(productId)

    suspend fun getProductImages(productId: Int): List<Image> =
        productDao.getProductImages(productId)

    suspend fun getProductsWithImage(): Map<Product, Image> =
        productDao.getProductsWithImage()

    suspend fun insertProduct(product: Product) {
        productDao.insertProduct(product)
    }

    suspend fun getProductsHistory(): List<HistoryProduct> =
        productDao.getProductsHistory()

    suspend fun insertProductsHistory(remoteHistory: List<HistoryProduct>) {
        productDao.insertProductsHistory(remoteHistory)
    }

    suspend fun getNewProducts(): List<Product> =
        productDao.getNewProducts()

    suspend fun searchProducts(query: String): List<ProductWithImage> =
        productDao.searchProducts(query)
}