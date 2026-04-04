package eu.zkkn.android.disruptions.messaging

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import eu.zkkn.android.disruptions.BuildConfig
import eu.zkkn.android.disruptions.CancelNotificationReceiver
import eu.zkkn.android.disruptions.R
import eu.zkkn.android.disruptions.data.DisruptionRepository
import eu.zkkn.android.disruptions.data.Preferences
import eu.zkkn.android.disruptions.ui.disruptiondetail.DisruptionDetailFragmentArgs
import eu.zkkn.android.disruptions.utils.Analytics
import eu.zkkn.android.disruptions.utils.AppNotificationManager
import eu.zkkn.android.disruptions.utils.Crashlytics
import eu.zkkn.android.disruptions.utils.Helpers
import eu.zkkn.disruptions.common.FcmConstants
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private val TAG = MyFirebaseMessagingService::class.simpleName

        internal fun validateNotificationData(data: Map<String, String>): NotificationData? {
            val guid = data[FcmConstants.KEY_ID]?.trim()
            val linesStr = data[FcmConstants.KEY_LINES]
            val title = data[FcmConstants.KEY_TITLE]
            val timeInfo = data[FcmConstants.KEY_TIME]

            if (guid.isNullOrBlank() || linesStr.isNullOrBlank() ||
                title.isNullOrBlank() || timeInfo.isNullOrBlank()
            ) {
                return null
            }

            val lines = linesStr.split(',').map { it.trim() }.filter { it.isNotBlank() }
            if (lines.isEmpty()) return null

            return NotificationData(guid, lines, title, timeInfo)
        }
    }

    internal data class NotificationData(
        val guid: String,
        val lines: List<String>,
        val title: String,
        val timeInfo: String
    )


    private val appNotificationManager: AppNotificationManager by lazy {
        AppNotificationManager(this)
    }


    @WorkerThread
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "New Message From: ${remoteMessage.from}")

        @FcmConstants.FcmMessageType
        val messageType = remoteMessage.data[FcmConstants.KEY_TYPE]

        when (messageType) {
            FcmConstants.TYPE_NOTIFICATION -> handleNotificationMsg(remoteMessage)
            FcmConstants.TYPE_HEARTBEAT -> handleHeartbeatMsg(remoteMessage)
            else -> Log.w(TAG, "Unknown FCM message type: $messageType")
        }

        if (remoteMessage.priority != remoteMessage.originalPriority) {
            Analytics.logFcmPriorityChanged(remoteMessage.priority, remoteMessage.originalPriority)
        }
    }

    @WorkerThread
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        //TODO: ??? maybe resubscribe to topics
    }


    private fun handleNotificationMsg(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data

        val notificationData = validateNotificationData(data)
        if (notificationData == null) {
            val missingOrEmpty = listOfNotNull(
                if (data[FcmConstants.KEY_ID].isNullOrBlank()) FcmConstants.KEY_ID else null,
                if (data[FcmConstants.KEY_LINES].isNullOrBlank()) FcmConstants.KEY_LINES else null,
                if (data[FcmConstants.KEY_TITLE].isNullOrBlank()) FcmConstants.KEY_TITLE else null,
                if (data[FcmConstants.KEY_TIME].isNullOrBlank()) FcmConstants.KEY_TIME else null
            )
            val errorMessage = "Incomplete FCM notification data. Received keys: ${data.keys}. " +
                "Missing or empty: $missingOrEmpty"
            Log.w(TAG, errorMessage)
            Crashlytics.log(errorMessage)
            Crashlytics.logException(RuntimeException(errorMessage))
            return
        }

        val (guid, lines, title, timeInfo) = notificationData

        DisruptionRepository.getInstance(this).addDisruption(guid, lines.toSet(), title, timeInfo)

        val notificationId = guid.hashCode().and(Int.MAX_VALUE)
        val notificationTitle = resources.getQuantityString(
            R.plurals.notification_lines, lines.size, lines.joinToString()
        )
        val bigText = "$title\n$timeInfo"
        showNotification(notificationId, guid, notificationTitle, title, bigText)
    }

    private fun showNotification(id: Int, guid: String, title: String, text: String, bigText: String) {
        val builder = NotificationCompat.Builder(this, AppNotificationManager.DISRUPTIONS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ContextCompat.getColor(this, R.color.disruption))
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))

        val pendingIntent = NavDeepLinkBuilder(this)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.disruptionFragment)
            .setArguments(DisruptionDetailFragmentArgs(guid).toBundle())
            .createPendingIntent()
        builder.setContentIntent(pendingIntent)

        // View on web action
        val viewOnWeb = Intent(Intent.ACTION_VIEW, Uri.parse("https://pid.cz/mimoradnost/?id=$guid"))
        if (viewOnWeb.resolveActivity(packageManager) != null) {
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            val viewOnWebPendingIntent = PendingIntent.getActivity(this, 0, viewOnWeb, flags)
            builder.addAction(
                R.drawable.ic_open_browser, getString(R.string.notification_action_detail),
                viewOnWebPendingIntent
            )
        }

        // Cancel notification action
        builder.addAction(CancelNotificationReceiver.getCancelNotificationAction(this, id))

        appNotificationManager.notify(id, builder.build())
    }

    private fun handleHeartbeatMsg(remoteMessage: RemoteMessage) {
        // save times from Heartbeat
        val received = System.currentTimeMillis()
        val sent = remoteMessage.sentTime
        Preferences.setLastHeartbeatReceivedTime(this, received)
        Preferences.setLastHeartbeatSentTime(this, sent)

        // show notification only in debug builds
        if (!BuildConfig.DEBUG) return
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val shortMessage = "Received: ${dateFormat.format(Date(received))};\n"
        val longMessage = shortMessage + "Sent: ${dateFormat.format(Date(sent))};\n" +
            "Standby Bucket: ${Helpers.getAppStandbyBucket(this)};\n"
        showHeartBeatNotification(shortMessage, longMessage)
    }

    private fun showHeartBeatNotification(text: String, bigText: String) {
        val id = -1
        val builder = NotificationCompat.Builder(this, AppNotificationManager.DISRUPTIONS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ContextCompat.getColor(this, R.color.info))
            .setContentTitle("Heartbeat")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))

        builder.addAction(CancelNotificationReceiver.getCancelNotificationAction(this, id))

        appNotificationManager.notify(id, builder.build())
    }

}
