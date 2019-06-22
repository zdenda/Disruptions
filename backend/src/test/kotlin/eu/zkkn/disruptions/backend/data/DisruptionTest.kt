package eu.zkkn.disruptions.backend.data

import eu.zkkn.disruptions.backend.datasource.PidRssFeed
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.RuntimeException
import java.text.SimpleDateFormat
import java.util.Date


class DisruptionTest {

    @Test
    fun fromPidRssFeedItem_FillInCreated() {
        val feedItem = PidRssFeed.Item(
            "guid-1", "Test disruption", "1.6. 12:00 - 1.6. 13:00", listOf("A", "1", "100")
        )

        val before = Date()

        val disruption = Disruption.fromPidRssFeedItem(feedItem)

        val after = Date()

        assertEquals(feedItem.guid, disruption.guid)
        assertEquals(feedItem.title, disruption.title)
        assertEquals(feedItem.timeInfo, disruption.timeInfo)
        assertThat(disruption.lines, containsInAnyOrder("A", "1", "100"))
        assertNull(disruption.updated)
        assertTrue(disruption.created >= before)
        assertTrue(disruption.created <= after)
    }

    @Test
    fun fromPidRssFeedItem_SetCreated() {
        val feedItem = PidRssFeed.Item(
            "guid-2", "Test disruption 2", "1.6. 12:00 - 1.6. 13:00", listOf("B", "2", "101")
        )
        val created = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2019-06-01T11:59:59")

        val disruption = Disruption.fromPidRssFeedItem(feedItem, created)

        assertEquals(feedItem.guid, disruption.guid)
        assertEquals(feedItem.title, disruption.title)
        assertEquals(feedItem.timeInfo, disruption.timeInfo)
        assertThat(disruption.lines, containsInAnyOrder("B", "2", "101"))
        assertEquals(created, disruption.created)
        assertNull(disruption.updated)
    }

    @Test
    fun modify() {
        val disruption = Disruption(
            1,
            "guid-a",
            mutableSetOf("1", "2"),
            "Mimořádnost na linkách 1 a 2",
            "1.6. 12:00 - do odvolání",
            Date(),
            null
        )

        val feedItem = PidRssFeed.Item(
            "guid-a",
            "Mimořádnost na linkách 1, 2 a 3",
            "1.6. 12:00 - 1.6. 13:00",
            listOf("3", "2", "1")
        )

        val before = Date()

        disruption.modify(feedItem)

        val after = Date()

        assertEquals(feedItem.guid, disruption.guid)
        assertEquals(feedItem.title, disruption.title)
        assertEquals(feedItem.timeInfo, disruption.timeInfo)
        assertThat(disruption.lines, containsInAnyOrder("1", "2", "3"))
        assertTrue(disruption.created <= before)
        assertNotNull(disruption.updated)
        assertTrue(disruption.updated!! >= after)
        assertTrue(disruption.updated!! <= after)
    }

    @Test(expected = RuntimeException::class)
    fun modify_guidConflict() {
        val disruption = Disruption(
            1,
            "guid-a",
            mutableSetOf("1", "2"),
            "Mimořádnost na linkách 1 a 2",
            "1.6. 12:00 - do odvolání",
            Date(),
            null
        )

        val feedItem = PidRssFeed.Item(
            "guid-1",
            "Mimořádnost na linkách 1, 2 a 3",
            "1.6. 12:00 - 1.6. 13:00",
            listOf("3", "2", "1")
        )

        disruption.modify(feedItem)
    }

    @Test
    fun setOperations() {
        val list = listOf("A", "A", "B", "C", "1", "100", "100")
        val set = list.toMutableSet()

        assertEquals(5, set.size)
        assertThat(set, containsInAnyOrder("A", "B", "C", "1", "100"))

        set.removeAll(mutableSetOf("1", "100"))

        assertEquals(3, set.size)
        assertThat(set, containsInAnyOrder("A", "B", "C"))

        set.removeAll(emptyList())
        set.removeAll(emptySet())

        assertEquals(3, set.size)
        assertThat(set, containsInAnyOrder("A", "B", "C"))

        set.removeAll(mutableSetOf("A", "B", "C"))

        assertFalse(set.isNotEmpty())
    }

}
