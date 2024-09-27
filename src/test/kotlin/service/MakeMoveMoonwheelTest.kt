package service

import entity.*
import java.io.File
import kotlin.test.*

/**
 * Test class for [Moonwheel]-related functionality in method makeMove from [PlayerActionService].
 */
class MakeMoveMoonwheelTest {

    private val highscores = mutableListOf<Score>()
    private val novaluna = Novaluna(highscores,null)
    private val rootService = RootService(novaluna)
    private val gameService = GameService(rootService)
    private val playerActionService = PlayerActionService(rootService)

    private val humanPlayer1Triple = Triple("Player1", PlayerColour.BLACK, PlayerType.HUMAN)
    private val humanPlayer2Triple = Triple("Player2", PlayerColour.ORANGE, PlayerType.HUMAN)
    private val humanPlayer3Triple = Triple("Player3", PlayerColour.BLUE, PlayerType.HUMAN)

    /**
     * Check that makeMove fails if there is no currently active game.
     */
    @Test
    fun makeMoveNoActiveGame(){
        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)

        assertFails { playerActionService.makeMove(Tile(),Pair(0,0)) }
        assertFalse(testRefreshable.refreshAfterEndMoveCalled)
    }

    /**
     * Check that makeMove fails if
     * (1) a tile is selected that is not selectable because it is out of the meeple range
     * (2) a tile is selected that is not on the moonwheel
     * (3) a tile is selected that is not coloured
     * (4) there is no meeple on the moonwheel
     */
    @Test
    fun makeMoveMoonwheelTileSelection(){
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

        // Check the above scenarios mentioned above
        assertFails { rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[5],Pair(5,5)) }
        assertFails { rootService.playerActionService.makeMove(Tile(Type.YELLOW,Cost.SEVEN, mutableListOf(),4),Pair(5,5)) }
        assertFails { rootService.playerActionService.makeMove(Tile(Type.MEEPLE,Cost.SEVEN, mutableListOf(),4),Pair(5,5)) }
        assertFails { rootService.playerActionService.makeMove(Tile(Type.EMPTY,Cost.SEVEN, mutableListOf(),4),Pair(5,5)) }
        currentGame.currentState.moonwheel.tiles[0] = Tile()
        assertFails { rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[1],Pair(5,5)) }
        assertFalse(testRefreshable.refreshAfterMakeMoveCalled)
    }

    /**
     * Check that after a move is made, the tiles on the moonwheel are updated correctly.
     */
    @Test
    fun makeMoveMoonwheelTileUpdate(){
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

        // Check that initially the meeple is at position 0
        assertTrue { currentGame.currentState.moonwheel.tiles[0].type == Type.MEEPLE }

        // Make a move and check tiles
        rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[2],Pair(5,5))
        assertTrue { currentGame.currentState.moonwheel.tiles[0] == Tile() }
        assertTrue { currentGame.currentState.moonwheel.tiles[2].type == Type.MEEPLE }
        assertTrue(testRefreshable.refreshAfterMakeMoveCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterMakeMoveCalled)


        // Make another move and check again
        rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[5],Pair(5,6))
        assertTrue { currentGame.currentState.moonwheel.tiles[2] == Tile() }
        assertTrue { currentGame.currentState.moonwheel.tiles[5].type == Type.MEEPLE }
        assertTrue(testRefreshable.refreshAfterMakeMoveCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterMakeMoveCalled)


        // Now place the moonwheel at position 11 and check that the modulo-wrap works
        currentGame.currentState.moonwheel.tiles[5] = Tile()
        currentGame.currentState.moonwheel.tiles[11] = Tile(Type.MEEPLE,Cost.ZERO, mutableListOf(),69)
        assertFails { rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[3],Pair(4,7)) }
        assertFails { rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[9],Pair(4,7)) }
        assertFails { rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[11],Pair(4,7)) }
        assertFalse(testRefreshable.refreshAfterMakeMoveCalled)

        rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[1],Pair(5,7))
        assertTrue { currentGame.currentState.moonwheel.tiles[11] == Tile() }
        assertTrue { currentGame.currentState.moonwheel.tiles[1].type == Type.MEEPLE }
        assertTrue(testRefreshable.refreshAfterMakeMoveCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterMakeMoveCalled)

    }

    /**
     * Check that makeMove works correctly in regard to the player movement.
     */
    @Test
    fun makeMoveMoonwheelPlayerPosition1(){
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

        // Check that at the beginning the current player is the highest player a position 0 and is contained
        // in total only once.
        assertEquals( currentGame.currentState.moonwheel.playerPositions[0][0], currentGame.currentState.currentPlayer )

        // Make move and check new player positions
        rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[1],Pair(0,0)) // Tile cost: 1
        assertTrue(testRefreshable.refreshAfterMakeMoveCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterMakeMoveCalled)

        assertNotEquals(
            currentGame.currentState.moonwheel.playerPositions[0][0], currentGame.currentState.currentPlayer
        )
        assertEquals(
            currentGame.currentState.moonwheel.playerPositions[1], listOf(currentGame.currentState.currentPlayer)
        )

        // Make move and check new player positions
        rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[4],Pair(0,1)) // Tile cost: 3
        assertTrue(testRefreshable.refreshAfterMakeMoveCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterMakeMoveCalled)

        assertTrue { currentGame.currentState.moonwheel.playerPositions[1].isEmpty() }
        assertEquals(
            currentGame.currentState.moonwheel.playerPositions[4], listOf(currentGame.currentState.currentPlayer)
        )

        // Move to position 22 and check the modulo wrap
        currentGame.currentState.moonwheel.playerPositions[4] = mutableListOf()
        currentGame.currentState.moonwheel.playerPositions[22] = mutableListOf(currentGame.currentState.currentPlayer)
        rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[7],Pair(0,2)) // Tile cost: 3
        assertTrue(testRefreshable.refreshAfterMakeMoveCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterMakeMoveCalled)

        assertTrue { currentGame.currentState.moonwheel.playerPositions[22].isEmpty() }
        assertEquals(
            currentGame.currentState.moonwheel.playerPositions[1], listOf(currentGame.currentState.currentPlayer)
        )

        // Move to position 20 and check that the player is stacked on top of the players at position 0
        currentGame.currentState.moonwheel.playerPositions[1] = mutableListOf()
        currentGame.currentState.moonwheel.playerPositions[20] = mutableListOf(currentGame.currentState.currentPlayer)
        rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[8],Pair(0,3)) // Tile cost: 4
        assertTrue(testRefreshable.refreshAfterMakeMoveCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterMakeMoveCalled)
        assertTrue { currentGame.currentState.moonwheel.playerPositions[20].isEmpty() }
        assertEquals(currentGame.currentState.moonwheel.playerPositions[0][0], currentGame.currentState.currentPlayer)
        assertEquals(currentGame.currentState.moonwheel.playerPositions[0], currentGame.currentState.players)
    }

    /**
     * Check that makeMove does not work if
     * (1) the currentPlayer is not on top of the playerStack at his position
     * (2) the currentPlayer is contained more than once on the moonwheel.
     */
    @Test
    fun makeMovePlayerPosition2(){
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

        // Set the currentPlayer to the second and then third player in the player list. makeMove should fail now.
        currentGame.currentState.currentPlayer = currentGame.currentState.players[1]
        assertFails { rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[1],Pair(0,0)) }
        currentGame.currentState.currentPlayer = currentGame.currentState.players[2]
        assertFails { rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[1],Pair(0,0)) }
        assertFalse(testRefreshable.refreshAfterEndMoveCalled)

        // Reset the currentPlayer. Then insert the currentPlayer in a second position. makeMove should fail now.
        currentGame.currentState.currentPlayer = currentGame.currentState.players[0]
        currentGame.currentState.moonwheel.playerPositions[7].add(currentGame.currentState.currentPlayer)
        assertFails { rootService.playerActionService.makeMove(currentGame.currentState.moonwheel.tiles[1],Pair(0,0)) }
        assertFalse(testRefreshable.refreshAfterEndMoveCalled)
    }
}