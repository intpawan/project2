package entity

import tools.aqua.bgw.util.Stack
import kotlin.test.*

/**
 * Test class for [State]
 */
class StateTest {
    val p1 = Player("Tom", PlayerColour.BLACK,PlayerType.HUMAN,2)
    val p2 = Player("Tim", PlayerColour.BLUE,PlayerType.HUMAN,1)
    val playersList : MutableList<Player> = mutableListOf(p1,p2)

    val task1 = Task(false, mutableListOf(Type.RED, Type.BLUE))
    val task2 = Task(false, mutableListOf(Type.CYAN, Type.BLUE))
    val task3 = Task(false, mutableListOf(Type.RED, Type.YELLOW))
    val task4 = Task(false, mutableListOf(Type.CYAN, Type.YELLOW))
    val task5 = Task(true, mutableListOf(Type.RED, Type.YELLOW))
    val task6 = Task(false, mutableListOf(Type.CYAN, Type.BLUE))
    val task7 = Task(false, mutableListOf(Type.YELLOW, Type.YELLOW))
    val task8 = Task(false, mutableListOf(Type.CYAN, Type.BLUE))

    // tiles
    val tile1 = Tile(Type.RED, Cost.FIVE, mutableListOf(task1,task2))
    val tile2 = Tile(Type.YELLOW, Cost.SEVEN, mutableListOf(task3, task4))
    val tile3 = Tile(Type.RED, Cost.FIVE, mutableListOf(task5,task6))
    val tile4 = Tile(Type.YELLOW, Cost.SEVEN, mutableListOf(task7, task8))
    val tile5 = Tile(Type.YELLOW, Cost.SEVEN, mutableListOf(task1, task8))


    val tilesList1: MutableList<Tile> = mutableListOf(tile1, tile2, tile3, tile4)
    val tilesList2: MutableList<Tile> = mutableListOf(tile1, tile2)

    // Moonwheel with empty tiles
    val moonwheel = Moonwheel()

    // moon wheel
    val state1 = State(tilesList1, moonwheel, p1, playersList, "")
    val state2 = State(tilesList2, moonwheel, p1, playersList, "")


    /**
     * Test if getting and setting values of "stack" works
     */
    @Test
    fun testGetAndSetStack(){
        assertEquals(tilesList1, state1.stack)
        assertEquals(tilesList2, state2.stack)
        val testStack  : MutableList<Tile> = tilesList1
        testStack.add(tile5)
        state1.stack.add(tile5)
        assertEquals(testStack, state1.stack)
    }

    /**
     * Test if getting and setting value of "moonwheel" works
     */
    @Test
    fun testGetAndSetMoonwheel(){
        assertEquals(moonwheel, state1.moonwheel)
        assertEquals(moonwheel, state1.moonwheel)
        val testTileList : Array<Tile> = arrayOf(tile1,tile2, tile3, tile4, tile5)
        val testPlayerPositions : Array<MutableList<Player>> = Array<MutableList<Player>>(24){ mutableListOf<Player>()}
        val testMoonwheel  = Moonwheel(testTileList, testPlayerPositions )
        state1.moonwheel = testMoonwheel
        assertEquals(testMoonwheel, state1.moonwheel)
    }

    /**
     * Test if getting and setting value of "currentPlayer" works
     */
    @Test
    fun testGetAndSetCurrentPlayer(){
        assertEquals(p1, state1.currentPlayer)
        assertEquals(p1, state1.currentPlayer)
        state1.currentPlayer = p2
        assertEquals(p2, state1.currentPlayer)
    }


    /**
     * Test if getting and setting values of "players" works
     */
    @Test
    fun testGetAndSetPlayers(){
        assertEquals(playersList, state1.players)
        assertEquals(playersList, state2.players)
        val testPlayersList : MutableList<Player> = state1.players.map{ it -> it.clone() }.toMutableList()
        val testPlayer = Player("Max", PlayerColour.ORANGE,PlayerType.HUMAN,2)
        testPlayersList.add(testPlayer)
        state1.players = testPlayersList
    }

    /**
     * Test if cloning works
     */
    @Test
    fun testClone(){
        val clonedState1 = state1.clone()

        assertNotSame(clonedState1, state1)

        assertEquals(clonedState1.protocol, state1.protocol)
        assertEquals(clonedState1.stack, state1.stack)
        assertNotSame(clonedState1.stack, state1.stack)


        for (i  in state1.moonwheel.tiles.indices){
            assertEquals(clonedState1.moonwheel.tiles[i], state1.moonwheel.tiles[i])
        }
        assertNotSame(clonedState1.moonwheel, state1.moonwheel)

        assertEquals(clonedState1.currentPlayer, state1.currentPlayer)
        assertNotSame(clonedState1.currentPlayer, state1.currentPlayer)

        assertEquals(clonedState1.players, state1.players)
        assertNotSame(clonedState1.players, state1.players)
    }

}