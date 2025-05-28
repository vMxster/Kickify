package it.unibo.kickify.data.repositories.local

import it.unibo.kickify.data.database.*

class NotificationRepository(private val notificationDao: NotificationDao) {

    suspend fun createNotification(notification: Notification): Long =
        notificationDao.createNotification(notification)

    suspend fun getUserNotifications(email: String): List<Notification> =
        notificationDao.getUserNotifications(email)

    suspend fun markNotificationsAsRead(email: String, notificationIds: List<Int>): Int =
        notificationDao.markNotificationsAsRead(email, notificationIds)

    suspend fun getUnreadNotificationsCount(email: String): Int =
        notificationDao.getUnreadNotificationsCount(email)

    suspend fun addNotification(email: String, message: String, type: String): Long =
        notificationDao.addNotification(Notification(
            email = email,
            message = message,
            type = type,
            date = java.time.LocalDateTime.now().toString(),
            state = "Unread",
            notificationId = -1
        ))
}