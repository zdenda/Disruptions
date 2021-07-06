package eu.zkkn.android.disruptions.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test


class ExtensionsTest {

    @Test
    fun isValidLineName() {
        assertTrue("A".isValidLineName())
        assertTrue("b".isValidLineName())
        assertTrue("1".isValidLineName())
        assertTrue("10".isValidLineName())
        assertTrue("99".isValidLineName())
        assertTrue("100".isValidLineName())
        assertTrue("915".isValidLineName())
        assertTrue("Cyklobus".isValidLineName())
        assertTrue("U4".isValidLineName())
        assertTrue("r49".isValidLineName())
        assertTrue("s1".isValidLineName())
        assertTrue("S99".isValidLineName())
        assertTrue("X9".isValidLineName())
        assertTrue("x480".isValidLineName())
        assertTrue("LD".isValidLineName())
        assertTrue("p1".isValidLineName())
        assertTrue("P8".isValidLineName())
        assertTrue("H1".isValidLineName())
        assertTrue("AE".isValidLineName())
        assertTrue("zoo".isValidLineName())

        assertFalse("metro a".isValidLineName())
        assertFalse("linka a".isValidLineName())
        assertFalse("vše".isValidLineName())
        assertFalse("a,b".isValidLineName())
        assertFalse("a, b".isValidLineName())
        assertFalse("a b".isValidLineName())
        assertFalse("a b".isValidLineName())
        assertFalse("Anděl".isValidLineName())

        // TODO: improve validation of line names
        /*
        assertFalse("E".isValidLineName())
        assertFalse("40".isValidLineName())
        assertFalse("42".isValidLineName())
        assertFalse("metro".isValidLineName())
        assertFalse("luka".isValidLineName())
        assertTrue("Cyklo Brdy".isValidLineName())
        */
    }

    @Test
    fun capitalize() {
        assertEquals("Test", "test".capitalize())
        assertEquals("Test test", "test test".capitalize())
        assertEquals("Test", "Test".capitalize())
        assertEquals("TEST", "TEST".capitalize())
        assertEquals("", "".capitalize())

        assertEquals("Áčko", "áčko".capitalize())
        assertEquals("Ďas", "ďas".capitalize())
        assertEquals("Únor", "únor".capitalize())
        assertEquals("Ůůů", "ůůů".capitalize())
        assertEquals("Ěščřžýáíé", "ěščřžýáíé".capitalize())

        assertEquals("+", "+".capitalize())
        assertEquals("---", "---".capitalize())
    }

}
