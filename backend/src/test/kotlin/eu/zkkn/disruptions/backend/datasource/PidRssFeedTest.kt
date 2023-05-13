package eu.zkkn.disruptions.backend.datasource

import eu.zkkn.disruptions.backend.MyGson
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime
import java.time.Month

class PidRssFeedTest {

    @Test
    fun gsonSerialization() {
        //val jsonWithNull = MyGson.get().toJson(PidRssFeed("test-1", null, emptyList()))
        //assertEquals("""{"title":"test-1","updated":null,"items":[]}""", jsonWithNull)

        val json = MyGson.get().toJson(PidRssFeed("test-2", LocalDateTime.of(2023, Month.MAY, 13, 17, 8), emptyList()))
        assertEquals("""{"title":"test-2","updated":"2023-05-13T17:08:00","items":[]}""", json)
    }

    @Test
    fun gsonDeserialization() {
        //val objWithNull = MyGson.get().fromJson("""{"title":"test-1","updated":null,"items":[]}""", PidRssFeed::class.java)
        //assertEquals(PidRssFeed("test-1", null, emptyList()), objWithNull)

        //val objWithoutProp = MyGson.get().fromJson("""{"title":"test-1","items":[]}""", PidRssFeed::class.java)
        //assertEquals(PidRssFeed("test-1", null, emptyList()), objWithoutProp)

        val obj2 = MyGson.get().fromJson("""{"title":"test-2","updated":"2023-05-13T17:08:00","items":[]}""", PidRssFeed::class.java)
        assertEquals(PidRssFeed("test-2", LocalDateTime.of(2023, Month.MAY, 13, 17, 8, 0, 0), emptyList()), obj2)
    }
}
