package entity

import kotlin.test.*

/**
 * Test class for [Player]
 */
class PlayerTest {
    /**
     * Test if getting the value of "name" works
     */
    @Test
    fun testGetName(){
        val player1 = Player("name1",PlayerColour.BLACK,PlayerType.EASY,17)
        assertEquals(player1.name,"name1")
    }

    /**
     * Test if getting the value of "playerColour" works
     */
    @Test
    fun testGetPlayerColour(){
        val player1 = Player("name1",PlayerColour.BLACK,PlayerType.EASY,17)
        assertEquals(player1.playerColour,PlayerColour.BLACK)
    }

    /**
     * Test if getting the value of "playerType" works
     */
    @Test
    fun testGetPlayerType(){
        val player1 = Player("name1",PlayerColour.BLACK,PlayerType.EASY,17)
        assertEquals(player1.playerType,PlayerType.EASY)
    }

    /**
     * Test if getting the value of "tokens" works
     */
    @Test
    fun testGetTokens(){
        val player1 = Player("name1",PlayerColour.BLACK,PlayerType.EASY,17)
        assertEquals(player1.tokens,17)
    }

    /**
     * Test if setting the value of "tokens" works
     */
    @Test
    fun testSetTokens(){
        val player1 = Player("name1",PlayerColour.BLACK,PlayerType.EASY,17)
        player1.tokens = 16
        assertEquals(player1.tokens,16)
    }

    /**
     * Test if getting the value of "choseTip" works
     */
    @Test
    fun testGetChoseTip(){
        val player1 = Player("name1",PlayerColour.BLACK,PlayerType.EASY,17)
        assertFalse(player1.choseTip)
    }

    /**
     * Test if setting the value of "choseTip" works
     */
    @Test
    fun testSetChoseTip(){
        val player1 = Player("name1",PlayerColour.BLACK,PlayerType.EASY,17)
        player1.choseTip = true
        assertTrue(player1.choseTip)
    }

    /**
     * Test if getting the values of "grid" works
     */
    @Test
    fun testGetGrid(){
        val player1 = Player("name1",PlayerColour.BLACK,PlayerType.EASY,17)
        player1.grid.forEach { array ->
            array.forEach { tile ->
                assertEquals(tile,Tile())
            }
        }
    }

    /**
     * Test if getting the clusters works.
     */
    @Test
    fun testGetClusters(){
        val player1 = Player("name1",PlayerColour.BLACK,PlayerType.EASY,17)
        val clusters = player1.clusters
        assertEquals(player1.clusters,clusters)
        assertSame(player1.clusters,clusters)
    }

    /**
     * Test if cloning works
     */
    @Test
    fun testClone(){
        val player1 = Player("name1",PlayerColour.BLACK,PlayerType.EASY,17)
        val clonedPlayer1 = player1.clone()

        assertNotSame(player1,clonedPlayer1)
        assertEquals(player1,clonedPlayer1)

        assertEquals(player1.name,clonedPlayer1.name)
        assertNotSame(player1.name, clonedPlayer1.name)

        assertEquals(player1.playerColour,clonedPlayer1.playerColour)

        assertEquals(player1.playerType,clonedPlayer1.playerType)

        assertEquals(player1.tokens,clonedPlayer1.tokens)

        assertEquals(player1.choseTip, clonedPlayer1.choseTip)

        assertNotSame(player1.grid,clonedPlayer1.grid)
        player1.grid.forEachIndexed { x, array ->
            assertNotSame(array, clonedPlayer1.grid[x])
            array.forEachIndexed { y, tile ->
                assertNotSame(tile,clonedPlayer1.grid[x][y])
            }
        }

        assertNotSame(player1.clusters,clonedPlayer1.clusters)
        assertEquals(player1.clusters,clonedPlayer1.clusters)
        player1.clusters.forEach { player1Pair->
            // Every cluster is contained in clonedPlayer1.
            // This is basically checked again in the next assert with the first two comparisons.
            assertTrue { clonedPlayer1.clusters.contains(player1Pair) }
            // Every cluster has a copy deep copy, so they are equal but the elements are not the same.
            assertTrue {
                clonedPlayer1.clusters.any { clonedPlayer1Pair->
                    player1Pair.first == clonedPlayer1Pair.first &&
                            player1Pair.second == clonedPlayer1Pair.second &&
                            !(player1Pair.second === clonedPlayer1Pair.second)
                }
            }
        }
    }
}