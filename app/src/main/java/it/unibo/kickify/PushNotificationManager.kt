package it.unibo.kickify

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class PushNotificationManager(
    private val activity: Activity,
    private val context: Context
) {
    private val channelID = "kickifyNotifications1"
    private val notificationManager = this.context.getSystemService(NotificationManager::class.java)

    private var lastUsedNotificationID = 0

    init {
        checkNotificationPermission()
        createNotificationChannel()
    }

    /* CRASHES APP
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(this.activity, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this.activity,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }
    }*/

    fun checkNotificationPermission(): Boolean {
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

        //val notificationManager = this.context.getSystemService(NotificationManager::class.java)
        val notificationManager = this.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun sendNotificationNoAction(
        notificationTitle:String,
        notificationMessage: String,
        icon: Int = R.drawable.ic_launcher_foreground,
        priority: Int = NotificationCompat.PRIORITY_HIGH,
        // actionOnClick: PendingIntent,
        setAutoCancelNotification: Boolean = true
    ){
        val notification = NotificationCompat.Builder(this.context, this.channelID)
            .setContentTitle(notificationTitle)
            .setContentText(notificationMessage)
            .setSmallIcon(icon)
            .setPriority(priority)
            //.setContentIntent(pendingIntent)
            .setAutoCancel(setAutoCancelNotification)
            .build()

        notificationManager.notify(getNotificationID(), notification)
    }

    fun sendNotificationWithActionTEST() {
        val intent = Intent(this.context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this.context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this.context, this.channelID)
            .setContentTitle("Notifica con Azione")
            .setContentText("Tocca per aprire l'app")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
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