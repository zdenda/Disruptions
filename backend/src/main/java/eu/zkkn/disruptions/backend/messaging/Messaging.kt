package eu.zkkn.disruptions.backend.messaging

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.FcmOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import eu.zkkn.disruptions.backend.ServletContextHolder
import eu.zkkn.disruptions.backend.datasource.PidRssFeed
import eu.zkkn.disruptions.common.FcmConstants


object Messaging {

    private val firebaseMessaging: FirebaseMessaging by lazy {
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
            // Quick fix for:
            // IllegalArgumentException: Analytics label must have format matching'^[a-zA-Z0-9-_.~%]{1,50}$
            // TODO: do it better
            if (!"[a-zA-Z0-9-_.~%]{1,50}".toRegex().matches(topicName)) continue
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


    fun send(messages: Set<Message>): Set<String> {
        val results = mutableSetOf<String>()
        for (message in messages) {
            results.add(send(message))
        }
        return results
    }

    fun send(message: Message): String {
        return firebaseMessaging.send(message)
    }

}
