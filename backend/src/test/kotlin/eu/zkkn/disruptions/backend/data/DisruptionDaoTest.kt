package eu.zkkn.disruptions.backend.data

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig
import com.google.appengine.tools.development.testing.LocalServiceTestHelper
import com.googlecode.objectify.Objectify
import com.googlecode.objectify.ObjectifyService
import com.googlecode.objectify.util.Closeable
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date


class DisruptionDaoTest {

    private val METRO_A inline get() = Disruption(null, "guid-a", mutableSetOf("A"), "Metro A Disruption",
        "1.2. 08:00 - 1.2. 08:30", Date(1549004400000), null)
    private val TRAM_1 inline get() = Disruption(null, "guid-1", mutableSetOf("1"), "Tram 1 Disruption",
        "1.2. 12:30 - 1.2. 13:00", Date(1549020600000), null)
    private val MULTIPLE_LINES inline get() = Disruption(null, "12345-2", mutableSetOf("A", "1", "100"),
        "Mimořádnost", "1.2. 18:00 - do odvolání", Date(1549044000000), null)

    private var helper = LocalServiceTestHelper(LocalDatastoreServiceTestConfig())
    private lateinit var objectify: Objectify
    private lateinit var objectifySession: Closeable

    @Before
    fun setUp() {
        helper.setUp()
        objectifySession = ObjectifyService.begin()
        objectify = ObjectifyService.ofy()
    }

    @After
    fun tearDown() {
        objectifySession.close()
        helper.tearDown()
    }

    @Test
    fun save_simple() {
        DisruptionDao().save(METRO_A)

        val list = objectify.load().type<Disruption>(Disruption::class.java).list()
        assertEquals(1, list.size)
        assertEqualsIgnoreId(METRO_A, list[0])
        assertEquals(METRO_A.guid, list[0].guid)
        assertEquals(METRO_A.lines.size, list[0].lines.size)
        assertEquals(METRO_A.lines, list[0].lines)
        assertEquals(METRO_A.created, list[0].created)
    }

    @Test
    fun save_multiple() {
        val id1: Long; val id2: Long; val id3: Long
        DisruptionDao().apply {
            id1 = save(METRO_A)
            id2 = save(TRAM_1)
            id3 = save(MULTIPLE_LINES)
        }

        val list = objectify.load().type<Disruption>(Disruption::class.java).list()
        assertEquals(3, list.size)
        assertTrue(list.contains(METRO_A.copy(id = id1)))
        assertTrue(list.contains(TRAM_1.copy(id = id2)))
        assertTrue(list.contains(MULTIPLE_LINES.copy(id = id3)))
    }

    @Test
    fun load_empty() {
        val disruption = DisruptionDao().load("guid")
        assertNull(disruption)
    }

    @Test
    fun load_simple() {
        val disruptionDao = DisruptionDao()

        objectify.save().entities<Disruption>(METRO_A, TRAM_1, MULTIPLE_LINES).now()

        val disruption = disruptionDao.load(TRAM_1.guid)

        assertNotNull(disruption)
        assertEqualsIgnoreId(TRAM_1, disruption!!)
        assertEquals(TRAM_1.guid, disruption.guid)
        assertEquals(TRAM_1.lines.size, disruption.lines.size)
        assertEquals(TRAM_1.lines, disruption.lines)
        assertEquals(TRAM_1.created, disruption.created)
    }

    @Test
    fun load_notExisting() {
        val disruptionDao = DisruptionDao()

        objectify.save().entities<Disruption>(METRO_A, TRAM_1).now()

        val disruption = disruptionDao.load(MULTIPLE_LINES.guid)

        assertNull(disruption)
    }

    @Test
    fun save_load() {
        val disruptionDao = DisruptionDao()

        disruptionDao.apply {
            save(METRO_A)
            save(TRAM_1)
        }


        var disruption = disruptionDao.load(METRO_A.guid)
        assertNotNull(disruption)
        assertEqualsIgnoreId(METRO_A, disruption!!)

        disruption = disruptionDao.load(TRAM_1.guid)
        assertNotNull(disruption)
        assertEqualsIgnoreId(TRAM_1, disruption!!)

        assertNull(disruptionDao.load(MULTIPLE_LINES.guid))

        val id = disruptionDao.save(MULTIPLE_LINES)

        assertEquals(MULTIPLE_LINES.copy(id = id), disruptionDao.load(MULTIPLE_LINES.guid))
    }

    @Test
    fun update() {
        val disruptionDao = DisruptionDao()

        disruptionDao.apply {
            save(METRO_A)
            save(TRAM_1)
            save(MULTIPLE_LINES)
        }

        assertEquals(3, objectify.load().type<Disruption>(Disruption::class.java).list().size)

        val disruption = DisruptionDao().load(MULTIPLE_LINES.guid)

        assertNotNull(disruption)
        if (disruption == null) return
        assertEqualsIgnoreId(MULTIPLE_LINES, disruption)

        val date = Date()
        val timeInfo = "1.2. 18:00 - 1.2. 18:01"
        disruption.timeInfo = timeInfo
        disruption.updated = date

        disruptionDao.save(disruption)

        assertEquals(3, objectify.load().type<Disruption>(Disruption::class.java).list().size)
        assertEqualsIgnoreId(METRO_A, disruptionDao.load(METRO_A.guid)!!)
        assertEqualsIgnoreId(TRAM_1, disruptionDao.load(TRAM_1.guid)!!)
        assertNotEquals(MULTIPLE_LINES, disruptionDao.load(MULTIPLE_LINES.guid))
        assertEquals(timeInfo, disruptionDao.load(MULTIPLE_LINES.guid)?.timeInfo)
        assertEquals(date, DisruptionDao().load(MULTIPLE_LINES.guid)?.updated)
    }


    // id is automatically generated by database
    private fun assertEqualsIgnoreId(expected: Disruption, actual: Disruption) {
        assertEquals(expected.copy(id = null), actual.copy(id = null))
    }

}
