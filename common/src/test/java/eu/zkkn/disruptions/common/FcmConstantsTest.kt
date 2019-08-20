package eu.zkkn.disruptions.common

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.matchesPattern
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
        assertEquals("topic_admin_heartbeat", FcmConstants.TOPIC_HEARTBEAT)
    }

    @Test
    fun topicNameForLine_length() {
        val short = FcmConstants.topicNameForLine("0123456789012345678901234567890123456789") //40 characters
        assertEquals("topic_pid_0123456789012345678901234567890123456789", short)
        assertEquals(50, short.length)
        assertThat(short, matchesPattern("^[a-zA-Z0-9-_.~%]{1,50}\$"))

        val long = FcmConstants.topicNameForLine("01234567890123456789012345678901234567891") // 41 characters
        assertEquals("topic_pid_0123456789012345678901234567890123456789", long)
        assertEquals(50, long.length)
        assertThat(long, matchesPattern("^[a-zA-Z0-9-_.~%]{1,50}\$"))

        val shortWithSpaces =  FcmConstants.topicNameForLine(" 0123456789012345678901234567890123456789 ")
        assertEquals("topic_pid_0123456789012345678901234567890123456789", shortWithSpaces)
        assertEquals(50, shortWithSpaces.length)
        assertThat(shortWithSpaces, matchesPattern("^[a-zA-Z0-9-_.~%]{1,50}\$"))
    }

    @Test
    fun topicNameForLine_space() {
        val topic = FcmConstants.topicNameForLine("Cyklo Brdy")
        assertEquals("topic_pid_cyklo-brdy", topic)
        assertThat(topic, matchesPattern("^[a-zA-Z0-9-_.~%]{1,50}\$"))

        assertEquals("topic_pid_cyklo-brdy", FcmConstants.topicNameForLine(" Cyklo Brdy "))
    }

    @Test
    fun topicNameForLine_diacritics() {
        val topic = FcmConstants.topicNameForLine("Cyklohráček")
        assertThat(topic, matchesPattern("^[a-zA-Z0-9-_.~%]{1,50}\$"))
        assertEquals("topic_pid_cyklohracek", topic)

        assertEquals("topic_pid_a-z", FcmConstants.topicNameForLine("\u00C1-\u017E"))

        val diacritics = FcmConstants.topicNameForLine("Test ÁáČčĎďÉéĚěÍíŇňÓóŘřŠšŤťÚúŮůÝýŽž")
        assertThat(diacritics, matchesPattern("^[a-zA-Z0-9-_.~%]{1,50}\$"))
        assertEquals("topic_pid_test-aaccddeeeeiinnoorrssttuuuuyyzz", diacritics)
    }

}
