package entity

import kotlin.test.*

/**
 * Test class for [Moonwheel].
 */
class MoonwheelTest {

    private val p1 = Player("Tom", PlayerColour.BLACK,PlayerType.HUMAN,2)
    private val p2 = Player("Tim", PlayerColour.BLUE,PlayerType.HUMAN,1)
    private val p3 = Player("John", PlayerColour.ORANGE,PlayerType.HUMAN,3)

    // tasks for tile 1
    private val task1 = Task(false, mutableListOf(Type.RED, Type.BLUE))
    private val task2 = Task(false, mutableListOf(Type.CYAN, Type.BLUE))
    // tasks for tile 2
    private val task3 = Task(false, mutableListOf(Type.RED, Type.YELLOW))
    private val task4 = Task(false, mutableListOf(Type.CYAN, Type.YELLOW))
    // tiles
    private val t1 = Tile(Type.RED, Cost.FIVE, mutableListOf(task1,task2))
    private val t2 = Tile(Type.YELLOW, Cost.SEVEN, mutableListOf(task3, task4))

    /**
     * Test for initialisation of property playerPositions of class [Moonwheel]
     */
    @Test
    fun testGetPlayerPositions(){
        val moonWheel = Moonwheel()

        moonWheel.playerPositions[2] = mutableListOf(p1)
        moonWheel.playerPositions[5] = mutableListOf(p2)

        val expectedPlayerAt2: Player = p1
        val expectedPlayerAt5: Player = p2

        assertEquals(expectedPlayerAt2, moonWheel.playerPositions.get(2).get(0))
        assertEquals(expectedPlayerAt5, moonWheel.playerPositions[5].get(0))
    }
    /**
     * Test for initialisation of property tiles of class [Moonwheel]
     */
    @Test
    fun testGetTiles(){
        val moonWheel = Moonwheel()

        moonWheel.tiles[1] = t1
        moonWheel.tiles[2] = t2

        assertEquals(t1, moonWheel.tiles[1])
        assertEquals(t2, moonWheel.tiles[2])
    }

    /**
     *  Tests if clone method in [Moonwheel] works correctly
     */

    @Test
    fun testMoonWheelClone(){

        val moonWheel = Moonwheel()

        moonWheel.playerPositions[2] = mutableListOf(p1)
        moonWheel.playerPositions[5] = mutableListOf(p2)

        moonWheel.tiles[1] = t1
        moonWheel.tiles[2] = t2
        // moon wheel cloned
        val clonedMoonWheel = moonWheel.clone()

        assertEquals(mutableListOf(p1), clonedMoonWheel.playerPositions[2])
        assertEquals(mutableListOf(p2), clonedMoonWheel.playerPositions[5])
        assertTrue(moonWheel.equals(clonedMoonWheel))

        assertEquals(t1, clonedMoonWheel.tiles[1])
        assertEquals(t2, clonedMoonWheel.tiles[2])

        assertEquals(moonWheel.tiles[1], clonedMoonWheel.tiles[1])
        assertEquals(moonWheel.playerPositions[2], clonedMoonWheel.playerPositions[2])
    }


    /**
     * test if comparing moonwheels fails in different cases
     */
    @Test
    fun testEquals(){
        val moonWheel1 = Moonwheel()
        val moonWheel2 = Moonwheel()
        val moonWheel3 = Moonwheel()

        moonWheel1.playerPositions[2] = mutableListOf(p1)
        moonWheel1.playerPositions[5] = mutableListOf(p2)
        moonWheel1.playerPositions[6] = mutableListOf(p3)

        moonWheel2.playerPositions[2] = mutableListOf(p2)
        moonWheel2.playerPositions[5] = mutableListOf(p3,p1)

        moonWheel3.playerPositions[2] = mutableListOf(p1,p2,p3)

        assertFalse(moonWheel1.equals(moonWheel2))
        assertFalse(moonWheel3.equals(moonWheel1))
    }

    /**
     * Test if hashing works
     */
    @Test
    fun testHashCode() {
        val moonWheel1 = Moonwheel()
        val clonedMoonwheel = moonWheel1.clone()
        assertEquals(moonWheel1.hashCode(), clonedMoonwheel.hashCode())
        assertEquals(moonWheel1.hashCode(), Moonwheel().hashCode())

        val moonWheel2 = Moonwheel()
        moonWheel2.tiles[0] = t1
        assertNotEquals(moonWheel1.hashCode(), moonWheel2.hashCode())

    }

}