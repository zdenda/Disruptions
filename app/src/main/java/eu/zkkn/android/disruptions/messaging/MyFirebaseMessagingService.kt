package eu.zkkn.android.disruptions.messaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import eu.zkkn.android.disruptions.R


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = MyFirebaseMessagingService::class.simpleName
    private val disruptionsChannelId = "disruptions"

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.d(TAG, "New Message From: ${remoteMessage?.from}")
        if (remoteMessage == null) return

        val data = remoteMessage.data

        if ("notification" == data["type"]) {
            //TODO add validation that all data fields exists and have values
            val id = data["id"]!!.trim().replace("[^0-9]".toRegex(), "0").toIntOrNull() ?: 1 //TODO do it better
            val lines = data["lines"]!!.split(',').map { it.trim() }
            val title = resources.getQuantityString(R.plurals.notification_lines, lines.size, lines.joinToString())
            val bigText = "${data["title"]}\n${data["time"]}"
            showNotification(id, title, data["title"]!!, bigText)
        }
    }

    override fun onNewToken(token: String?) {
        Log.d(TAG, "Refreshed token: $token")
        //TODO: ??? maybe resubscribe to topics
    }

    private fun showNotification(id: Int, title: String, text: String, bigText: String) {
        val notifications = NotificationManagerCompat.from(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //TODO add channel description
            notifications.createNotificationChannel(NotificationChannel(disruptionsChannelId,
                getString(R.string.notification_channel_disruptions_name), NotificationManager.IMPORTANCE_DEFAULT))
        }

        val notification = NotificationCompat.Builder(this, disruptionsChannelId)
                .setSmallIcon(android.R.drawable.ic_dialog_alert) //TODO: proper icon
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
                .build()

        notifications.notify(id, notification)
    }

}
