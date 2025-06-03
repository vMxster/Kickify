package it.unibo.kickify.data.repositories.local

import it.unibo.kickify.data.database.NotificationState
import it.unibo.kickify.data.database.NotificationStateDao

class NotificationStateRepository (private val notificationStateDao: NotificationStateDao) {
    suspend fun initNotificationStates(notificationStates: List<NotificationState>) {
        notificationStates.forEach { notificationState ->
            notificationStateDao.addNotificationState(notificationState)
        }
    }
}