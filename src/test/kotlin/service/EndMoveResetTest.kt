package service

import entity.*
import kotlin.test.*

/**
 * Test class for methods endMove and reset from [GameService].
 */
class EndMoveResetTest {

    private val highscores = mutableListOf<Score>()
    private val novaluna = Novaluna(highscores,null)
    private val rootService = RootService(novaluna)
    private val gameService = GameService(rootService)

    private val humanPlayer1Triple = Triple("Player1", PlayerColour.BLACK, PlayerType.HUMAN)
    private val humanPlayer2Triple = Triple("Player2", PlayerColour.ORANGE, PlayerType.HUMAN)
    private val humanPlayer3Triple = Triple("Player3", PlayerColour.BLUE, PlayerType.HUMAN)
    private val humanPlayer4Triple = Triple("Player4", PlayerColour.WHITE, PlayerType.HUMAN)

    private val player1 = Player(humanPlayer1Triple.first,humanPlayer1Triple.second,humanPlayer1Triple.third,20)
    private val player2 = Player(humanPlayer2Triple.first,humanPlayer2Triple.second,humanPlayer2Triple.third,20)

    /**
     * Check if reset works correctly if there is a current game.
     */
    @Test
    fun resetCase1(){
        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)

        val playerTriples = listOf(humanPlayer1Triple,humanPlayer2Triple,humanPlayer3Triple)
        assertNull(rootService.novaluna.game)
        rootService.gameService.startNewGame(playerTriples)
        assertTrue(testRefreshable.refreshAfterStartNewGameCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterStartNewGameCalled)


        // Check that a game was created.
        assertNotNull(rootService.novaluna.game)

        // Reset and check
        rootService.gameService.reset()
        assertTrue(testRefreshable.refreshAfterResetCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterResetCalled)

