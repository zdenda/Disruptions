package eu.zkkn.android.disruptions.ui.map

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MarkerDataTest {

    @Test
    fun testEquals() {
        val markerData0 = MarkerData("A", "A1", 0)
        val markerData1 = MarkerData("A", "A1", 0)
        val markerData2 = MarkerData("B", "B1", 1)

        assertFalse(markerData0 === markerData1)
        assertFalse(markerData1 === markerData2)
        assertTrue(markerData1 !== markerData2)

        assertFalse(markerData1 == markerData2)
        assertTrue(markerData0 == markerData1)
        assertTrue(markerData2 != markerData1)

        assertFalse(markerData1.equals(markerData2))
        assertTrue(markerData0.equals(markerData1))
    }
}
