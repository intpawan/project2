package service

import entity.*
import org.junit.jupiter.api.Test
import kotlin.test.*

/**
 * Test class for [PlayerActionService]
 */
class PlayerActionServiceTest {
    val highScore1 : Score = Score(20.3,"A")
    val highScore2 : Score = Score(11.1,"B")
    val highScoreList = mutableListOf<Score>(highScore1,highScore2)

    val p1 = Player("Tom", PlayerColour.BLACK, PlayerType.HUMAN,2)
    val p2 = Player("Tim", PlayerColour.BLUE, PlayerType.HUMAN,1)
    val p3 = Player("Hans", PlayerColour.ORANGE, PlayerType.HUMAN,1)
    val p4 = Player("Max", PlayerColour.WHITE, PlayerType.HUMAN,1)
    val playersList = mutableListOf<Player>(p1,p2,p3,p4)

    val task1 = Task(false, mutableListOf(Type.RED, Type.YELLOW))
    val task2 = Task(false, mutableListOf(Type.CYAN, Type.BLUE))
    val task3 = Task(false, mutableListOf(Type.RED, Type.YELLOW))
    val task4 = Task(false, mutableListOf(Type.CYAN, Type.YELLOW))
    val task5 = Task(false, mutableListOf(Type.RED, Type.YELLOW))
    val task6 = Task(false, mutableListOf(Type.CYAN, Type.BLUE))
    val task7 = Task(false, mutableListOf(Type.YELLOW, Type.YELLOW))
    val task8 = Task(false, mutableListOf(Type.CYAN, Type.BLUE))
    val task9 = Task(false, mutableListOf(Type.YELLOW, Type.RED))
    val task10 = Task(false, mutableListOf(Type.YELLOW, Type.BLUE))


    // tiles for moonwheel
    val meeple = Tile(Type.MEEPLE, Cost.ZERO, mutableListOf<Task>(), 69)

    // tiles for stack
    val tile3 = Tile(Type.RED, Cost.FIVE, mutableListOf(task5,task6))
    val tile4 = Tile(Type.YELLOW, Cost.SEVEN, mutableListOf(task7, task8))
    val tile5 = Tile(Type.CYAN, Cost.TWO, mutableListOf(task1, task8))
    val tile6 = Tile(Type.BLUE, Cost.FOUR, mutableListOf(task6, task7))
    val tile7 = Tile(Type.CYAN, Cost.ONE, mutableListOf(task6, task7))
    val tile8 = Tile(Type.CYAN, Cost.TWO, mutableListOf(task1, task5))
    val tile9 = Tile(Type.BLUE, Cost.THREE, mutableListOf(task3, task7))
    val tile10 = Tile(Type.YELLOW, Cost.SIX, mutableListOf(task6, task7))
    val tile11 = Tile(Type.RED, Cost.FIVE, mutableListOf(task6, task7))
    val tile12 = Tile(Type.BLUE, Cost.FOUR, mutableListOf(task9, task5))
    val tile13 = Tile(Type.RED, Cost.THREE, mutableListOf(task1, task2))
    val tile14 = Tile(Type.RED, Cost.FOUR, mutableListOf(task10, task5))
    val tile15 = Tile(Type.RED, Cost.FIVE, mutableListOf(task1,task2))
    val tile16 = Tile(Type.YELLOW, Cost.SEVEN, mutableListOf(task3, task4))


    val tilesList1: MutableList<Tile> = mutableListOf(
        tile3,tile4,tile5,tile6,tile7,tile8,tile9,tile10,tile11,tile12,tile13,tile14,tile15,tile16
    )


    val tilesList2 : MutableList<Tile> = mutableListOf(
        tile3,tile4,tile5,tile6,tile7,tile8,tile9,tile10,tile11,tile12
    )

    val tilesList3 : MutableList<Tile> = mutableListOf(
        tile3,tile4,tile5,tile6,tile7,tile8,tile9,tile10
    )


    val state1 = State(tilesList1, Moonwheel(), p1, playersList,"")
    val state2 = State(tilesList2, Moonwheel(), p1, playersList,"")
    val state3 = State(tilesList3, Moonwheel(), p1, playersList,"")
    val state4 = State(tilesList1, Moonwheel(), p2, playersList,"")
    val state5 = State(tilesList1, Moonwheel(), p3, playersList,"")
    val state6 = State(tilesList2, Moonwheel(), p3, playersList,"")
    val state7 = State(tilesList1, Moonwheel(), p4, playersList,"")
    val state8 = State(tilesList2, Moonwheel(), p4, playersList,"")

    val stateList : MutableList<State> = mutableListOf(
        state1,state2,state3,state4,state5,state6,state7,state8
    )

    val testGame1 = Game(state1, false, "", false, 0,stateList)

    val rootService = RootService(novaluna = Novaluna(highScoreList,testGame1))

    /**
     * Test if refilling the tiles work:
     * The moonwheel is refilled with 11 tiles from the stack.
     * So the stack has only 3 tiles left.
     */
    @Test
    fun testRefillTiles(){
        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)