        assertNull(rootService.novaluna.game)
    }

    /**
     * Check if reset works correctly if there is no current game.
     */
    @Test
    fun resetCase2(){
        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)

        assertNull(rootService.novaluna.game)

        // Reset and check
        rootService.gameService.reset()
        assertTrue(testRefreshable.refreshAfterResetCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterResetCalled)

        assertNull(rootService.novaluna.game)
    }

    /**
     * Check that the player positions are updated correctly.
     */
    @Test
    fun endMovePlayerPosition(){
        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)

        val playerTriples = listOf(humanPlayer1Triple,humanPlayer2Triple,humanPlayer3Triple,humanPlayer4Triple)
        assertNull(rootService.novaluna.game)
        rootService.gameService.startNewGame(playerTriples)
        assertTrue(testRefreshable.refreshAfterStartNewGameCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterStartNewGameCalled)


        val currentGame = rootService.novaluna.game

        // Check that a game was created and that player1 is the current player.
        assertNotNull(currentGame)
        assertEquals(currentGame.currentState.currentPlayer,player1)

        // Check that currentPlayer stays the same if they are stacked.
        rootService.gameService.endMove()
        assertEquals(currentGame.currentState.currentPlayer,player1)
        assertTrue(testRefreshable.refreshAfterEndMoveCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterEndMoveCalled)


        // Check that player1 stays current player if there is no one in range behind him
        currentGame.currentState.moonwheel.playerPositions[0].removeFirst()
        currentGame.currentState.moonwheel.playerPositions[22].add(currentGame.currentState.currentPlayer)
        rootService.gameService.endMove()
        assertTrue(testRefreshable.refreshAfterEndMoveCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterEndMoveCalled)

        assertEquals(currentGame.currentState.currentPlayer,player1)

        // Check that player1 becomes the current player if he is the first in range.
        currentGame.currentState.currentPlayer = currentGame.currentState.moonwheel.playerPositions[0][0]
        rootService.gameService.endMove()
        assertTrue(testRefreshable.refreshAfterEndMoveCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterEndMoveCalled)

        assertEquals(currentGame.currentState.currentPlayer,player1)

        // Check that player2 becomes the currentPlayer if he is on top of stack and the first in range.
        currentGame.currentState.moonwheel.playerPositions[22].removeFirst()
        currentGame.currentState.moonwheel.playerPositions[7].add(currentGame.currentState.currentPlayer)
        rootService.gameService.endMove()
        //assertTrue(testRefreshable.refreshAfterEndMoveCalled)
        //testRefreshable.reset()

        //assertFalse(testRefreshable.refreshAfterEndMoveCalled)

        assertEquals(currentGame.currentState.currentPlayer,player2)

        // Check that player1 stays the currentPlayer if the others are just out of range.
        currentGame.currentState.currentPlayer = currentGame.currentState.moonwheel.playerPositions[7].removeFirst()
        currentGame.currentState.moonwheel.playerPositions[8].add(currentGame.currentState.currentPlayer)
        rootService.gameService.endMove()
        assertTrue(testRefreshable.refreshAfterEndMoveCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterEndMoveCalled)

        assertEquals(currentGame.currentState.currentPlayer,player1)

        // Check that player2 stays the currentPlayer if he has no more tokens
        currentGame.currentState.currentPlayer = currentGame.currentState.moonwheel.playerPositions[0][0]
        currentGame.currentState.currentPlayer.tokens = 0
        rootService.gameService.endMove()
        assertTrue(testRefreshable.refreshAfterEndGameCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterEndGameCalled)

        assertEquals(currentGame.currentState.currentPlayer,player2.apply { tokens = 0 })
    }

    /**
     * Check that the moonwheel tiles are filled if there are none left.
     */
    @Test
    fun endMoveMoonwheelTiles(){
        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)

        val playerTriples = listOf(humanPlayer1Triple,humanPlayer2Triple,humanPlayer3Triple,humanPlayer4Triple)
        assertNull(rootService.novaluna.game)
        rootService.gameService.startNewGame(playerTriples)
        assertTrue(testRefreshable.refreshAfterStartNewGameCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterStartNewGameCalled)

        val currentGame = rootService.novaluna.game
        assertNotNull(currentGame)

        // Set some moonwheel tiles to empty and check that the tiles are not refilled.
        assertEquals(currentGame.currentState.moonwheel.tiles.count { it == Tile() }, 0)
        currentGame.currentState.moonwheel.tiles[1] = Tile()
        currentGame.currentState.moonwheel.tiles[5] = Tile()
        currentGame.currentState.moonwheel.tiles[7] = Tile()
        currentGame.currentState.moonwheel.tiles[10] = Tile()
        assertEquals(currentGame.currentState.moonwheel.tiles.count { it == Tile() }, 4)
        rootService.gameService.endMove()
        assertTrue(testRefreshable.refreshAfterEndMoveCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterEndMoveCalled)

        assertEquals(currentGame.currentState.moonwheel.tiles.count { it == Tile() }, 4)

        // Set all tiles empty except meeple to empty and check that the moonwheel is refilled.
        currentGame.currentState.moonwheel.tiles.forEachIndexed { index,tile ->
            if(tile.type != Type.MEEPLE){
                currentGame.currentState.moonwheel.tiles[index] = Tile()
            }
        }
        assertEquals(currentGame.currentState.moonwheel.tiles.count { it == Tile() }, 11)
        rootService.gameService.endMove()
        assertTrue(testRefreshable.refreshAfterEndMoveCalled)
        assertTrue(testRefreshable.refreshAfterRefillTilesCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterEndMoveCalled)

        assertEquals(currentGame.currentState.moonwheel.tiles.count { it == Tile() }, 0)

        // Now check that it is an endgame condition if the moonwheel can not be refilled.
        // Check this by checking that the current player does not get advanced.
        // First empty the moonwheel and the stack.
        currentGame.currentState.moonwheel.tiles.forEachIndexed { index,tile ->
            if(tile.type != Type.MEEPLE){
                currentGame.currentState.moonwheel.tiles[index] = Tile()
            }
        }
        assertEquals(currentGame.currentState.moonwheel.tiles.count { it == Tile() }, 11)
        currentGame.currentState.stack.removeAll { true }
        assertTrue(currentGame.currentState.stack.isEmpty())
        // Then advance the current player, so that player two should be the current player.
        currentGame.currentState.moonwheel.playerPositions[0].removeFirst()
        currentGame.currentState.moonwheel.playerPositions[1].add(currentGame.currentState.currentPlayer)
        assertEquals(currentGame.currentState.currentPlayer,player1)
        // Now call endMove and see that nothing happens regarding tiles and currentplayer
        rootService.gameService.endMove()
        assertTrue(testRefreshable.refreshAfterEndGameCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterEndGameCalled)

        assertEquals(currentGame.currentState.moonwheel.tiles.count { it == Tile() }, 11)
        assertEquals(currentGame.currentState.currentPlayer,player1)
    }

    /**
     * Checks that endMove fails if there is no currently active game.
     */
    @Test
    fun endMoveNoActiveGame(){
        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)

        assertFails { gameService.endMove() }
        assertFalse(testRefreshable.refreshAfterEndMoveCalled)
    }
}