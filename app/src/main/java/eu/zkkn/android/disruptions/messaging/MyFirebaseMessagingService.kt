package eu.zkkn.android.disruptions.messaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import eu.zkkn.android.disruptions.R
import eu.zkkn.disruptions.common.FcmConstants


class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private val TAG = MyFirebaseMessagingService::class.simpleName
    }


    private val disruptionsChannelId = "disruptions"


    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.d(TAG, "New Message From: ${remoteMessage?.from}")
        if (remoteMessage == null) return

        val data = remoteMessage.data

        @FcmConstants.FcmMessageType
        val messageType = data[FcmConstants.KEY_TYPE]

        if (FcmConstants.TYPE_NOTIFICATION == messageType) {
            //TODO add validation that all data fields exists and have values
            val id = data[FcmConstants.KEY_ID]!!.trim().replace("[^0-9]".toRegex(), "0").toIntOrNull() ?: 1 //TODO do it better
            val lines = data[FcmConstants.KEY_LINES]!!.split(',').map { it.trim() }
            val title = resources.getQuantityString(R.plurals.notification_lines, lines.size, lines.joinToString())
            val bigText = "${data[FcmConstants.KEY_TITLE]}\n${data[FcmConstants.KEY_TIME]}"
            val url = Uri.parse("https://pid.cz/mimoradnost/?id=${data[FcmConstants.KEY_ID]!!.trim()}")
            showNotification(id, title, data[FcmConstants.KEY_TITLE]!!, bigText, url)
        }
    }

    override fun onNewToken(token: String?) {
        Log.d(TAG, "Refreshed token: $token")
        //TODO: ??? maybe resubscribe to topics
    }

    private fun showNotification(id: Int, title: String, text: String, bigText: String, url: Uri) {
        val notifications = NotificationManagerCompat.from(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //TODO add channel description
            notifications.createNotificationChannel(NotificationChannel(disruptionsChannelId,
                getString(R.string.notification_channel_disruptions_name), NotificationManager.IMPORTANCE_DEFAULT))
        }

        val builder = NotificationCompat.Builder(this, disruptionsChannelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))

        val action = Intent(Intent.ACTION_VIEW, url)
        if (action.resolveActivity(packageManager) != null) {
            builder.addAction(R.drawable.ic_open_browser, getString(R.string.notification_action_detail),
                PendingIntent.getActivity(this, 0, action, 0))
        }

        notifications.notify(id, builder.build())
    }

}
