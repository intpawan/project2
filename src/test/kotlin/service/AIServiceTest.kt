package service
import entity.*
import java.io.File
import kotlin.test.*

/**
 * Test class for AI Service to test all methods of a class
 * and verify correctly functionality of NPC
 */
class AIServiceTest {
    private val highscores = mutableListOf<Score>()
    //private val novaluna = Novaluna(highscores,null)


    private val p1 = Player("Tom", PlayerColour.BLACK, PlayerType.HUMAN,4)
    private val p2 = Player("Tim", PlayerColour.BLUE, PlayerType.HUMAN,2)
    private val playersList = mutableListOf(p1,p2)

    // tasks for tile 1 (stack)
    private val task1 = Task(false, mutableListOf(Type.RED, Type.YELLOW))
    private val task2 = Task(false, mutableListOf(Type.CYAN, Type.BLUE))

    // tasks for tile 2(stack)
    private val task3 = Task(false, mutableListOf(Type.RED, Type.YELLOW))
    private val task4 = Task(false, mutableListOf(Type.CYAN, Type.YELLOW))

    // tasks for tile 3 (moon wheel)
    private val task5 = Task(false, mutableListOf(Type.RED, Type.YELLOW))
    private val task6 = Task(false, mutableListOf(Type.CYAN, Type.BLUE))

    // tasks for tile 4 (moon wheel)
    private val task7 = Task(false, mutableListOf(Type.YELLOW, Type.YELLOW))
    private val task8 = Task(false, mutableListOf(Type.CYAN, Type.BLUE))

    // tasks for tile 5
    private val task9 = Task(false, mutableListOf(Type.CYAN, Type.CYAN))
    private val task10 = Task(false, mutableListOf(Type.RED, Type.YELLOW))
    //Tasks for tile 6
    private val task11 = Task(false, mutableListOf(Type.RED, Type.RED))
    private val task12 = Task(false, mutableListOf(Type.RED, Type.CYAN, Type.RED))
    // tasks for tile 7
    //private val task13 = Task(false, mutableListOf(Type.YELLOW, Type.YELLOW, Type.CYAN))
    //private val task14 = Task(false, mutableListOf(Type.RED, Type.RED, Type.YELLOW))

    // tiles for stack
    private val tile11 = Tile(Type.CYAN,Cost.TWO, mutableListOf(Task(false, mutableListOf(Type.CYAN))))
    private val tile22 = Tile(Type.CYAN,Cost.ONE, mutableListOf(Task(false, mutableListOf(Type.CYAN))))
    private val tile1 = Tile(Type.RED, Cost.FIVE, mutableListOf(task1,task2))
    private val tile2 = Tile(Type.YELLOW, Cost.SEVEN, mutableListOf(task3, task4))
    private val tile3 = Tile(Type.RED, Cost.FIVE, mutableListOf(task5,task6))
    private val tile4 = Tile(Type.YELLOW, Cost.SEVEN, mutableListOf(task7, task8))
    private val tile5 = Tile(Type.RED, Cost.FOUR, mutableListOf(task9, task10))
    private val tile6 = Tile(Type.YELLOW, Cost.SEVEN, mutableListOf(task11, task12))
    //private val tile7 = Tile(Type.CYAN, Cost.FOUR, mutableListOf(task13, task14))

    // Moonwheel with empty tiles
    var moonwheel = Moonwheel()

    private val tilesList1: MutableList<Tile> = mutableListOf(tile1, tile2, tile3, tile4, tile5, tile6)
    private val tilesList2: MutableList<Tile> = mutableListOf(tile1, tile2)

    private val state1 = State(tilesList1, moonwheel, p1, playersList,"")
    private val state2 = State(tilesList2, moonwheel, p1, playersList,"")

    /**
     * Test whether method IsTerminal works correctly:
     * case 1: stack of tiles is empty and there is no tile on the moonwheel
     * case 2: true if someone has won
     */
    /**
     * case2
     */
    @Test
    fun testIsTerminalSomeoneWon(){
        val p1 = Player("Tom", PlayerColour.BLACK, PlayerType.HUMAN,2)
        val p2 = Player("Tim", PlayerColour.BLUE, PlayerType.HUMAN,0)
        val playersList = mutableListOf(p1,p2)

        //case 2
        val state1 = State(tilesList1, moonwheel, p2, playersList,"")
        val testGame1 = Game(state1, false, "", false, 0, mutableListOf(state1))
        val rootService =  RootService(novaluna = Novaluna(highscores,testGame1))
        val aiService = AIService(rootService)

        assertEquals(true, aiService.isTerminal(state1))
    }

