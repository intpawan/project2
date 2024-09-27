package entity

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for the getter and setter methods of  the class [Score]
 */

class ScoreTest {

    val winnerNameForTest = "Paul"
    val avgScoreToTest = 23.5

    /**
     * Test if getting value of average Score and name of the winner works
     */
    @Test
    fun testGetScoreAndWinner(){
        // score to test
        val scoreToTest = Score(avgScoreToTest, winnerNameForTest)

        // expected values
        val expectedAvgScore = 23.5
        val expectedWinnerName = "Paul"

        assertEquals(expectedAvgScore, scoreToTest.avgScore)
        assertEquals(expectedWinnerName, scoreToTest.winner)
    }

    /**
     * Test if setting value of average Score and name of the winner works
     */
    fun testSetScoreAndWinner(){
        val scoreToTest = Score(avgScoreToTest, winnerNameForTest)
        scoreToTest.winner = "Max"
        scoreToTest.avgScore = 20.0
        assertEquals(20.0, scoreToTest.avgScore)
        assertEquals("Max", scoreToTest.winner)


    }
}