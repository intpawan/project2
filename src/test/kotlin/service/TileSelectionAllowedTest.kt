package service

import entity.*
import org.junit.jupiter.api.Test
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Test class for method tileSelectionAllowed from [PlayerActionService].
 */
class TileSelectionAllowedTest {

    private val highscores = mutableListOf<Score>()
    private val novaluna = Novaluna(highscores,null)
    private val rootService = RootService(novaluna)
    private val gameService = GameService(rootService)
    private val playerActionService = PlayerActionService(rootService)

    private val humanPlayer1Triple = Triple("Player1", PlayerColour.BLACK, PlayerType.HUMAN)
    private val humanPlayer2Triple = Triple("Player2", PlayerColour.ORANGE, PlayerType.HUMAN)
    private val humanPlayer3Triple = Triple("Player3", PlayerColour.BLUE, PlayerType.HUMAN)

    /**
     * Check that tileSelectionAllowed fails if a noncoloured tile is selected.
     */
    @Test
    fun tileSelectionAllowedCaseOne(){
        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)

        val playerTriples = listOf(humanPlayer1Triple,humanPlayer2Triple,humanPlayer3Triple)
        rootService.gameService.startNewAISimulationGame(playerTriples,0)
        assertTrue(testRefreshable.refreshAfterStartNewGameCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterStartNewGameCalled)

        val currentGame = rootService.novaluna.game
        assertNotNull(currentGame)

        assertFails { rootService.playerActionService.tileSelectionAllowed(
            Tile(Type.MEEPLE, Cost.ZERO, mutableListOf(),69),
            currentGame.currentState
        ) }

        assertFails { rootService.playerActionService.tileSelectionAllowed(
            currentGame.currentState.moonwheel.tiles[0],
            currentGame.currentState
        ) }

