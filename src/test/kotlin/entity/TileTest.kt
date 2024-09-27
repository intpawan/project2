package entity

import kotlin.test.*

/**
 * Test class for [Tile]
 */
class TileTest {
    private val task1 = Task(false, mutableListOf(Type.RED, Type.BLUE))
    private val task2 = Task(true, mutableListOf(Type.CYAN, Type.BLUE))
    private val task3 = Task(false, mutableListOf(Type.RED, Type.YELLOW))
    private val task4 = Task(false, mutableListOf(Type.CYAN, Type.YELLOW))

    private val tile1 = Tile(Type.YELLOW, Cost.FIVE, mutableListOf(task1, task2),10)
    private val tile2 = Tile(Type.CYAN, Cost.ONE, mutableListOf(task3,task4))

    /**
     * Test if getting the value of "type" works
     */
    @Test
    fun testGetType(){
        assertEquals(tile1.type, Type.YELLOW)
        assertEquals(tile2.type, Type.CYAN)
    }

    /**
     * Test if getting the value of "cost" works
     */
    @Test
    fun testGetCost(){
        assertEquals(tile1.cost, Cost.FIVE)
        assertEquals(tile2.cost, Cost.ONE)
    }

    /**
     * Test if getting the values of "tasks" works
     */
    @Test
    fun testGetTasks(){
        assertEquals(tile1.tasks, mutableListOf(task1, task2))
        assertEquals(tile2.tasks, mutableListOf(task3,task4))
    }

    /**
     * Test if getting the values of "id" works
     */
    @Test
    fun testGetId(){
        assertEquals(tile1.id,10)
        assertEquals(tile2.id,-1)
    }

    /**
     * Test if cloning works
     */
    @Test
    fun testTileClone(){
        //cloned tile to test
        val clonedTile1 = tile1.clone()

        assertEquals(tile1,clonedTile1)
        assertNotSame(tile1,clonedTile1)

        assertEquals(tile1.type,clonedTile1.type)
        assertEquals(tile1.cost,clonedTile1.cost)
        assertEquals(tile1.tasks,clonedTile1.tasks)
        assertNotSame(tile1.tasks,clonedTile1.tasks)
        assertEquals(tile1.id,clonedTile1.id)

    }
}