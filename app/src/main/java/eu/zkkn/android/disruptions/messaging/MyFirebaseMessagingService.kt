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
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import eu.zkkn.android.disruptions.CancelNotificationReceiver
import eu.zkkn.android.disruptions.R
import eu.zkkn.android.disruptions.data.DisruptionRepository
import eu.zkkn.android.disruptions.ui.disruptiondetail.DisruptionDetailFragmentArgs
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
            val guid = data[FcmConstants.KEY_ID]!!.trim()
            val lines = data[FcmConstants.KEY_LINES]!!.split(',').map { it.trim() }
            val title = data[FcmConstants.KEY_TITLE]!!
            val timeInfo = data[FcmConstants.KEY_TIME]!!

            DisruptionRepository.getInstance(this).addDisruption(guid, lines.toSet(), title, timeInfo)

            val notificationId = guid.replace("[^0-9]".toRegex(), "0").toIntOrNull() ?: 1 //TODO do it better
            val notificationTitle = resources.getQuantityString(
                R.plurals.notification_lines, lines.size, lines.joinToString())
            val bigText = "$title\n$timeInfo"
            showNotification(notificationId, guid, notificationTitle, title, bigText)
        }
    }

    override fun onNewToken(token: String?) {
        Log.d(TAG, "Refreshed token: $token")
        //TODO: ??? maybe resubscribe to topics
    }

    private fun showNotification(id: Int, guid: String, title: String, text: String, bigText: String) {
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

        val pendingIntent = NavDeepLinkBuilder(this)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.disruptionFragment)
            .setArguments(DisruptionDetailFragmentArgs(guid).toBundle())
            .createPendingIntent()
        builder.setContentIntent(pendingIntent)

        val actionWeb = Intent(Intent.ACTION_VIEW, Uri.parse("https://pid.cz/mimoradnost/?id=$guid"))
        if (actionWeb.resolveActivity(packageManager) != null) {
            builder.addAction(R.drawable.ic_open_browser, getString(R.string.notification_action_detail),
                //TODO: shouldn't be the requestCode and flags of PendingIntent set similarly
                // as in PendingIntent for actionCancel
                PendingIntent.getActivity(this, 0, actionWeb, 0))
        }

        val actionCancel = CancelNotificationReceiver.getIntent(this, id)
        builder.addAction(R.drawable.ic_notification_clear, getString(R.string.notification_action_cancel),
            PendingIntent.getBroadcast(this, id, actionCancel, PendingIntent.FLAG_UPDATE_CURRENT))

        notifications.notify(id, builder.build())
    }

}
