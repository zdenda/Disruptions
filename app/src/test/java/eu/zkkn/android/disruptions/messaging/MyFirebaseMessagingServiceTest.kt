package eu.zkkn.android.disruptions.messaging

import eu.zkkn.disruptions.common.FcmConstants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test


class MyFirebaseMessagingServiceTest {

    @Test
    fun validateNotificationData_validData() {
        val data = mapOf(
            FcmConstants.KEY_ID to "guid-123",
            FcmConstants.KEY_LINES to "A, B",
            FcmConstants.KEY_TITLE to "Disruption",
            FcmConstants.KEY_TIME to "1.1. 10:00"
        )
        val result = MyFirebaseMessagingService.validateNotificationData(data)
        assertNotNull(result)
        assertEquals("guid-123", result!!.guid)
        assertEquals(listOf("A", "B"), result.lines)
        assertEquals("Disruption", result.title)
        assertEquals("1.1. 10:00", result.timeInfo)
    }

    @Test
    fun validateNotificationData_missingField() {
        val data = mapOf(FcmConstants.KEY_ID to "guid-123")
        assertNull(MyFirebaseMessagingService.validateNotificationData(data))
    }

    @Test
    fun validateNotificationData_emptyLines() {
        val data = mapOf(
            FcmConstants.KEY_ID to "guid-123",
            FcmConstants.KEY_LINES to "  ,  , ",
            FcmConstants.KEY_TITLE to "Disruption",
            FcmConstants.KEY_TIME to "1.1. 10:00"
        )
        assertNull(MyFirebaseMessagingService.validateNotificationData(data))
    }

    @Test
    fun validateNotificationData_blankFields() {
        val data = mapOf(
            FcmConstants.KEY_ID to "   ",
            FcmConstants.KEY_LINES to "A",
            FcmConstants.KEY_TITLE to "Disruption",
            FcmConstants.KEY_TIME to "1.1. 10:00"
        )
        assertNull(MyFirebaseMessagingService.validateNotificationData(data))
    }
}
