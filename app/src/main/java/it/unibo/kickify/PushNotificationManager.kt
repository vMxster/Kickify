package it.unibo.kickify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class PushNotificationManager(private val context: Context) {
    private val channelID = "kickifyNotifications1"
    private val notificationManager = this.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager//this.context.getSystemService(NotificationManager::class.java)

    private var lastUsedNotificationID = 0

    init {
        isPermissionGranted()
        createNotificationChannel()
    }

    fun isPermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= 33) { // in API >= 33 required to give permission
            return ContextCompat.checkSelfPermission(
                this.context, android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        }
        return true // in versions before 33, permission always granted
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            this.channelID, // notification channel ID
            getChannelVisibleNameString(), // notification channel name, visible in settings
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = getChannelVisibleDescriptionString() // description visible in settings
        }

        notificationManager.createNotificationChannel(channel)
    }

    fun sendNotification(
        notificationTitle:String,
        notificationMessage: String,
        icon: Int = R.drawable.kickify_logo,
        priority: Int = NotificationCompat.PRIORITY_HIGH
    ){
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, Intent(),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this.context, this.channelID)
            .setContentTitle(notificationTitle)
            .setContentText(notificationMessage)
            .setSmallIcon(icon)
            .setPriority(priority)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(getNotificationID(), notification)
    }

    private fun getNotificationID() : Int {
        this.lastUsedNotificationID += 1
        return this.lastUsedNotificationID
    }

    private fun getChannelVisibleNameString() : String{
        return context.getString(R.string.notificationManager_channelVisibleName)
    }

    private fun getChannelVisibleDescriptionString() : String{
        return context.getString(R.string.notificationManager_channelVisibleDescription)
    }
}