package eu.zkkn.android.disruptions.data

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import eu.zkkn.android.disruptions.utils.getValue
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Date


@RunWith(AndroidJUnit4::class)
class DisruptionDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var disruptionDao: DisruptionDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        disruptionDao = db.disruptionDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }


    @Test
    @Throws(Exception::class)
    fun insertSameGuid() {
        val guid = "guid"

        val disruptionFirst = Disruption(0, guid, Date(), setOf("A", "B", "C"), "Mimořádnost v metru",
            "1.1. 10:00 - do odvolání")

        val disruptionSecond = Disruption(0, guid, Date(), setOf("A", "B", "C", "D"),
            "Mimořádnost v metru na linkách A, B, C a D", "1.1. 10:00 - 1.1. 11:00")

        var id = disruptionDao.insert(disruptionFirst)

        var allDisruptions = getValue(disruptionDao.getAll())
        assertNotNull(allDisruptions)
        assertTrue(allDisruptions.isNotEmpty())

        var disruption = allDisruptions.filter { it.guid == guid }[0]
        assertEquals(guid, disruption.guid)
        assertEquals(disruptionFirst.received, disruption.received)
        assertEquals(disruptionFirst.lineNames, disruption.lineNames)
        assertEquals(disruptionFirst.title, disruption.title)
        assertEquals(disruptionFirst.timeInfo, disruption.timeInfo)


        id = disruptionDao.insert(disruptionSecond)

        allDisruptions = getValue(disruptionDao.getAll())
        assertNotNull(allDisruptions)
        assertTrue(allDisruptions.isNotEmpty())

        disruption = allDisruptions.filter { it.guid == guid }[0]
        assertEquals(guid, disruption.guid)
        assertEquals(disruptionSecond.received, disruption.received)
        assertEquals(disruptionSecond.lineNames, disruption.lineNames)
        assertEquals(disruptionSecond.title, disruption.title)
        assertEquals(disruptionSecond.timeInfo, disruption.timeInfo)
    }

}
