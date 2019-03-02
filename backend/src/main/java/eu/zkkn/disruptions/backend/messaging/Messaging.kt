package eu.zkkn.disruptions.backend.messaging

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import eu.zkkn.disruptions.backend.ServletContextHolder
import eu.zkkn.disruptions.backend.datasource.PidRssFeed


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


        fun prepareMessages(pidRssItem: PidRssFeed.Item): Set<Message> {
            val messages = mutableSetOf<Message>()
            for (line in pidRssItem.lines) {
                val message = Message.builder()
                    .putData("type", "notification")
                    .putData("id", pidRssItem.guid)
                    .putData("title", pidRssItem.title)
                    .putData("time", pidRssItem.timeInfo)
                    .putData("lines", pidRssItem.lines.joinToString(","))
                    .setAndroidConfig(
                        AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build()
                    )
                    .setTopic(topic(line))
                    .build()
                messages.add(message)
            }
            return messages
        }

        private fun topic(line: String): String {
            // remove spaces a use lower case for the name of line
            return "topic_pid_${line.trim().toLowerCase()}"
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
