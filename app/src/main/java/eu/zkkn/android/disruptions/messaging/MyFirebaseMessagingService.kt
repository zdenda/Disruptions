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
            //TODO use names of lines as a title for notification
            showNotification(data["id"]!!, data["title"]!!, data["title"]!!)
        }
    }

    override fun onNewToken(token: String?) {
        Log.d(TAG, "Refreshed token: $token")
        //TODO: ??? maybe resubscribe to topics
    }

    private fun showNotification(id: String, title: String, text: String) {
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
                //TODO .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
                .build()

        notifications.notify(1, notification) //TODO: ID
    }

}
