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
import com.kaz_furniture.mahjongChat.activity.DMDetailActivity
import com.kaz_furniture.mahjongChat.activity.MainActivity
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
        }
    }

    override fun onNewToken(p0: String) {
    }

    companion object {
        private const val KEY_ID = "KEY_ID"
        private const val TYPE_DM_MESSAGE = "0"
        private const val CHANNEL_ID_0 = "channel_id_0"
    }
}