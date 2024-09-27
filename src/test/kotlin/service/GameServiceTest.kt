package service

import kotlin.test.*
import entity.*
import java.io.File

/**
 * Test class for [GameService].
 */
class GameServiceTest {
    private val highscores = mutableListOf<Score>()
    private val novaluna = Novaluna(highscores,null)
    private val rootService = RootService(novaluna)
    private val gameService = GameService(rootService)

    private val humanPlayer1Triple = Triple("Player1", PlayerColour.BLACK,PlayerType.HUMAN)
    private val humanPlayer2Triple = Triple("Player2", PlayerColour.ORANGE,PlayerType.HUMAN)
    private val humanPlayer3Triple = Triple("Player3", PlayerColour.BLUE,PlayerType.HUMAN)
    private val humanPlayer4Triple = Triple("Player4", PlayerColour.WHITE,PlayerType.HUMAN)

    private val aiPlayer1Triple = Triple("Player1", PlayerColour.BLACK,PlayerType.EASY)
    private val aiPlayer2Triple = Triple("Player2", PlayerColour.ORANGE,PlayerType.NORMAL)
    private val aiPlayer3Triple = Triple("Player3", PlayerColour.BLUE,PlayerType.HARD)
    private val aiPlayer4Triple = Triple("Player4", PlayerColour.WHITE,PlayerType.HARD)

    /**
     * Tests if a game is created correctly if all parameters are set accordingly.
     */
    @Test
    fun startNewGameCaseOne(){
        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)

        val playerTriples = listOf(humanPlayer1Triple,humanPlayer2Triple,humanPlayer3Triple)
        assertNull(rootService.novaluna.game)
        rootService.gameService.startNewGame(playerTriples)
        assertTrue(testRefreshable.refreshAfterStartNewGameCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterStartNewGameCalled)

        val currentGame = rootService.novaluna.game

        // Check that a game was created.
        assertNotNull(currentGame)

        // Check that the properties of current game are set accordingly.
        assertFalse(currentGame.aiTournament)
        assertFalse(currentGame.choseUndo)
        assertEquals(currentGame.aiSpeed, 0)
        assertEquals(currentGame.tournamentProtocol, "")

        // Check that exactly one state exists.
        assertEquals(currentGame.states.size,1)

        // Check size of playerlist players
        val players = currentGame.states[0].players
        assertEquals(players.size, 3)

        // Check that the currentPlayer is correct
        assertEquals(players[0], currentGame.states[0].currentPlayer)

        // Check the grid for each player.
        players.forEach {
            assertEquals(it.tokens,20)
            assertFalse(it.choseTip)
            it.grid.forEach { array ->
                array.forEach { tile ->
                    assertEquals(tile,Tile())
                }
            }
        }

        // Check name, colour and type for each player.
        players[0].apply {
            assertEquals(name,"Player1")
            assertEquals(playerColour, PlayerColour.BLACK)
            assertEquals(playerType, PlayerType.HUMAN)
        }
        players[1].apply {
            assertEquals(name,"Player2")
            assertEquals(playerColour, PlayerColour.ORANGE)
            assertEquals(playerType, PlayerType.HUMAN)
        }
        players[2].apply {
            assertEquals(name,"Player3")
            assertEquals(playerColour, PlayerColour.BLUE)
            assertEquals(playerType, PlayerType.HUMAN)
        }

        val stack = currentGame.states[0].stack

        // Check that the size of the stack is correct.
        assertEquals(stack.size,68 - 11)

        // Check that the player positions on the moonwheel are correct.
        val moonwheel = currentGame.states[0].moonwheel
        assertEquals(moonwheel.playerPositions[0][0],players[0])
        assertEquals(moonwheel.playerPositions[0][1],players[1])
        assertEquals(moonwheel.playerPositions[0][2],players[2])
        moonwheel.playerPositions.forEachIndexed { index, mutableList ->  
            if (index != 0){
                assertEquals(mutableList.size, 0)
            }
        }

