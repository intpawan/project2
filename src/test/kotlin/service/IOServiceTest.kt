package service
import entity.*
import java.io.File
import kotlin.test.*

/**
 * Test class for [IOService]
 */
class IOServiceTest {
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

    val state1 = State(tilesList1, moonwheel, p1, playersList,"")
    val state2 = State(tilesList2, moonwheel, p1, playersList,"")

    val testGame1 = Game(state1, false, "", false, 0, mutableListOf(state1,state1))
    val testGame2 = Game(state2, true, "", true, 5, mutableListOf(state1))



    val score1 = Score(14.5, "Anny")
    val score2 = Score(3.0, "Will")
    val score3 = Score(5.0, "John")
    val score4 =  Score(20.3,"Max")
    val score5 = Score(11.1,"Brian")
    val highScoreList = mutableListOf<Score>(score1,score2,score3,score4,score5)

    val novaluna = Novaluna(highScoreList, testGame1)
    val rootService = RootService(novaluna)



    /**
     * Test if parsing of the CSV-[File] works
     */
    @Test
    fun testParseCSV(){
        val ioServiceTest = IOService(rootService)
        val testFile = File("kotlin/service/testFiles/test.txt")
        ioServiceTest.CSV_FILE = testFile
        assertFalse{ testFile.length().equals(0)  }
        assertFails { ioServiceTest.parseCSV() }
        ioServiceTest.CSV_FILE = File("src/main/resources/default_CSV_SaveFile/nl_tiles.csv")
        val lineList = ioServiceTest.parseCSV()

        val line1 : MutableList<String> = lineList[0]
        val line2 : MutableList<String> = lineList[1]

        // Check Tile IDs
        assertEquals("01", line1[0])
        assertEquals("02", line2[0])

        // Check Tile Colours
        assertEquals("cyan", line1[1])
        assertEquals("cyan", line2[1])

        // Check Tile Cost
        assertEquals("1", line1[2])
        assertEquals("2", line2[2])

        // Check Tile task1
        assertEquals("", line1[3])
        assertEquals("cccc", line2[3])
    }

    /**
     * Some further tests for the format of the created list.
     */
    @Test
    fun parseCSVTestsRegardingFormat(){
        val ioServiceTest = IOService(rootService)
        val lineList = ioServiceTest.parseCSV()

        // Check that lineList has correct length
        assertEquals(lineList.size,68)

        // Check that each line from the csv file was converted to 6 elements.
        lineList.forEach {
            assertEquals(it.size,6)
        }

        // Check that the tasks of the first tile are indeed empty.
        for (i in 3..5){
            assertEquals(lineList[0][i],"")
        }
        // Check furthermore the 24th tile and the 68th (last) tile
        assertEquals(lineList[23][3],"cyrb")
        assertEquals(lineList[23][4],"rrr")
        assertEquals(lineList[23][5],"")
        assertEquals(lineList[67][3],"cr")
        assertEquals(lineList[67][4],"cb")
        assertEquals(lineList[67][5],"rb")
    }