    /**
     * case 1
     */
    @Test
    fun testIsTerminalEmptyStack(){
        val tilesList = arrayOf<Tile>()

        //case 1
        moonwheel = Moonwheel()
        val state2 = State(tilesList.toMutableList(), moonwheel, p1, playersList,"")
        val testGame1 = Game(state2, false, "", false, 0, mutableListOf(state1))
        //val desiredWinnerStateTest = State(tilesList1,moonwheel,p2, playersList)
        val rootService =  RootService(novaluna = Novaluna(highscores,testGame1))
        val aiService = AIService(rootService)


        assertEquals(true, aiService.isTerminal(state2))
    }
    /**
     * Test whether method IsTerminal works correctly and the output is false if no condition for
     * termination holds
     */
    @Test
    fun testIsTerminal(){
        moonwheel.tiles[4]= Tile(Type.MEEPLE, Cost.ZERO, mutableListOf())
        moonwheel.tiles[0]= tile1
        val p6 = Player("Tom", PlayerColour.BLACK, PlayerType.HUMAN,5)
        val players = mutableListOf(p1,p6)
        val state2 = State(tilesList1, moonwheel, p6, players,"")

        val testGame1 = Game(state2, false, "", false, 0, mutableListOf(state2))
        val rootService =  RootService(novaluna = Novaluna(highscores,testGame1))
        val aiService = AIService(rootService)
        assertEquals(false, aiService.isTerminal(state2))
    }

    /**
     * Test for the method getValidTiles in order to find tiles that current player can peek
     */
    @Test
    fun testGetValidTiles(){
        val moonwheel = Moonwheel()
        // meeple position 2
        moonwheel.tiles[6] = Tile(Type.MEEPLE, Cost.ZERO, mutableListOf())
        // available tiles positions: 3,5,7
        moonwheel.tiles[3] = tile1
        //  moonwheel.tiles[5] = tile2
        moonwheel.tiles[7] = tile3
        //moonwheel.tiles[8] = tile4

        val state1 = State(tilesList1, moonwheel, p2, playersList,"")
        val listOfAvailableTilesToTest = arrayOf(tile2,tile3,tile4)

        val testGame1 = Game(state1, false, "", false, 0, mutableListOf(state1))

        val rootService =  RootService(novaluna = Novaluna(highscores,testGame1))
        val aiService = AIService(rootService)



        val listOfAvailableTiles = aiService.getValidTiles(state1)
        //ckeck for tiles on moonwheel
        assertEquals(listOfAvailableTilesToTest[0],tile2)
        assertEquals(listOfAvailableTilesToTest[1],tile3)
        assertEquals(listOfAvailableTilesToTest[2],tile4)
        //check for tiles from the output of the method
        assertEquals(listOfAvailableTiles[0], tile3)
        assertEquals(listOfAvailableTiles[1], tile1)
        //check of the size
        assertEquals(2, listOfAvailableTiles.size)
    }

    /**
     * Test whether method getPositions works correctly and the output is list of possible
     * positions of a grid of a current player.
     */
    @Test
    fun testGetPositions(){
        val p3 = Player("Ann", PlayerColour.BLACK, PlayerType.HUMAN,5)
        p3.grid[2][4] = tile1

        val testGame1 = Game(state1, false, "", false, 0, mutableListOf(state1))

        val rootService =  RootService(novaluna = Novaluna(highscores,testGame1))
        val aiService = AIService(rootService)


        val possiblePositions = aiService.getPositions(p3)
        val possiblePositionsTotTest = mutableListOf(Pair(1,4), Pair(2,3), Pair(2,5), Pair(3,4))

        assertEquals(Pair(1,4), possiblePositions[0])
        assertEquals(Pair(1,4), possiblePositionsTotTest[0])
        assertEquals(possiblePositionsTotTest[0], possiblePositions[0])
        assertEquals(possiblePositionsTotTest[1], possiblePositions[1])
        assertEquals(possiblePositionsTotTest[2], possiblePositions[2])
        assertEquals(possiblePositionsTotTest[3], possiblePositions[3])

        assertEquals(possiblePositionsTotTest.size, possiblePositions.size)

        // player 4 grid is empty
        val p4 = Player("Maria", PlayerColour.ORANGE, PlayerType.HUMAN,4)
        val possiblePositions1 = aiService.getPositions(p4)
        val expectedSize = 81
        assertEquals(expectedSize, possiblePositions1.size)
        assertEquals(Pair(0,0), possiblePositions1[0])
        assertEquals(Pair(0,8), possiblePositions1[8])

    }

