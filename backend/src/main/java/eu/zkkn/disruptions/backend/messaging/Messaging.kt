package eu.zkkn.disruptions.backend.messaging

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import eu.zkkn.disruptions.backend.ServletContextHolder
import eu.zkkn.disruptions.backend.datasource.PidRssFeed
import eu.zkkn.disruptions.common.FcmConstants


class Messaging {

    companion object {

        private val firebaseMessaging: FirebaseMessaging

        init {
            val googleCredentials = GoogleCredentials.fromStream(
                ServletContextHolder.getServletContext().getResourceAsStream("/WEB-INF/serviceAccountKey.json")
            )
            val options = FirebaseOptions.builder().setCredentials(googleCredentials).build()
            firebaseMessaging = FirebaseMessaging.getInstance(FirebaseApp.initializeApp(options))
        }


        fun prepareMessages(lines: Set<String>, pidRssItem: PidRssFeed.Item): Set<Message> {
            val messages = mutableSetOf<Message>()
            // TODO: use lines.chunked(5) and send notification to multiple (up to 5) topics at once
            // https://firebase.google.com/docs/cloud-messaging/android/topic-messaging#build_send_requests
            for (line in lines) {
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
                    .setTopic(FcmConstants.topicNameForLine(line))
                    .build()
                messages.add(message)
            }
            return messages
        }

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
