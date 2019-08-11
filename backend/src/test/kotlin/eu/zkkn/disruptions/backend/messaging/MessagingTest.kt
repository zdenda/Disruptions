package eu.zkkn.disruptions.backend.messaging

import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.Message
import eu.zkkn.disruptions.backend.datasource.PidRssFeed
import eu.zkkn.disruptions.common.FcmConstants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test
import kotlin.reflect.jvm.isAccessible


class MessagingTest {

    /*
    @Test
    fun prepareHeartbeatMessage() {
        val heartbeatMsg = Messaging.prepareHeartbeatMessage()

        assertEquals("topic_admin_heartbeat", callMessageMethod(heartbeatMsg, "getTopic"))

        val data: Map<String, String>? = callMessageMethod(heartbeatMsg, "getData")
        assertNotNull(data)

        assertEquals("heartbeat", data!![FcmConstants.KEY_TYPE])
    }
    */

    @Test
    fun prepareNotificationMessages_single() {

        val messages = Messaging.prepareNotificationMessages(
            setOf("A"),
            PidRssFeed.Item("guid-1", "Mimořádnost Metro A", "1.8. 20:00 - do odvolání", listOf("A"))
        )

        assertFalse(messages.isEmpty())
        assertEquals(1, messages.size)

        val message = messages.first()

        assertEquals("topic_pid_a", callMessageMethod(message, "getTopic"))

        val data: Map<String, String>? = callMessageMethod(message, "getData")
        assertNotNull(data)
        assertEquals("notification", data!!["type"])
        assertEquals("guid-1", data[FcmConstants.KEY_ID])
        assertEquals("Mimořádnost Metro A", data[FcmConstants.KEY_TITLE])
        assertEquals("1.8. 20:00 - do odvolání", data[FcmConstants.KEY_TIME])
        assertEquals("A", data["lines"])

        val androidConfig: AndroidConfig? = callMessageMethod(message, "getAndroidConfig")
        assertNotNull(androidConfig)

    }

    @Test
    fun prepareNotificationMessages_multiple() {

        val messages = Messaging.prepareNotificationMessages(
            setOf("1", "2"),
            PidRssFeed.Item("1234-5", "Trams 1 & 2", "1.8. 20:30 - 1.8. 21:00", listOf("1", "2"))
        )

        assertFalse(messages.isEmpty())
        assertEquals(2, messages.size)

        val firstMsg = messages.find { "topic_pid_1" == callMessageMethod<String>(it, "getTopic") }
        assertNotNull(firstMsg)
        val secondMsg = messages.find { "topic_pid_2" == callMessageMethod<String>(it, "getTopic") }
        assertNotNull(secondMsg)

        val firstData: Map<String, String>? = callMessageMethod(firstMsg!!, "getData")
        assertNotNull(firstData)
        assertEquals("notification", firstData!!["type"])
        assertEquals("1234-5", firstData["id"])
        assertEquals("Trams 1 & 2", firstData["title"])
        assertEquals("1.8. 20:30 - 1.8. 21:00", firstData["time"])
        assertEquals("1,2", firstData["lines"])

        val secondData: Map<String, String>? = callMessageMethod(secondMsg!!, "getData")
        assertNotNull(secondData)
        assertEquals("notification", secondData!![FcmConstants.KEY_TYPE])
        assertEquals("1234-5", secondData[FcmConstants.KEY_ID])
        assertEquals("Trams 1 & 2", secondData[FcmConstants.KEY_TITLE])
        assertEquals("1.8. 20:30 - 1.8. 21:00", secondData[FcmConstants.KEY_TIME])
        assertEquals("1,2", secondData[FcmConstants.KEY_LINES])

    }


    // Some useful methods on Message are not accessible, so we need to use reflection to call them (just for testing)
    private inline fun <reified T> callMessageMethod(message: Message, methodName: String): T? {
        val function = Message::class.members.find { it.name == methodName }
            ?: throw RuntimeException("Unknown method: $methodName()")
        function.let {
            it.isAccessible = true
            val result = it.call(message)
            return if (result is T?) result
            else throw RuntimeException("Wrong return type: ${T::class.java.canonicalName}")
        }
    }

}