    /**
     * Test for highScore method
     */
    @Test
    fun testHighScore(){
        // p1 tokens = 4
        // p2 tokens = 2
        val p3 = Player("Ann", PlayerColour.BLACK, PlayerType.HUMAN,0)
        moonwheel.tiles[2] = Tile(Type.MEEPLE, Cost.ZERO, mutableListOf())


        p3.grid[2][4] = tile1
        val players = mutableListOf(p1,p2,p3)

        //case1 chosenUndo = false ,p3.tokens=0
        val state1 = State(tilesList1,moonwheel,p3,players,"")
        val testGame1 = Game(state1, false, "", false, 0, mutableListOf(state1))

        val rootService =  RootService(novaluna = Novaluna(highscores,testGame1))
        val aiService = AIService(rootService)

        val highscore = aiService.highScore(testGame1)
        val expectedHighScore = 3.0
        val expectedWinner = p3.name
        assertEquals(expectedHighScore,highscore.second)
        assertEquals(expectedWinner,highscore.first)

        //case2 chosenUndo = true
        val state2 = State(tilesList1,moonwheel,p1,players,"")
        val testGame2 = Game(state2, false, "", true, 0, mutableListOf(state1))
        val highscore2 = aiService.highScore(testGame2)
        assertEquals(0.0,highscore2.second)
        assertEquals("",highscore2.first)

        // case3 Moonwheel is empty and both players have tokens
        val players1 = mutableListOf(p1, p2)

        val state3 = State(tilesList2, moonwheel, p2,players1,""  )
        val testGame3 = Game(state3, false, "", false, 0, mutableListOf(state1, state2))
        val highScore3 = aiService.highScore(testGame3)
        assertEquals(4.0, highScore3.second)
        assertEquals(p2.name, highScore3.first)
        //case4 MoonWheel is empty and chosenUndo = true
        val testGame4 = Game(state3, false, "", true, 0, mutableListOf(state1, state2))
        val highScore4 = aiService.highScore(testGame4)
        assertEquals(0.0, highScore4.second)
        assertEquals("", highScore4.first)

    }


