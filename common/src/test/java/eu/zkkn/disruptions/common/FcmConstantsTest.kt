package eu.zkkn.disruptions.common

import org.junit.Assert.assertEquals
import org.junit.Test


class FcmConstantsTest {

    @Test
    fun topicNameForLine() {
        assertEquals("topic_pid_a", FcmConstants.topicNameForLine("a"))
        assertEquals("topic_pid_b", FcmConstants.topicNameForLine("B"))
        assertEquals("topic_pid_c", FcmConstants.topicNameForLine(" C "))
    }

    @Test
    fun topicNameHeartbeat() {
        assertEquals("topic_admin_heartbeat", FcmConstants.topicNameHeartbeat)
    }

}