        // Check that the tiles on the moonwheel are correct
        assertEquals(moonwheel.tiles[0].type, Type.MEEPLE)
        moonwheel.tiles.forEachIndexed { index, tile ->
            if(index != 0){
                assertNotEquals(tile, Tile())
            }
        }
    }

    /**
     * Tests if startNewGame fails if there is already a currently active game.
     */
    @Test
    fun startNewGameCaseTwo(){
        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)
        val playerTriples = listOf(humanPlayer1Triple,humanPlayer2Triple,humanPlayer3Triple)
        assertNull(rootService.novaluna.game)
        rootService.gameService.startNewGame(playerTriples)
        assertTrue(testRefreshable.refreshAfterStartNewGameCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterStartNewGameCalled)

        // This call should fail because there is already a game.
        assertFails { gameService.startNewGame(playerTriples) }
    }

    /**
     * Tests if startNewGame fails if the wrong numbers of players are given.
     */
    @Test
    fun startNewGameCaseThree(){
        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)

        val playerTriplesTooFew = listOf(humanPlayer1Triple)
        val playerTriplesTooLong = listOf(
            humanPlayer1Triple,
            humanPlayer2Triple,
            humanPlayer3Triple,
            humanPlayer4Triple,
            aiPlayer1Triple
        )
        assertNull(rootService.novaluna.game)

        // This call should fail because there are too few players.
        assertFails { gameService.startNewGame(playerTriplesTooFew) }

        // This call should fail because there are too many players.
        assertFails { gameService.startNewGame(playerTriplesTooLong) }

        assertFalse(testRefreshable.refreshAfterStartNewGameCalled)
    }

    /**
     * Tests if startNewFirstGame works.
     *
     * Note that the method is similar to startNewGame, only the starting tokens are different.
     * This is why here, only the number of starting tokens are tested, everything else is covered in the tests above.
     */
    @Test
    fun startNewFirstGameOnlyCase(){
        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)

        val playerTriplesLength2 = listOf(humanPlayer1Triple, humanPlayer2Triple)
        val playerTriplesLength3 = listOf(aiPlayer1Triple,humanPlayer2Triple,aiPlayer3Triple)
        val playerTriplesLength4 = listOf(aiPlayer1Triple, humanPlayer2Triple, aiPlayer3Triple, humanPlayer4Triple)

        // Case: 2 players
        assertNull(rootService.novaluna.game)
        rootService.gameService.startNewFirstGame(playerTriplesLength2)
        assertTrue(testRefreshable.refreshAfterStartNewGameCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterStartNewGameCalled)

        val currentGameLength2 = rootService.novaluna.game
        assertNotNull(currentGameLength2)
        // Check that every player has 18-1 tokens.
        currentGameLength2.states[0].players.forEach {
            assertEquals(it.tokens,18-1)
        }

        // Reset game
        rootService.novaluna.game = null

        // Case: 3 players
        assertNull(rootService.novaluna.game)
        rootService.gameService.startNewFirstGame(playerTriplesLength3)
        assertTrue(testRefreshable.refreshAfterStartNewGameCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterStartNewGameCalled)

        val currentGameLength3 = rootService.novaluna.game
        assertNotNull(currentGameLength3)
        // Check that every player has 18-1 tokens.
        currentGameLength3.states[0].players.forEach {
            assertEquals(it.tokens,18-1)
        }

        // Reset game
        rootService.novaluna.game = null

        // Case: 4 players
        assertNull(rootService.novaluna.game)
        rootService.gameService.startNewFirstGame(playerTriplesLength4)
        assertTrue(testRefreshable.refreshAfterStartNewGameCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterStartNewGameCalled)

        val currentGameLength4 = rootService.novaluna.game
        assertNotNull(currentGameLength4)
        // Check that every player has 18-1 tokens.
        currentGameLength4.states[0].players.forEach {
            assertEquals(it.tokens,16-1)
        }
    }

    /**
     * Tests if startNewAITournament works.
     *
     * Note that the method is similar to startNewGame,
     * only that the aiTournament-flag is different, the gameConfigFile is specified, and it is not shuffled.
     * Thus, it is only tested whether the tiles are correct (in the test case they have to be ordered)
     * and whether the flags are correct.
     */
    @Test
    fun startNewAITournamentOnlyCase(){
        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)

        val playerTriples = listOf(humanPlayer1Triple, aiPlayer2Triple)
        assertNull(rootService.novaluna.game)
        rootService.gameService.startNewAITournamentGame(playerTriples, File("src/main/resources/default_CSV_SaveFile/nl_tiles.csv"))
        assertTrue(testRefreshable.refreshAfterStartNewGameCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterStartNewGameCalled)

        val currentGame = rootService.novaluna.game

        // Check that a game was created.
        assertNotNull(currentGame)

        // Check that the game is marked as an aiTournament.
        assertTrue(currentGame.aiTournament)

        // Check that the size of the stack is correct.
        assertEquals(currentGame.states[0].stack.size,68 - 11)

        // Check that the order of tiles is correct.
        val moonwheel = currentGame.states[0].moonwheel
        val stack = currentGame.states[0].stack
        // Start by creating the test tiles.
        val tile1 = Tile(
            Type.CYAN,
            Cost.ONE,
            mutableListOf(),
            1
        )
        val tile2 = Tile(
            Type.CYAN,
            Cost.TWO,
            mutableListOf(Task(false, mutableListOf(Type.CYAN,Type.CYAN,Type.CYAN,Type.CYAN))),
            2
        )
        val tile11 = Tile(
            Type.CYAN,
            Cost.FOUR,
            mutableListOf(
                Task(false, mutableListOf(Type.BLUE,Type.BLUE,Type.BLUE,Type.BLUE)),
                Task(false, mutableListOf(Type.BLUE,Type.BLUE))
            ),
            11
        )
        val tile12 = Tile(
            Type.CYAN,
            Cost.FIVE,
            mutableListOf(
                Task(false, mutableListOf(Type.CYAN,Type.CYAN)),
                Task(false, mutableListOf(Type.RED,Type.RED)),
                Task(false, mutableListOf(Type.YELLOW,Type.YELLOW))
            ),
            12
        )
        val tile68 = Tile(
            Type.YELLOW,
            Cost.SEVEN,
            mutableListOf(
                Task(false, mutableListOf(Type.CYAN,Type.RED)),
                Task(false, mutableListOf(Type.CYAN,Type.BLUE)),
                Task(false, mutableListOf(Type.RED,Type.BLUE))
            ),
            68
        )
        // Check some moonwheel tiles.
        assertEquals(moonwheel.tiles[0], Tile(Type.MEEPLE,Cost.ZERO, mutableListOf(),69))
        assertEquals(moonwheel.tiles[1],tile1)
        assertEquals(moonwheel.tiles[2],tile2)
        assertEquals(moonwheel.tiles[11],tile11)
        // Check some stack tiles.
        assertEquals(tile12,stack.first())
        assertEquals(tile68,stack.last())
    }

    /**
     * Tests if startNewAISimulationGame works, if all parameters are set correctly.
     *
     * Note that the method is similar to startNewGame, only the aiSpeed is set separately.
     * Thus, it is only checked whether aiSpeed is correct.
     */
    @Test
    fun startNewAISimulationGameCaseOne(){
        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)

        val playerTriples = listOf(aiPlayer1Triple,aiPlayer2Triple,aiPlayer3Triple,aiPlayer4Triple)
        assertNull(rootService.novaluna.game)
        rootService.gameService.startNewAISimulationGame(playerTriples,10)
        assertTrue(testRefreshable.refreshAfterStartNewGameCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterStartNewGameCalled)


        val currentGame = rootService.novaluna.game

        // Check that a game was created.
        assertNotNull(currentGame)

        // Check that aiSpeed is set correctly.
        assertEquals(currentGame.aiSpeed,10)
    }

    /**
     * Test if startNewAISimulationGame fails, if aiSpeed is strictly smaller 0.
     */
    @Test
    fun startNewAISimulationGameCaseTwo(){
        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)

        val playerTriples = listOf(aiPlayer1Triple,aiPlayer2Triple,aiPlayer3Triple,aiPlayer4Triple)
        assertNull(rootService.novaluna.game)


        // Check that call fails because of negative aiSpeed.
        assertFails { rootService.gameService.startNewAISimulationGame(playerTriples,-1) }
        assertFalse(testRefreshable.refreshAfterStartNewGameCalled)
    }

    /**
     * Checks that endGameCondition returns false if no condition satisfied.
     */
    @Test
    fun endGameConditionReachedCaseOne(){
        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)
        val playerTriples = listOf(humanPlayer1Triple,humanPlayer2Triple,humanPlayer3Triple)
        rootService.gameService.startNewAISimulationGame(playerTriples,0)
        assertTrue(testRefreshable.refreshAfterStartNewGameCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterStartNewGameCalled)

        val currentGame = rootService.novaluna.game
        assertNotNull(currentGame)

        assertFalse { gameService.endGameConditionReached(currentGame.currentState) }
    }

    /**
     * Checks that endGameCondition returns true if currentPlayer has 0 tokens.
     */
    @Test
    fun endGameConditionReachedCaseTwo(){
        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)

        val playerTriples = listOf(humanPlayer1Triple,humanPlayer2Triple,humanPlayer3Triple)
        rootService.gameService.startNewAISimulationGame(playerTriples,0)
        assertTrue(testRefreshable.refreshAfterStartNewGameCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterStartNewGameCalled)

        val currentGame = rootService.novaluna.game
        assertNotNull(currentGame)

        currentGame.currentState.currentPlayer.tokens = 0

        assertTrue { gameService.endGameConditionReached(currentGame.currentState) }
    }

    /**
     * Checks that endGameCondition returns true if moonwheel and stack are empty.
     */
    @Test
    fun endGameConditionReachedCaseThree(){
        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)
        val playerTriples = listOf(humanPlayer1Triple,humanPlayer2Triple,humanPlayer3Triple)
        rootService.gameService.startNewAISimulationGame(playerTriples,0)
        assertTrue(testRefreshable.refreshAfterStartNewGameCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterStartNewGameCalled)

        val currentGame = rootService.novaluna.game
        assertNotNull(currentGame)

        currentGame.currentState.stack = mutableListOf()
        currentGame.currentState.moonwheel.tiles.forEachIndexed { index, tile ->
            if (tile.type != Type.MEEPLE){
                currentGame.currentState.moonwheel.tiles[index] = Tile()
            }
        }

        assertTrue { gameService.endGameConditionReached(currentGame.currentState) }
    }
}