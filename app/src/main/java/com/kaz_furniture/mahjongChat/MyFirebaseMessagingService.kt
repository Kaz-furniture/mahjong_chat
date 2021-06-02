package com.kaz_furniture.mahjongChat

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kaz_furniture.mahjongChat.activity.*
import timber.log.Timber

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        super.onMessageReceived(remoteMessage)
        val data = remoteMessage.data
        Timber.d("remoteData = $data")
        when (data["type"]) {
            TYPE_DM_MESSAGE -> {
                val resultIntent = Intent(this, DMDetailActivity::class.java).apply {
                    putExtra(KEY_ID, data["id"])
                }
                val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                    addNextIntentWithParentStack(resultIntent)
                    getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                }
                val notification = NotificationCompat.Builder(this, CHANNEL_ID_0)
                    .setSmallIcon(R.drawable.ic_baseline_email_24)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true)
                    .setContentTitle(data["key1"])
                    .setContentText(data["key2"])
                    .build()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Create the NotificationChannel
                    val name = getString(R.string.channel_name_0)
                    val descriptionText = getString(R.string.channel_description_0)
                    val importance = NotificationManager.IMPORTANCE_DEFAULT
                    val mChannel = NotificationChannel(CHANNEL_ID_0, name, importance)
                    mChannel.description = descriptionText
                    // Register the channel with the system; you can't change the importance
                    // or other notification behaviors after this
                    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.createNotificationChannel(mChannel)
                }
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(0, notification)
            }
            TYPE_FOLLOWED -> {
                val resultIntent = Intent(this, ProfileActivity::class.java).apply {
                    putExtra(KEY_ID, data["id"])
                }
                val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                    addNextIntentWithParentStack(resultIntent)
                    getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                }
                val notification = NotificationCompat.Builder(this, CHANNEL_ID_1)
                    .setSmallIcon(R.drawable.ic_baseline_email_24)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true)
                    .setContentTitle(data["key1"])
                    .setContentText(data["key2"])
                    .build()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Create the NotificationChannel
                    val name = getString(R.string.channel_name_1)
                    val descriptionText = getString(R.string.channel_description_1)
                    val importance = NotificationManager.IMPORTANCE_DEFAULT
                    val mChannel = NotificationChannel(CHANNEL_ID_1, name, importance)
                    mChannel.description = descriptionText
                    // Register the channel with the system; you can't change the importance
                    // or other notification behaviors after this
                    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.createNotificationChannel(mChannel)
                }
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(1, notification)
            }
            TYPE_FAVORITE -> {
                val resultIntent = Intent(this, SplashActivity::class.java)
                val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                    addNextIntentWithParentStack(resultIntent)
                    getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                }
                val notification = NotificationCompat.Builder(this, CHANNEL_ID_2)
                    .setSmallIcon(R.drawable.ic_baseline_star_24)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true)
                    .setContentTitle(data["key1"])
                    .setContentText(data["key2"])
                    .build()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Create the NotificationChannel
                    val name = getString(R.string.channel_name_2)
                    val descriptionText = getString(R.string.channel_description_2)
                    val importance = NotificationManager.IMPORTANCE_DEFAULT
                    val mChannel = NotificationChannel(CHANNEL_ID_2, name, importance)
                    mChannel.description = descriptionText
                    // Register the channel with the system; you can't change the importance
                    // or other notification behaviors after this
                    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.createNotificationChannel(mChannel)
                }
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(2, notification)
            }
            TYPE_COMMENT -> {
                val resultIntent = Intent(this, SplashActivity::class.java)
                val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                    addNextIntentWithParentStack(resultIntent)
                    getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                }
                val notification = NotificationCompat.Builder(this, CHANNEL_ID_3)
                    .setSmallIcon(R.drawable.ic_baseline_comment_24)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true)
                    .setContentTitle(data["key1"])
                    .setContentText(data["key2"])
                    .build()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Create the NotificationChannel
                    val name = getString(R.string.channel_name_3)
                    val descriptionText = getString(R.string.channel_description_3)
                    val importance = NotificationManager.IMPORTANCE_DEFAULT
                    val mChannel = NotificationChannel(CHANNEL_ID_3, name, importance)
                    mChannel.description = descriptionText
                    // Register the channel with the system; you can't change the importance
                    // or other notification behaviors after this
                    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.createNotificationChannel(mChannel)
                }
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(3, notification)

            }
        }
    }

    override fun onNewToken(p0: String) {
    }

    companion object {
        private const val KEY_ID = "KEY_ID"
        private const val TYPE_DM_MESSAGE = "0"
        private const val TYPE_FOLLOWED = "1"
        private const val TYPE_FAVORITE = "2"
        private const val TYPE_COMMENT = "3"
        private const val CHANNEL_ID_0 = "channel_id_0"
        private const val CHANNEL_ID_1 = "channel_id_1"
        private const val CHANNEL_ID_2 = "channel_id_2"
        private const val CHANNEL_ID_3 = "channel_id_3"
    }
}