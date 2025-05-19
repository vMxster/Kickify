package it.unibo.kickify.data.repositories

import it.unibo.kickify.data.database.*

class NotificationRepository(private val notificationDao: NotificationDao) {

    suspend fun createNotification(notification: Notification): Long =
        notificationDao.createNotification(notification)

    suspend fun getUserNotifications(email: String): List<NotificationWithMessage> =
        notificationDao.getUserNotifications(email)

    suspend fun markAllNotificationsAsRead(email: String): Int =
        notificationDao.markAllNotificationsAsRead(email)

    suspend fun markNotificationsAsRead(email: String, notificationIds: List<Int>): Int =
        notificationDao.markNotificationsAsRead(email, notificationIds)

    suspend fun getUnreadNotificationsCount(email: String): Int =
        notificationDao.getUnreadNotificationsCount(email)
}