    /**
     * Test for method aiPlayer that checks if a given player is an AI player or a human
     */
    @Test
    fun testAIPlayer(){
        val p4 = Player("Lisa", PlayerColour.BLUE, PlayerType.HUMAN, 25)
        val aiEasy = Player("NPC1", PlayerColour.BLACK, PlayerType.EASY, 4)
        val aiMidium = Player("NPC2", PlayerColour.BLACK, PlayerType.NORMAL, 3)
        val aiHard = Player("NPC3", PlayerColour.BLACK, PlayerType.HARD, 2)

        val players = mutableListOf(p4,aiEasy, aiMidium, aiHard)
        val state3 = State(tilesList1, moonwheel, p1, players,"")
        val testGame1 = Game(state3, false, "", false, 0, mutableListOf(state1))

        val rootService =  RootService(novaluna = Novaluna(highscores,testGame1))
        val aiService = AIService(rootService)


        assertFalse { aiService.aiplayer(p4) }
        assertTrue { aiService.aiplayer(aiEasy) }
        assertTrue { aiService.aiplayer(aiMidium) }
        assertTrue { aiService.aiplayer(aiHard) }
        assertFalse { aiService.aiplayer(p1) }
    }
    /**
     * Check if method evaluateNextTurn works correctly and generates the right score for
     * a given player and given state of a game
     * case 1 : aiEasy is NPC and aiEasy Tokens > p2 tokens > p1 tokens
     * case 2 : aiEasy is NPC and aiEasy Tokens < p2 tokens < p1 tokens
     * case 3 : aiEasy is NPC and aiEasy Tokens < p1 tokens and aiEasy Tokens > p1 tokens
     * for human players hold the same conditions
     */
    @Test
    fun testEvaluateNextTurnNegative(){
        // p1 tokens 4
        // p2 tokens 2
        // aiEasy tokens 5
        val aiEasy = Player("NPC1", PlayerColour.BLACK, PlayerType.EASY, 5)
        val players = mutableListOf(p1,p2,aiEasy)
        val state3 = State(tilesList1, moonwheel, p1, players,"")
        val testGame1 = Game(state3, false, "", false, 0, mutableListOf(state2))

        val rootService =  RootService(novaluna = Novaluna(highscores,testGame1))
        val aiService = AIService(rootService)

        val evaluatedTurnAI = aiService.evaluateNextTurn(state3, aiEasy)
        val expectedEvaluationAI = Pair(Pair(Tile(),Pair(0,0)),-100)
        // p2 tokens < p1 token < aiEasy tokens; min = 2; max = 0
        val evaluatedTurnPlayer2 = aiService.evaluateNextTurn(state3, p2)
        val expectedEvaluationPlayer2 = Pair(Pair(Tile(),Pair(0,0)), -100)

        assertEquals(expectedEvaluationAI, evaluatedTurnAI)
        assertEquals(expectedEvaluationPlayer2, evaluatedTurnPlayer2)

    }
    /**
     * Test evaluateNextTurn case 2
     */
    @Test
    fun testEvaluateNextTurnPositive(){
        // p1 tokens 4
        // p2 tokens 2
        // aiEasy tokens 1
        val aiEasy = Player("NPC1", PlayerColour.BLACK, PlayerType.EASY, 1)
        val players = mutableListOf(p1,p2,aiEasy)
        val state3 = State(tilesList1, moonwheel, p1, players,"")

        val testGame1 = Game(state3, false, "", false, 0, mutableListOf(state2))

        val rootService =  RootService(novaluna = Novaluna(highscores,testGame1))
        val aiService = AIService(rootService)

        val evaluatedTurnAI = aiService.evaluateNextTurn(state3, aiEasy)
        val expectedEvaluationAI = Pair(Pair(Tile(),Pair(0,0)),100)
        // p2 tokens < p1 tokens and p2 tokens > ai tokens ; min = 1; max = 1
        val evaluatedTurnPlayer2 = aiService.evaluateNextTurn(state3, p2)
        val expectedEvaluationPlayer2 = Pair(Pair(Tile(),Pair(0,0)), 0)
        assertEquals(expectedEvaluationAI, evaluatedTurnAI)
        assertEquals(expectedEvaluationPlayer2, evaluatedTurnPlayer2)
    }
    /**
     * Test evaluateNextTurn case 3
     */
    @Test
    fun testEvaluateNextTurnZero(){
        // p1 tokens 4
        // p2 tokens 2
        // aiEasy tokens 3
        val aiEasy = Player("NPC1", PlayerColour.BLACK, PlayerType.EASY, 3)
        val players = mutableListOf(p1,p2,aiEasy)
        val state3 = State(tilesList1, moonwheel, p1, players,"")

        val testGame1 = Game(state3, false, "", false, 0, mutableListOf(state2))

        val rootService =  RootService(novaluna = Novaluna(highscores,testGame1))
        val aiService = AIService(rootService)

        val evaluatedTurnAI = aiService.evaluateNextTurn(state3, aiEasy)
        val expectedEvaluationAI = Pair(Pair(Tile(),Pair(0,0)), 0)
        // min = 0; max = 2
        val evaluatedTurnPlayer2 = aiService.evaluateNextTurn(state3, p2)
        val expectedEvaluationPlayer2 = Pair(Pair(Tile(),Pair(0,0)), -100)
        // p2 tokens < ai tokens < p1 tokens ;min = 0; max = 2
        assertEquals(expectedEvaluationAI, evaluatedTurnAI)
        assertEquals(expectedEvaluationPlayer2, evaluatedTurnPlayer2)
    }

