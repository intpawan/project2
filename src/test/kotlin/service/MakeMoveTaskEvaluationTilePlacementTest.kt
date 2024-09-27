package service

import entity.*
import java.io.File
import kotlin.test.*

/**
 * Test class for task evaluation and tile placement in method makeMove from [PlayerActionService].
 */
class MakeMoveTaskEvaluationTilePlacementTest {

    private val highscores = mutableListOf<Score>()
    private val novaluna = Novaluna(highscores,null)
    private val rootService = RootService(novaluna)
    private val gameService = GameService(rootService)
    private val playerActionService = PlayerActionService(rootService)

    private val humanPlayer1Triple = Triple("Player1", PlayerColour.BLACK, PlayerType.HUMAN)
    private val humanPlayer2Triple = Triple("Player2", PlayerColour.ORANGE, PlayerType.HUMAN)
    private val humanPlayer3Triple = Triple("Player3", PlayerColour.BLUE, PlayerType.HUMAN)

    /**
     * Method to test the placement of tiles on the grid and the evaluation of the tasks. Some tiles are taken from
     * the moonwheel and placed on the grid.
     */
    @Test
    fun makeMoveTaskEvaluation1(){
        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)

        // Create a game and get the players. Note that the game is a aiTournament so that the cardorder is controlled.
        val playerTriples = listOf(humanPlayer1Triple,humanPlayer2Triple,humanPlayer3Triple)
        assertNull(rootService.novaluna.game)
        rootService.gameService.startNewAITournamentGame(playerTriples, File("src/main/resources/default_CSV_SaveFile/nl_tiles.csv"))
        assertTrue(testRefreshable.refreshAfterStartNewGameCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterStartNewGameCalled)


        val currentGame = rootService.novaluna.game
        assertNotNull(currentGame)

