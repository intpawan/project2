package entity

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Test class for initialisation methods of class [Novaluna]
 */


class NovaLunaTest {
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

    val score1 = Score(14.5, "Anny")
    val score2 = Score(3.0, "Will")
    val score3 = Score(5.0, "John")
    val highScoreTest1 = mutableListOf<Score>(score1, score2)
    val highScoreTest2 = mutableListOf<Score>(score1, score2, score3)

    val novaLunaTest1 = Novaluna(highScoreTest1, testGame1)
    val novaLunaTest2 = Novaluna(highScoreTest2, null)

    /**
     * Test if getting and setting value of "highscores" works
     */
    @Test
    fun testGetAndSetHighScores(){
        assertEquals(highScoreTest1, novaLunaTest1.highscores)
        assertEquals(highScoreTest2, novaLunaTest2.highscores)
        highScoreTest1.add(score3)
        novaLunaTest1.highscores = highScoreTest1
        assertEquals(highScoreTest1, novaLunaTest1.highscores)

    }

    /**
     * Test if getting and setting value of "game" works
     */
    @Test
    fun testGetAndSetGame(){
        assertNotNull(novaLunaTest1.game)
        assertEquals(testGame1, novaLunaTest1.game)
        assertNull(novaLunaTest2.game)

        val testGame2 = Game(state2, true, "", true, 5, mutableListOf(state1,state2))
        novaLunaTest2.game = testGame2

        assertNotNull(novaLunaTest2)

    }
}