    /**
     * Tests if csvToTiles works correctly if the CSV-File is a correct file.
     *
     * The call parseCSV in this method fails, if the format is not correct. Because this is tested separately, tests
     * regarding this are omitted here.
     */
    @Test
    fun csvToTilesOnlyCase(){
        val ioServiceTest = IOService(rootService)
        val stack = ioServiceTest.csvToTiles()

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
        val tile10 = Tile(
            Type.CYAN,
            Cost.FOUR,
            mutableListOf(
                Task(false, mutableListOf(Type.CYAN,Type.YELLOW)),
                Task(false, mutableListOf(Type.RED,Type.BLUE))
            ),
            10
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

        assertEquals(stack[0],tile1)
        assertEquals(stack[1],tile2)
        assertEquals(stack[9],tile10)
        assertEquals(stack[67],tile68)
    }

    /**
     * Test if converting string to [Cost] works
     */
    @Test
    fun testStringToCost(){
        val ioServiceTest = IOService(rootService)
        assertEquals(Cost.ZERO, ioServiceTest.stringToCost("0") )
        assertEquals(Cost.ONE, ioServiceTest.stringToCost("1") )
        assertEquals(Cost.TWO, ioServiceTest.stringToCost("2") )
        assertEquals(Cost.THREE, ioServiceTest.stringToCost("3") )
        assertEquals(Cost.FOUR, ioServiceTest.stringToCost("4") )
        assertEquals(Cost.FIVE, ioServiceTest.stringToCost("5") )
        assertEquals(Cost.SIX, ioServiceTest.stringToCost("6") )
        assertEquals(Cost.SEVEN, ioServiceTest.stringToCost("7") )
        assertFails { ioServiceTest.stringToCost("10") }
    }

    /**
     * Test if converting string as colour to [Type] works
     */
    @Test
    fun testStringToType(){
        val ioServiceTest = IOService(rootService)
        assertEquals(Type.BLUE, ioServiceTest.stringToType("blue") )
        assertEquals(Type.RED, ioServiceTest.stringToType("red") )
        assertEquals(Type.YELLOW, ioServiceTest.stringToType("yellow") )
        assertEquals(Type.CYAN, ioServiceTest.stringToType("cyan") )
        assertFails { ioServiceTest.stringToType("blabla") }
    }


    /**
     * Tests if saving the game and loading it after that results to the same game object
     */
    @Test
    fun testLoadAndSaveGame(){
        val ioServiceTest = IOService(rootService)

        ioServiceTest.SAVE_FILE = File("service/testFiles/test.txt")
        assertFails { ioServiceTest.loadGame() }
        assertFails { ioServiceTest.saveGame() }

        ioServiceTest.SAVE_FILE = File("")
        assertFails { ioServiceTest.loadGame() }
        assertFails { ioServiceTest.saveGame() }

        val testGameFile = File("src/test/kotlin/service/testFiles/testGame.json")
        testGameFile.writeText("")
        assertEquals(0,testGameFile.length())

        ioServiceTest.SAVE_FILE = testGameFile
        ioServiceTest.saveGame()
        assertFalse(testGameFile.length().equals(0))
        ioServiceTest.loadGame()
        val currentGame : Game? = ioServiceTest.rootService.novaluna.game
        assertNotNull(currentGame)

        assertEquals(currentGame.aiTournament, testGame1.aiTournament)
        assertEquals(currentGame.tournamentProtocol, testGame1.tournamentProtocol)
        assertEquals(currentGame.choseUndo, testGame1.choseUndo)
        assertEquals(currentGame.currentState, testGame1.currentState)
        assertEquals(currentGame.states.size, testGame1.states.size)
        for(i in currentGame.states.indices){
            val state1 = currentGame.states[i]
            val state2 = testGame1.states[i]
            assertEquals(state1,state2)
        }
    }


    /**
     * Tests if saving the highscore list and loading it after that results to the same highscore object
     */
    @Test
    fun testLoadAndSaveHighscore(){
        val ioServiceTest = IOService(rootService)

        ioServiceTest.HIGHSCORE_FILE = File("service/testFiles/test.txt")
        assertFails { ioServiceTest.loadHighscore() }
        assertFails { ioServiceTest.saveHighscore() }

        ioServiceTest.HIGHSCORE_FILE = File("")
        assertFails { ioServiceTest.loadHighscore() }
        assertFails { ioServiceTest.saveHighscore() }

        val testHighscoreFile = File("src/test/kotlin/service/testFiles/highscoreSaved.json")
        testHighscoreFile.writeText("")
        assertEquals(0, testHighscoreFile.length())

        ioServiceTest.HIGHSCORE_FILE = testHighscoreFile
        ioServiceTest.saveHighscore()
        assertFalse(testHighscoreFile.length().equals(0))
        ioServiceTest.loadHighscore()
        val currentHighscores = ioServiceTest.rootService.novaluna.highscores
        assertEquals(currentHighscores,highScoreList)
    }




}