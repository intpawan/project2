package entity

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [Game]
 */
class GameTest {

    val p1 = Player("Tom", PlayerColour.BLACK,PlayerType.HUMAN,2)
    val p2 = Player("Tim", PlayerColour.BLUE,PlayerType.HUMAN,1)
    val playersList = mutableListOf<Player>(p1,p2)

    // tasks for tile 1 (stack)
    val task1 = Task(false, mutableListOf(Type.RED, Type.YELLOW))
    val task2 = Task(false, mutableListOf(Type.CYAN, Type.BLUE))

    // tasks for tile 2(stack)
    val task3 = Task(false, mutableListOf(Type.RED, Type.YELLOW))
    val task4 = Task(false, mutableListOf(Type.CYAN, Type.YELLOW))

    // tasks for tile 3 (moon wheel)
    val task5 = Task(true, mutableListOf(Type.RED, Type.YELLOW))
    val task6 = Task(false, mutableListOf(Type.CYAN, Type.BLUE))

    // tasks for tile 4 (moon wheel)
    val task7 = Task(false, mutableListOf(Type.YELLOW, Type.YELLOW))
    val task8 = Task(false, mutableListOf(Type.CYAN, Type.BLUE))

    // tiles for stack
    val tile1 = Tile(Type.RED, Cost.FIVE, mutableListOf(task1,task2))
    val tile2 = Tile(Type.YELLOW, Cost.SEVEN, mutableListOf(task3, task4))
    val tile3 = Tile(Type.RED, Cost.FIVE, mutableListOf(task5,task6))
    val tile4 = Tile(Type.YELLOW, Cost.SEVEN, mutableListOf(task7, task8))

    // Moonwheel with empty tiles
    val moonwheel = Moonwheel()

    val tilesList1: MutableList<Tile> = mutableListOf(tile1, tile2, tile3, tile4)
    val tilesList2: MutableList<Tile> = mutableListOf(tile1, tile2)

    val state1 = State(tilesList1, moonwheel, p1, playersList, "")
    val state2 = State(tilesList2, moonwheel, p1, playersList, "")

    val testGame1 = Game(state1, false, "", false, 0, mutableListOf(state1))
    val testGame2 = Game(state2, true, "", true, 5, mutableListOf(state1,state2))

   /**
     * Test if getting and setting value of "currentState" works
     */
   @Test
   fun testGetAndSetCurrentState(){
       assertEquals(state1, testGame1.currentState)
       assertEquals(state2, testGame2.currentState)
       testGame2.currentState  = state1
       assertEquals(state1, testGame2.currentState)
   }

    /**
     * Test if getting and setting value of "aiTournament" works
     */
    @Test
    fun testGetAndSetAiTournament(){
        assertFalse(testGame1.aiTournament)
        assertTrue(testGame2.aiTournament)
        testGame1.aiTournament = true
        assertTrue(testGame1.aiTournament)
    }

    /**
     * Test if getting and setting value of "tournamentProtocol" works
     */
    @Test
    fun testGetAndSetTournamentProtocol(){
        assertEquals("", testGame1.tournamentProtocol)
        assertEquals("", testGame2.tournamentProtocol)
        val testProtocol =  "KI hat einen Zug gemacht"
        testGame1.tournamentProtocol = testProtocol
        assertEquals(testProtocol, testGame1.tournamentProtocol)
    }


    /**
     * Test if getting and setting value of "choseUndo" works
     */
    @Test
    fun testGetAndSetChoseUndo(){
        assertFalse(testGame1.choseUndo)
        assertTrue(testGame2.choseUndo)
        testGame1.choseUndo = true
        assertTrue(testGame1.choseUndo)
   }

    /**
     * Test if getting and setting value of "aiSpeed" works
     */
    @Test
    fun testGetAndSetAiSpeed(){
        assertEquals(0, testGame1.aiSpeed)
        assertEquals(5, testGame2.aiSpeed)
        val testAISpeed = 10
        testGame2.aiSpeed = testAISpeed
        assertEquals(testAISpeed, testGame2.aiSpeed)
    }

    /**
     * Test if getting and modifying values of "states" works
     */
    @Test
    fun testGetAndModifyStates(){
        assertEquals(mutableListOf(state1), testGame1.states)
        assertEquals(mutableListOf(state1,state2), testGame2.states)
        val tilesList3: MutableList<Tile> = mutableListOf(tile1, tile2, tile3)
        val testState  = State(tilesList3, moonwheel, p1, playersList, "")
        val testStateList : List<State> = mutableListOf(state1,state2,testState)
        testGame2.states.add(testState)
        assertEquals(testStateList, testGame2.states)
    }
}