        // Make a first move and check
        rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[1],Pair(0,0))
        assertTrue(testRefreshable.refreshAfterMakeMoveCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterMakeMoveCalled)

        assertEquals(currentGame.currentState.currentPlayer.clusters, setOf(Pair(Type.CYAN,setOf(Pair(0,0)))))
        assertEquals(currentGame.currentState.currentPlayer.tokens ,20)

        // Make a few more moves and check. Four cyan tokens were placed.
        // Note that still no tasks should be completed because tiles don't count for themselves.
        rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[2],Pair(0,1))
        assertTrue(testRefreshable.refreshAfterMakeMoveCalled)
        rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[3],Pair(1,0))
        assertTrue(testRefreshable.refreshAfterMakeMoveCalled)
        rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[4],Pair(1,1))
        assertEquals(currentGame.currentState.currentPlayer.tokens ,20)

        assertTrue(testRefreshable.refreshAfterMakeMoveCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterMakeMoveCalled)


        // Make a fifth move placing a cyan. Now two tasks are completed.
        rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[5],Pair(1,2))
        assertEquals(
            currentGame.currentState.currentPlayer.clusters,
            setOf(Pair(Type.CYAN,setOf(Pair(0,0),Pair(0,1),Pair(1,0),Pair(1,1),Pair(1,2))))
        )
        assertTrue(testRefreshable.refreshAfterMakeMoveCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterMakeMoveCalled)

        assertTrue(currentGame.currentState.currentPlayer.grid[0][1].tasks[0].completed)
        assertTrue(currentGame.currentState.currentPlayer.grid[1][0].tasks[0].completed)
        assertFalse(currentGame.currentState.currentPlayer.grid[1][1].tasks[0].completed)
        assertEquals(currentGame.currentState.currentPlayer.tokens ,18)

        // Make a few more moves. Now three tasks should be done
        rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[6],Pair(2,2))
        rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[7],Pair(2,3))
        rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[8],Pair(2,4))
        rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[9],Pair(2,5))
        assertTrue(testRefreshable.refreshAfterMakeMoveCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterMakeMoveCalled)

        assertEquals(
            currentGame.currentState.currentPlayer.clusters,
            setOf(Pair(Type.CYAN,setOf(Pair(0,0),Pair(0,1),Pair(1,0),Pair(1,1),Pair(1,2),
                Pair(2,2),Pair(2,3),Pair(2,4),Pair(2,5))))
        )
        assertTrue(currentGame.currentState.currentPlayer.grid[0][1].tasks[0].completed)
        assertTrue(currentGame.currentState.currentPlayer.grid[1][0].tasks[0].completed)
        assertTrue(currentGame.currentState.currentPlayer.grid[2][5].tasks[0].completed)
        assertEquals(currentGame.currentState.currentPlayer.tokens ,17)
    }

    /**
     * Second method to test task evaluation. Now some custom tiles are placed on the grid to test the task
     * evaluation.
     */
    @Test
    fun makeMoveTaskEvaluation2(){
        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)

        // Create a game and get the players. Note that the game is a aiTournament so that the cardorder is controlled.
        val playerTriples = listOf(humanPlayer1Triple,humanPlayer2Triple,humanPlayer3Triple)
        assertNull(rootService.novaluna.game)
        rootService.gameService.startNewAITournamentGame(playerTriples, File("src/main/resources/default_CSV_SaveFile/nl_tiles.csv"))
        assertTrue(testRefreshable.refreshAfterStartNewGameCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterStartNewGameCalled)

        val currentGame = rootService.novaluna.game
        assertNotNull(currentGame)

        // Eight tiles, one two tasks completed, one uncompleted. All not yet marked as completed
        val tile1 = Tile(Type.CYAN,Cost.FIVE, mutableListOf())
        val pos1 = Pair(3,3)
        val tile2 = Tile(Type.CYAN,Cost.THREE, mutableListOf())
        val pos2 = Pair(4,3)
        val tile3 = Tile(Type.CYAN,Cost.FIVE, mutableListOf())
        val pos3 = Pair(4,2)
        val tile4 = Tile(Type.CYAN,Cost.ONE, mutableListOf())
        val pos4 = Pair(4,1)
        val tile5 = Tile(Type.RED,Cost.FOUR, mutableListOf())
        val pos5 = Pair(3,4)
        val tile6 = Tile(Type.YELLOW,Cost.ONE, mutableListOf(
            Task(false, mutableListOf(Type.CYAN,Type.CYAN,Type.CYAN,Type.CYAN)),
            Task(false, mutableListOf(Type.RED,Type.RED)),
            Task(false, mutableListOf(Type.RED,Type.BLUE,Type.YELLOW))
        ))
        val pos6 = Pair(4,4)
        val tile7 = Tile(Type.YELLOW,Cost.ONE, mutableListOf())
        val pos7 = Pair(5,4)
        val tile8 = Tile(Type.BLUE,Cost.TWO, mutableListOf())
        val pos8 = Pair(4,5)
        val tile9 = Tile(Type.YELLOW,Cost.TWO, mutableListOf())
        val pos9 = Pair(6,4)

        currentGame.currentState.currentPlayer.placeTileOnGrid(tile1,pos1)
        currentGame.currentState.currentPlayer.placeTileOnGrid(tile2,pos2)
        currentGame.currentState.currentPlayer.placeTileOnGrid(tile3,pos3)
        currentGame.currentState.currentPlayer.placeTileOnGrid(tile4,pos4)
        currentGame.currentState.currentPlayer.placeTileOnGrid(tile5,pos5)
        currentGame.currentState.currentPlayer.placeTileOnGrid(tile6,pos6)
        currentGame.currentState.currentPlayer.placeTileOnGrid(tile7,pos7)
        currentGame.currentState.currentPlayer.placeTileOnGrid(tile8,pos8)

        // Place tile. Now 3 completed and marked as such.
        rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[3],Pair(3,2))
        assertTrue(testRefreshable.refreshAfterMakeMoveCalled)
        testRefreshable.reset()


        assertFalse(testRefreshable.refreshAfterMakeMoveCalled)

        assertEquals(currentGame.currentState.currentPlayer.tokens,17)
        assertTrue { currentGame.currentState.currentPlayer.grid[pos6.first][pos6.second].tasks[0].completed }
        assertFalse { currentGame.currentState.currentPlayer.grid[pos6.first][pos6.second].tasks[1].completed }
        assertTrue { currentGame.currentState.currentPlayer.grid[pos6.first][pos6.second].tasks[2].completed }
        assertTrue { currentGame.currentState.currentPlayer.grid[3][2].tasks[0].completed }

        // Place tile. Now 4 completed and marked as such.
        rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[4],Pair(5,5))
        assertTrue(testRefreshable.refreshAfterMakeMoveCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterMakeMoveCalled)

        assertEquals(currentGame.currentState.currentPlayer.tokens,16)

        // Place a few more tiles and check.
        rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[7],Pair(6,5)) // No new task completed.
        assertTrue(testRefreshable.refreshAfterMakeMoveCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterMakeMoveCalled)

        assertEquals(currentGame.currentState.currentPlayer.tokens,16)
        currentGame.currentState.currentPlayer.placeTileOnGrid(tile9,pos9) // This completes the tile at (5,6)
        rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[8],Pair(2,4))  // This updates the tokens
        assertTrue(testRefreshable.refreshAfterMakeMoveCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterMakeMoveCalled)

        assertEquals(currentGame.currentState.currentPlayer.tokens,15)
    }

    /**
     * Third method to test task evaluation. Now it is checked that no new tiles are marked as completed once the
     * tokens are at 0.
     */
    @Test
    fun makeMoveTaskEvaluation3(){
        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)

        // Create a game and get the players. Note that the game is a aiTournament so that the cardorder is controlled.
        val playerTriples = listOf(humanPlayer1Triple,humanPlayer2Triple,humanPlayer3Triple)
        assertNull(rootService.novaluna.game)
        rootService.gameService.startNewAITournamentGame(playerTriples, File("src/main/resources/default_CSV_SaveFile/nl_tiles.csv"))
        assertTrue(testRefreshable.refreshAfterStartNewGameCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterStartNewGameCalled)


        val currentGame = rootService.novaluna.game
        assertNotNull(currentGame)

        // Eight tiles, one two tasks completed, one uncompleted. All not yet marked as completed
        val tile1 = Tile(Type.CYAN,Cost.FIVE, mutableListOf())
        val pos1 = Pair(3,3)
        val tile2 = Tile(Type.CYAN,Cost.THREE, mutableListOf())
        val pos2 = Pair(4,3)
        val tile3 = Tile(Type.CYAN,Cost.FIVE, mutableListOf())
        val pos3 = Pair(4,2)
        val tile4 = Tile(Type.CYAN,Cost.ONE, mutableListOf())
        val pos4 = Pair(4,1)
        val tile5 = Tile(Type.RED,Cost.FOUR, mutableListOf())
        val pos5 = Pair(3,4)
        val tile6 = Tile(Type.YELLOW,Cost.ONE, mutableListOf(
            Task(false, mutableListOf(Type.CYAN,Type.CYAN,Type.CYAN,Type.CYAN)),
            Task(false, mutableListOf(Type.RED,Type.RED)),
            Task(false, mutableListOf(Type.RED,Type.BLUE,Type.YELLOW))
        ))
        val pos6 = Pair(4,4)
        val tile7 = Tile(Type.YELLOW,Cost.ONE, mutableListOf())
        val pos7 = Pair(5,4)
        val tile8 = Tile(Type.BLUE,Cost.TWO, mutableListOf())
        val pos8 = Pair(4,5)

        currentGame.currentState.currentPlayer.placeTileOnGrid(tile1,pos1)
        currentGame.currentState.currentPlayer.placeTileOnGrid(tile2,pos2)
        currentGame.currentState.currentPlayer.placeTileOnGrid(tile3,pos3)
        currentGame.currentState.currentPlayer.placeTileOnGrid(tile4,pos4)
        currentGame.currentState.currentPlayer.placeTileOnGrid(tile5,pos5)
        currentGame.currentState.currentPlayer.placeTileOnGrid(tile6,pos6)
        currentGame.currentState.currentPlayer.placeTileOnGrid(tile7,pos7)
        currentGame.currentState.currentPlayer.placeTileOnGrid(tile8,pos8)

        // Reduce the tokens.
        currentGame.currentState.currentPlayer.tokens = 1

        // Place tile. 3 are completed, but because only 1 token is left, only one is marked as such
        rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[3],Pair(3,2))
        assertTrue(testRefreshable.refreshAfterMakeMoveCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterMakeMoveCalled)

        assertEquals(currentGame.currentState.currentPlayer.tokens,0)
        assertTrue { currentGame.currentState.currentPlayer.grid[3][2].tasks[0].completed }
        assertFalse { tile6.tasks[0].completed }
        assertFalse { tile6.tasks[1].completed }
        assertFalse { tile6.tasks[2].completed }
    }

    /**
     * Checks if makeMove works for eligible and fails for ineligible position.
     */
    @Test
    fun makeMovePositionEligibility(){
        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)

        // Create a game and get the players. Note that the game is a aiTournament so that the cardorder is controlled.
        val playerTriples = listOf(humanPlayer1Triple,humanPlayer2Triple,humanPlayer3Triple)
        assertNull(rootService.novaluna.game)
        rootService.gameService.startNewAITournamentGame(playerTriples, File("src/main/resources/default_CSV_SaveFile/nl_tiles.csv"))
        assertTrue(testRefreshable.refreshAfterStartNewGameCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterStartNewGameCalled)

        val currentGame = rootService.novaluna.game
        assertNotNull(currentGame)

        // Fails for nonexistent position
        assertFails { rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[1],Pair(-1,2)) }
        assertFails { rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[1],Pair(1,-1)) }
        assertFails { rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[1],Pair(10,20)) }
        assertFails { rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[1],Pair(-1,-22)) }

        assertFalse(testRefreshable.refreshAfterEndMoveCalled)
        // Place one tile. Now non-neighbouring positions are not eligible.
        rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[1],Pair(2,2))
        assertTrue(testRefreshable.refreshAfterMakeMoveCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterMakeMoveCalled)


        // Check that it fails for non-eligible positions.
        assertFails { rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[1],Pair(0,0)) }
        assertFails { rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[1],Pair(1,1)) }
        assertFails { rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[1],Pair(2,4)) }
        assertFails { rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[1],Pair(5,7)) }
        assertFalse(testRefreshable.refreshAfterMakeMoveCalled)
    }
}