        val playerActionService = PlayerActionService(rootService)

        // Case 1: No meeple on the moonwheel
        assertFails { playerActionService.refillTiles() }
        assertFalse(testRefreshable.refreshAfterRefillTilesCalled)



        // Case 2: Set the meeple on the moonwheel, so refillTiles() can be executed
        val currentGame : Game? = playerActionService.rootService.novaluna.game
        assertNotNull(currentGame)
        currentGame.currentState.moonwheel.tiles[2] = meeple
        val currentStack = currentGame.currentState.stack

        assertEquals(currentStack.size, 14)

        rootService.playerActionService.refillTiles()
        assertTrue(testRefreshable.refreshAfterRefillTilesCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterRefillTilesCalled)


        val moonwheelCase2 = currentGame.currentState.moonwheel

        assertEquals(tile12, moonwheelCase2.tiles[0])
        assertEquals(tile13, moonwheelCase2.tiles[1])

        assertEquals(tile3, moonwheelCase2.tiles[3])
        assertEquals(tile4, moonwheelCase2.tiles[4])
        assertEquals(tile5, moonwheelCase2.tiles[5])
        assertEquals(tile6, moonwheelCase2.tiles[6])
        assertEquals(tile7, moonwheelCase2.tiles[7])
        assertEquals(tile8, moonwheelCase2.tiles[8])
        assertEquals(tile9, moonwheelCase2.tiles[9])
        assertEquals(tile10, moonwheelCase2.tiles[10])
        assertEquals(tile11, moonwheelCase2.tiles[11])

        assertEquals(currentStack.size, 3)

        assertEquals(currentStack[0], tile14)
        assertEquals(currentStack[1], tile15)
        assertEquals(currentStack[2], tile16)


        // Case 3: refill the moonwheel but the stack has not enough tiles
        currentGame.currentState.moonwheel = Moonwheel()
        currentGame.currentState.moonwheel.tiles[4] = meeple
        val moonwheelCase3 = currentGame.currentState.moonwheel

        val emptyTile = Tile()
        assertEquals(emptyTile, moonwheelCase3.tiles[5])
        assertEquals(emptyTile, moonwheelCase3.tiles[0])

        rootService.playerActionService.refillTiles()
        assertTrue(testRefreshable.refreshAfterRefillTilesCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterRefillTilesCalled)


        assertEquals(currentStack.size, 0)

        assertEquals(tile14, moonwheelCase3.tiles[5])
        assertEquals(tile15, moonwheelCase3.tiles[6])
        assertEquals(tile16, moonwheelCase3.tiles[7])
        assertEquals(emptyTile,moonwheelCase3.tiles[8])
        assertEquals(emptyTile,moonwheelCase3.tiles[0])
    }

    /**
     * Test if undo and redo works
     */
    @Test
    fun testUndoRedo(){
        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)

        val playerActionService = PlayerActionService(rootService)
        val currentGame = playerActionService.rootService.novaluna.game
        assertNotNull(currentGame)

        // Case 1: Undo the first state
        assertEquals(state1,currentGame.currentState)
        assertEquals(p1,currentGame.currentState.currentPlayer)
        assertFails { rootService.playerActionService.undo() }
       assertFalse(testRefreshable.refreshAfterUndoCalled)

        assertFalse(currentGame.choseUndo)
        assertEquals(state1,currentGame.currentState)
        assertEquals(p1,currentGame.currentState.currentPlayer)


        // Case 2: Redo the last state
        currentGame.currentState = currentGame.states.last()
        assertEquals(p4,currentGame.currentState.currentPlayer)
        assertFails { rootService.playerActionService.redo() }
        assertFalse(testRefreshable.refreshAfterUndoCalled)

        assertEquals(state8,currentGame.currentState)
        assertEquals(p4,currentGame.currentState.currentPlayer)

        // Case 3: Doing redo three times starting with the first state
        // -> player one is only current player in first two states
        currentGame.currentState = currentGame.states[0]
        rootService.playerActionService.redo()
        assertTrue(testRefreshable.refreshAfterRedoCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterRedoCalled)

        assertEquals(state2,currentGame.currentState)
        val indexCurrentGame = currentGame.states.indexOf(currentGame.currentState)
        assertEquals(1,indexCurrentGame)
        rootService.playerActionService.redo()
        assertTrue(testRefreshable.refreshAfterRedoCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterRedoCalled)

        assertEquals(state3,currentGame.currentState)
        assertEquals(p1,currentGame.currentState.currentPlayer)
        assertFails { playerActionService.redo() }

        // Case 4: Undo the third state
        rootService.playerActionService.undo()
        assertTrue(testRefreshable.refreshAfterUndoCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterUndoCalled)

        assertEquals(state2,currentGame.currentState)
        assertEquals(p1,currentGame.currentState.currentPlayer)
        assertTrue(currentGame.choseUndo)
    }
}