        assertFails { rootService.playerActionService.tileSelectionAllowed(
            Tile(),
            currentGame.currentState
        ) }
    }

    /**
     * Check that tileSelectionAllowed works correctly if a coloured tile is selected without modulo wrap-around.
     */
    @Test
    fun tileSelectionAllowedCaseTwo(){
        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)

        val playerTriples = listOf(humanPlayer1Triple,humanPlayer2Triple,humanPlayer3Triple)
        rootService.gameService.startNewAISimulationGame(playerTriples,0)
        assertTrue(testRefreshable.refreshAfterStartNewGameCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterStartNewGameCalled)

        val currentGame = rootService.novaluna.game
        assertNotNull(currentGame)

        val moonwheel = currentGame.currentState.moonwheel

        // Check for starting position
        for (i in 1 .. 11){
            if(i in 1..3){
                assertTrue { rootService.playerActionService.tileSelectionAllowed(moonwheel.tiles[i],currentGame.currentState) }
            }else{
                assertFalse { rootService.playerActionService.tileSelectionAllowed(moonwheel.tiles[i],currentGame.currentState) }
            }
        }

        // Include empty tile at 2, thus 4 is also eligible, but 2 is not
        moonwheel.tiles[2] = Tile()
        for (i in 1 .. 11){
            if(i in setOf(1,3,4)){
                assertTrue { rootService.playerActionService.tileSelectionAllowed(moonwheel.tiles[i],currentGame.currentState) }
            }else if(i in setOf(2)){
                assertFails { rootService.playerActionService.tileSelectionAllowed(moonwheel.tiles[i],currentGame.currentState) }
            }else{
                assertFalse { rootService.playerActionService.tileSelectionAllowed(moonwheel.tiles[i],currentGame.currentState) }
            }
        }

        // Include empty tile at 8, not changing the eligible tiles
        moonwheel.tiles[8] = Tile()
        for (i in 1 .. 11){
            if(i in setOf(1,3,4)){
                assertTrue { rootService.playerActionService.tileSelectionAllowed(moonwheel.tiles[i],currentGame.currentState) }
            }else if(i in setOf(2,8)){
                assertFails { rootService.playerActionService.tileSelectionAllowed(moonwheel.tiles[i],currentGame.currentState) }
            }else{
                assertFalse { rootService.playerActionService.tileSelectionAllowed(moonwheel.tiles[i],currentGame.currentState) }
            }
        }
    }

    /**
     * Check that tileSelectionAllowed works correctly if a coloured tile is selected with modulo wrap-around.
     */
    @Test
    fun tileSelectionAllowedCaseThree(){
        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)

        val playerTriples = listOf(humanPlayer1Triple,humanPlayer2Triple,humanPlayer3Triple)
        rootService.gameService.startNewAISimulationGame(playerTriples,0)
        assertTrue(testRefreshable.refreshAfterStartNewGameCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterStartNewGameCalled)

        val currentGame = rootService.novaluna.game
        assertNotNull(currentGame)

        val moonwheel = currentGame.currentState.moonwheel

        // Switch tiles at 0 (meeple) and 10
        val temp = moonwheel.tiles[10]
        moonwheel.tiles[10] = moonwheel.tiles[0]
        moonwheel.tiles[0] = temp

        // Eligible: 11,0,1
        for (i in 0 .. 11){
            if(i in setOf(11,0,1)){
                assertTrue { playerActionService.tileSelectionAllowed(moonwheel.tiles[i],currentGame.currentState) }
            }else if(i in setOf(10)){
                assertFails { playerActionService.tileSelectionAllowed(moonwheel.tiles[i],currentGame.currentState) }
            }else{
                assertFalse { playerActionService.tileSelectionAllowed(moonwheel.tiles[i],currentGame.currentState) }
            }
        }

        // Include empty tile at 1. Eligible: 11,0,2
        moonwheel.tiles[1] = Tile()
        for (i in 0 .. 11){
            if(i in setOf(11,0,2)){
                assertTrue { playerActionService.tileSelectionAllowed(moonwheel.tiles[i],currentGame.currentState) }
            }else if(i in setOf(10,1)){
                assertFails { playerActionService.tileSelectionAllowed(moonwheel.tiles[i],currentGame.currentState) }
            }else{
                assertFalse { playerActionService.tileSelectionAllowed(moonwheel.tiles[i],currentGame.currentState) }
            }
        }

        // Include empty tile at 0. Eligible: 11,2,3
        moonwheel.tiles[0] = Tile()
        for (i in 0 .. 11){
            if(i in setOf(11,2,3)){
                assertTrue { playerActionService.tileSelectionAllowed(moonwheel.tiles[i],currentGame.currentState) }
            }else if(i in setOf(10,1,0)){
                assertFails { playerActionService.tileSelectionAllowed(moonwheel.tiles[i],currentGame.currentState) }
            }else{
                assertFalse { playerActionService.tileSelectionAllowed(moonwheel.tiles[i],currentGame.currentState) }
            }
        }

        // Include empty tile at 8. Eligible: 11,2,3
        moonwheel.tiles[8] = Tile()
        for (i in 0 .. 11){
            if(i in setOf(11,2,3)){
                assertTrue { playerActionService.tileSelectionAllowed(moonwheel.tiles[i],currentGame.currentState) }
            }else if(i in setOf(10,1,0,8)){
                assertFails { playerActionService.tileSelectionAllowed(moonwheel.tiles[i],currentGame.currentState) }
            }else{
                assertFalse { playerActionService.tileSelectionAllowed(moonwheel.tiles[i],currentGame.currentState) }
            }
        }
    }

    /**
     * Check that tileSelectionAllowed fails if moonwheel is not consistent.
     */
    @Test
    fun tileSelectionAllowedCaseFour(){
        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)

        val playerTriples = listOf(humanPlayer1Triple,humanPlayer2Triple,humanPlayer3Triple)
        rootService.gameService.startNewAISimulationGame(playerTriples,0)
        assertTrue(testRefreshable.refreshAfterStartNewGameCalled)
        testRefreshable.reset()

        assertFalse(testRefreshable.refreshAfterStartNewGameCalled)

        val currentGame = rootService.novaluna.game
        assertNotNull(currentGame)

        val moonwheel = currentGame.currentState.moonwheel

        // Fails if tile not included
        assertFails {
            playerActionService.tileSelectionAllowed(
                Tile(Type.CYAN,Cost.ZERO, mutableListOf(),70),
                currentGame.currentState)
        }

        // Fails if tiles is twice on moonwheel
        moonwheel.tiles[7] = moonwheel.tiles[8]
        assertFails { playerActionService.tileSelectionAllowed(moonwheel.tiles[7], currentGame.currentState) }

        // Fails if meeple is twice on moonwheel
        moonwheel.tiles[7] = Tile(Type.MEEPLE,Cost.ZERO, mutableListOf(),69)
        assertFails { playerActionService.tileSelectionAllowed(moonwheel.tiles[7], currentGame.currentState) }

        // Fails if meeple is not on the moonwheel at all
        moonwheel.tiles[0] = Tile()
        moonwheel.tiles[7] = Tile()
        assertFails { playerActionService.tileSelectionAllowed(moonwheel.tiles[7], currentGame.currentState) }
    }
}