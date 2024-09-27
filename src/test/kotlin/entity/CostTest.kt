package entity

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Test class for [Cost].
 */
class CostTest {

    /**
     * test converting [Cost] to String works
     */
    @Test
    fun testToString(){
        assertEquals("0", Cost.ZERO.toString())
        assertEquals("1", Cost.ONE.toString())
        assertEquals("2", Cost.TWO.toString())
        assertEquals("3", Cost.THREE.toString())
        assertEquals("4", Cost.FOUR.toString())
        assertEquals("5", Cost.FIVE.toString())
        assertEquals("6", Cost.SIX.toString())
        assertEquals("7", Cost.SEVEN.toString())
    }


}