    /**
     * Test method for makeAISimulation if it completes task
     */
    @Test
    fun testAISimulationTaskCompleted(){
        val highscores = mutableListOf<Score>()
        val novaluna = Novaluna(highscores,null)
        val rootService = RootService(novaluna)
        val gameService = GameService(rootService)
        //val playerActionService = PlayerActionService(rootService)
        val aiService = AIService(rootService)

        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)


        val aiPlayer1Triple = Triple("Player1", PlayerColour.BLUE, PlayerType.EASY)
        val humanPlayer2Triple = Triple("Player2", PlayerColour.BLACK, PlayerType.HUMAN)
        val humanPlayer3Triple = Triple("Player3", PlayerColour.ORANGE, PlayerType.HUMAN)

        val playerTriples = listOf(aiPlayer1Triple,humanPlayer2Triple,humanPlayer3Triple)

        rootService.gameService.startNewAITournamentGame(
            playerTriples,
            File("src/main/resources/default_CSV_SaveFile/nl_tiles.csv")
        )

        val currentGame = rootService.novaluna.game
        assertNotNull(currentGame)

        currentGame.currentState.players[0].tokens = 1
        currentGame.currentState.players[1].tokens = 5
        currentGame.currentState.players[2].tokens = 8

        currentGame.currentState.moonwheel.tiles[1] = tile11
        currentGame.currentState.moonwheel.tiles[2] = tile2
        currentGame.currentState.moonwheel.tiles[3] = tile4

        currentGame.currentState.players[0].placeTileOnGrid(tile22,Pair(0,0))
        val newState = rootService.aiService.makeAISimulation(currentGame.currentState.moonwheel.tiles[1],
            currentGame.currentState,Pair(1,0))


        println(currentGame.currentState.currentPlayer.name)
        println(newState.currentPlayer.name)

        println(newState.players[0].tokens)
        assertEquals(0,newState.players[0].tokens)

