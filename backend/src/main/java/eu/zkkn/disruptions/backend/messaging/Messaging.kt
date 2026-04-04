package eu.zkkn.disruptions.backend.messaging

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.FcmOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import eu.zkkn.disruptions.backend.ServletContextHolder
import eu.zkkn.disruptions.backend.Utils
import eu.zkkn.disruptions.backend.datasource.PidRssFeed
import eu.zkkn.disruptions.common.FcmConstants
import java.util.logging.Logger

object Messaging {

    private const val PREFIX_ERROR = "ERROR:"

    private val log = Logger.getLogger(Messaging::class.java.name)

    private val firebaseMessaging: FirebaseMessaging by lazy {
        //TODO: try GoogleCredentials.getApplicationDefault() if it works on App Engine and also on local development
        val googleCredentials = GoogleCredentials.fromStream(
            ServletContextHolder.getServletContext().getResourceAsStream("/WEB-INF/serviceAccountKey.json")
        )
        val options = FirebaseOptions.builder().setCredentials(googleCredentials).build()
        FirebaseMessaging.getInstance(FirebaseApp.initializeApp(options))
    }


    fun prepareNotificationMessages(lines: Set<String>, pidRssItem: PidRssFeed.Item): Set<Message> {
        val messages = mutableSetOf<Message>()
        // TODO: use lines.chunked(5) and send notification to multiple (up to 5) topics at once
        // https://firebase.google.com/docs/cloud-messaging/android/topic-messaging#build_send_requests
        for (line in lines) {
            val topicName = FcmConstants.topicNameForLine(line)
            val message = Message.builder()
                .putData(FcmConstants.KEY_TYPE, FcmConstants.TYPE_NOTIFICATION)
                .putData(FcmConstants.KEY_ID, pidRssItem.guid)
                .putData(FcmConstants.KEY_TITLE, pidRssItem.title)
                .putData(FcmConstants.KEY_TIME, pidRssItem.timeInfo)
                .putData(FcmConstants.KEY_LINES, pidRssItem.lines.joinToString(","))
                .setAndroidConfig(
                    AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .build()
                )
                .setFcmOptions(FcmOptions.withAnalyticsLabel(topicName))
                .setTopic(topicName)
                .build()
            messages.add(message)
        }
        return messages
    }

    fun prepareHeartbeatMessage(): Message {
        return Message.builder()
            .putData(FcmConstants.KEY_TYPE, FcmConstants.TYPE_HEARTBEAT)
            .setFcmOptions(FcmOptions.withAnalyticsLabel(FcmConstants.TOPIC_HEARTBEAT))
            .setTopic(FcmConstants.TOPIC_HEARTBEAT)
            .build()
    }


    fun send(messages: Set<Message>): List<String> {
        val results = mutableListOf<String>()
        for (message in messages) {
            results.add(send(message))
        }
        val failures = results.count { it.startsWith(PREFIX_ERROR) }
        if (failures > 0) {
            log.warning("$failures out of ${messages.size} FCM messages failed to send")
        }
        return results
    }

    fun send(message: Message): String {
        // perform only a dry run if not in production
        val dryRun = !Utils.isProduction()
        if (dryRun) log.warning("FCM messages are sent only from Production environment")
        return try {
            firebaseMessaging.send(message, dryRun)
        } catch (e: Exception) {
            log.severe("Failed to send FCM message: ${e.message}")
            "$PREFIX_ERROR ${e.message}"
        }
    }

}