       println(newState.players[0].grid[0][0])


    }

    /**
     * Test for minimax Algorithm
     */
    @Test
    fun testMinimax2(){

        val highscores = mutableListOf<Score>()
        val novaluna = Novaluna(highscores,null)
        val rootService = RootService(novaluna)
        val gameService = GameService(rootService)
        val aiService = AIService(rootService)

        val aiPlayer1Triple = Triple("Player1", PlayerColour.BLUE, PlayerType.EASY)
        val humanPlayer2Triple = Triple("Player2", PlayerColour.BLACK, PlayerType.HUMAN)
        val humanPlayer3Triple = Triple("Player3", PlayerColour.ORANGE, PlayerType.HUMAN)

        val playerTriples = listOf(aiPlayer1Triple,humanPlayer2Triple,humanPlayer3Triple)

        gameService.startNewAITournamentGame(
            playerTriples,
            File("src/main/resources/default_CSV_SaveFile/nl_tiles.csv")
        )

        val currentGame = rootService.novaluna.game
        assertNotNull(currentGame)

        val tile1 = currentGame.currentState.moonwheel.tiles[1]

        val newState = aiService.makeAISimulation(currentGame.currentState.moonwheel.tiles[1],
            currentGame.currentState,Pair(3,3))

        // Check that state is altered
        assertNotEquals(newState,currentGame.currentState)
        // Check that new state is altered
        assertEquals(newState.players[0].grid[3][3],tile1)
        assertEquals(newState.moonwheel.tiles[1],Tile(Type.MEEPLE,Cost.ZERO, mutableListOf(),69))
        assertEquals(newState.moonwheel.playerPositions[1][0].name,newState.players[0].name)


        // Check that tile is placed
        assertEquals(currentGame.currentState.moonwheel.tiles[1],tile1)
        assertEquals(currentGame.currentState.moonwheel.playerPositions[0][0],
            currentGame.currentState.currentPlayer)
    }

    /**
     * Test for minimax
     */
    @Test
    fun testMinimax3(){
        val highscores = mutableListOf<Score>()
        val novaluna = Novaluna(highscores,null)
        val rootService = RootService(novaluna)
        val gameService = GameService(rootService)
        val aiService = AIService(rootService)

        val aiPlayer1Triple = Triple("Player1", PlayerColour.BLUE, PlayerType.EASY)
        val humanPlayer2Triple = Triple("Player2", PlayerColour.BLACK, PlayerType.HUMAN)
        val humanPlayer3Triple = Triple("Player3", PlayerColour.ORANGE, PlayerType.HUMAN)

        val playerTriples = listOf(aiPlayer1Triple,humanPlayer2Triple,humanPlayer3Triple)

        gameService.startNewAITournamentGame(
            playerTriples,
            File("src/main/resources/default_CSV_SaveFile/nl_tiles.csv")
        )

        /*gameService.startNewAISimulationGame(
            playerTriples,
            0
        )*/

        val currentGame = rootService.novaluna.game
        assertNotNull(currentGame)

        currentGame.currentState.players[0].tokens = 1
        currentGame.currentState.players[1].tokens = 5
        currentGame.currentState.players[2].tokens = 8

        currentGame.currentState.players[0].placeTileOnGrid(tile22,Pair(0,0))

        currentGame.currentState.moonwheel.tiles[1] = tile4
        currentGame.currentState.moonwheel.tiles[2] = tile11
        currentGame.currentState.moonwheel.tiles[3] = tile5
        val bestMove: Pair<Pair<Tile, Pair<Int,Int>>,Int>  =
            aiService.minimax(currentGame.currentState,2,-999999999,999999999,true)

        println(bestMove)
        assertEquals(currentGame.currentState.currentPlayer.name,playerTriples[0].first)
    }

    /**
     * Test for AIMove that performsmove of NPC
     */
    @Test
    fun testAIMove1(){
        val highscores = mutableListOf<Score>()
        val novaluna = Novaluna(highscores,null)
        val rootService = RootService(novaluna)
        val gameService = GameService(rootService)
        val aiService = AIService(rootService)

        val aiPlayer1Triple = Triple("Player1", PlayerColour.BLUE, PlayerType.EASY)
        val humanPlayer2Triple = Triple("Player2", PlayerColour.BLACK, PlayerType.HUMAN)
        val humanPlayer3Triple = Triple("Player3", PlayerColour.ORANGE, PlayerType.HUMAN)

        val playerTriples = listOf(aiPlayer1Triple,humanPlayer2Triple,humanPlayer3Triple)

        gameService.startNewAITournamentGame(
            playerTriples,
            File("src/main/resources/default_CSV_SaveFile/nl_tiles.csv")
        )

        /*gameService.startNewAISimulationGame(
            playerTriples,
            0
        )*/

        val currentGame = rootService.novaluna.game
        assertNotNull(currentGame)

         val humanMove = aiService.aiMove(currentGame.currentState,PlayerType.HUMAN)

        assertEquals( humanMove.first,Tile())
        assertEquals( humanMove.second,Pair(0,0))
    }

    /**
     * Tests if getTip works correctly and give advice from [PlayerType.HARD]
     */

    @Test
    fun testGetTip(){
        val highscores = mutableListOf<Score>()
        val novaluna = Novaluna(highscores,null)
        val rootService = RootService(novaluna)
        val gameService = GameService(rootService)
        val aiService = AIService(rootService)

        val testRefreshable = RefreshableTest()
        rootService.addRefreshable(testRefreshable)


        val aiPlayer1Triple = Triple("Player1", PlayerColour.BLUE, PlayerType.EASY)
        val humanPlayer2Triple = Triple("Player2", PlayerColour.BLACK, PlayerType.HUMAN)
        val humanPlayer3Triple = Triple("Player3", PlayerColour.ORANGE, PlayerType.HUMAN)

        val playerTriples = listOf(aiPlayer1Triple,humanPlayer2Triple,humanPlayer3Triple)

        gameService.startNewAITournamentGame(
            playerTriples,
            File("src/main/resources/default_CSV_SaveFile/nl_tiles.csv")
        )


        val currentGame = rootService.novaluna.game
        assertNotNull(currentGame)

        val tip = rootService.aiService.getAITip(currentGame.currentState)
        println(tip)
    }
}