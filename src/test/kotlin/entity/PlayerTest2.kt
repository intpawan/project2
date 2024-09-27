package entity

import kotlin.test.*

/**
 * Second test class for [Player] to check grid-related methods.
 */
class PlayerTest2 {
    /**
     * Checks whether is position works correctly.
     */
    @Test
    fun isPositionOnlyCase(){
        assertTrue { Player.isPosition(Pair(0,0)) }
        assertTrue { Player.isPosition(Pair(8,8)) }
        assertTrue { Player.isPosition(Pair(0,8)) }
        assertTrue { Player.isPosition(Pair(8,0)) }
        assertTrue { Player.isPosition(Pair(2,4)) }
        assertTrue { Player.isPosition(Pair(5,1)) }

        assertFalse { Player.isPosition(Pair(-1,0)) }
        assertFalse { Player.isPosition(Pair(2,-1)) }
        assertFalse { Player.isPosition(Pair(10,0)) }
        assertFalse { Player.isPosition(Pair(10,10)) }
        assertFalse { Player.isPosition(Pair(-1,-3)) }
        assertFalse { Player.isPosition(Pair(0,20)) }
    }

    /**
     * Checks whether areNeighbours works correctly for grid position.
     */
    @Test
    fun areNeighboursCaseOne(){
        assertTrue { Player.areNeighbours(Pair(0,0),Pair(0,1)) }
        assertTrue { Player.areNeighbours(Pair(5,7),Pair(4,7)) }
        assertTrue { Player.areNeighbours(Pair(8,8),Pair(7,8)) }

        assertFalse { Player.areNeighbours(Pair(3,5),Pair(2,4)) }
        assertFalse { Player.areNeighbours(Pair(2,8),Pair(2,8)) }
        assertFalse { Player.areNeighbours(Pair(0,0),Pair(0,2)) }
        assertFalse { Player.areNeighbours(Pair(8,4),Pair(6,4)) }
    }

    /**
     * Checks whether areNeighbours fails if positions are not in range.
     */
    @Test
    fun areNeighboursCaseTwo(){
        assertFails { Player.areNeighbours(Pair(-1,5), Pair(7,8)) }
        assertFails { Player.areNeighbours(Pair(9,5), Pair(7,8)) }
        assertFails { Player.areNeighbours(Pair(1,-4), Pair(7,8)) }
        assertFails { Player.areNeighbours(Pair(4,12), Pair(7,8)) }

        assertFails { Player.areNeighbours(Pair(1,5), Pair(-2,8)) }
        assertFails { Player.areNeighbours(Pair(3,5), Pair(10,8)) }
        assertFails { Player.areNeighbours(Pair(1,4), Pair(3,-5)) }
        assertFails { Player.areNeighbours(Pair(4,2), Pair(7,663)) }

        assertFails { Player.areNeighbours(Pair(11,43), Pair(3,2)) }
        assertFails { Player.areNeighbours(Pair(1,3), Pair(23,26)) }
        assertFails { Player.areNeighbours(Pair(11,43), Pair(53,32)) }
    }

    /**
     * Test if getNeighbours works correctly.
     */
    @Test
    fun getNeighbours(){
        // Corners
        val pos1 = Pair(0,0)
        val neighbours1 = setOf(Pair(0,1),Pair(1,0))
        val pos2 = Pair(0,8)
        val neighbours2 = setOf(Pair(1,8),Pair(0,7))
        val pos3 = Pair(8,0)
        val neighbours3 = setOf(Pair(7,0),Pair(8,1))
        val pos4 = Pair(8,8)
        val neighbours4 = setOf(Pair(7,8),Pair(8,7))

        // Some Rim Positions
        val pos5 = Pair(0,4)
        val neighbours5 = setOf(Pair(0,3),Pair(0,5),Pair(1,4))
        val pos6 = Pair(2,0)
        val neighbours6 = setOf(Pair(1,0),Pair(3,0),Pair(2,1))
        val pos7 = Pair(3,8)
        val neighbours7 = setOf(Pair(3,7),Pair(2,8),Pair(4,8))
        val pos8 = Pair(8,6)
        val neighbours8 = setOf(Pair(8,7),Pair(8,5),Pair(7,6))

        // Inner position
        val pos9 = Pair(4,5)
        val neighbours9 = setOf(Pair(4,4),Pair(4,6),Pair(3,5),Pair(5,5))

        // Check the positions
        assertEquals(Player.getNeighbours(pos1),neighbours1)
        assertEquals(Player.getNeighbours(pos2),neighbours2)
        assertEquals(Player.getNeighbours(pos3),neighbours3)
        assertEquals(Player.getNeighbours(pos4),neighbours4)
        assertEquals(Player.getNeighbours(pos5),neighbours5)
        assertEquals(Player.getNeighbours(pos6),neighbours6)
        assertEquals(Player.getNeighbours(pos7),neighbours7)
        assertEquals(Player.getNeighbours(pos8),neighbours8)
        assertEquals(Player.getNeighbours(pos9),neighbours9)
    }

    /**
     * Test if getNeighbour fails if pos is not a position.
     */
    @Test
    fun getNeighboursCaseTwo(){
        assertFails { Player.getNeighbours(Pair(-1,0)) }
        assertFails { Player.getNeighbours(Pair(2,110)) }
    }

    /**
     * Tests if isEligibleGridPosition works correctly if a correct position is passed.
     */
    @Test
    fun isEligibleGridPositionCaseOne(){
        val player1 = Player("name1",PlayerColour.BLACK,PlayerType.EASY,17)

        // At the beginning, all positions are eligible.
        player1.grid.forEachIndexed { x,array ->
            array.forEachIndexed { y, _ ->
                assertTrue { player1.isEligibleGridPosition(Pair(x,y)) }
            }
        }

        // Create some tiles
        val tile1 = Tile(Type.CYAN,Cost.FIVE, mutableListOf())
        val pos1 = Pair(3,5)
        val tile2 = Tile(Type.YELLOW,Cost.FIVE, mutableListOf())
        val pos2 = Pair(4,5)
        //val tile3 = Tile(Type.BLUE,Cost.FIVE, mutableListOf())
        //val tile4 = Tile(Type.RED,Cost.FIVE, mutableListOf())

        //Place first tile and test
        player1.grid[pos1.first][pos1.second] = tile1
        player1.grid.forEachIndexed { x,array ->
            array.forEachIndexed { y, _ ->
                val compPair = Pair(x,y)
                if( Player.areNeighbours(compPair,pos1)){
                    assertTrue { player1.isEligibleGridPosition(compPair) }
                }else{
                    assertFalse { player1.isEligibleGridPosition(compPair) }
                }
            }
        }

        // Place second tile and test
        player1.grid[pos2.first][pos2.second] = tile2
        player1.grid.forEachIndexed { x,array ->
            array.forEachIndexed { y, _ ->
                val compPair = Pair(x,y)
                val neighbours = Player.areNeighbours(compPair,pos1) || Player.areNeighbours(compPair,pos2)
                val pos =compPair != pos1 && compPair != pos2
                if( neighbours && pos){
                    assertTrue { player1.isEligibleGridPosition(compPair) }
                }else{
                    assertFalse { player1.isEligibleGridPosition(compPair) }
                }
            }
        }
    }

    /**
     * Tests if isEligibleGridPosition fails if pos is not a position.
     */
    @Test
    fun isEligibleGridPositionCaseTwo(){
        val player1 = Player("name1",PlayerColour.BLACK,PlayerType.EASY,17)

        assertFails { player1.isEligibleGridPosition(Pair(-1,0)) }
        assertFails { player1.isEligibleGridPosition(Pair(2,110)) }
    }

    /**
     * Checks if placeTileOnGrid works correctly if the input is correct.
     * This means that clusters are merged or newly related dependent on what clusters already exist and
     * what tile is added at which position.
     */
    @Test
    fun placeTileOnGridCaseOne(){
        val player1 = Player("name1",PlayerColour.BLACK,PlayerType.EASY,17)
        var compClusters = mutableListOf<Pair<Type,Set<Pair<Int,Int>>>>()
        val tile1 = Tile(Type.CYAN,Cost.FIVE, mutableListOf())
        val pos1 = Pair(3,5)
        val tile2 = Tile(Type.CYAN,Cost.THREE, mutableListOf())
        val pos2 = Pair(4,5)
        val tile3 = Tile(Type.CYAN,Cost.FIVE, mutableListOf())
        val pos3 = Pair(5,5)
        val tile4 = Tile(Type.YELLOW,Cost.ONE, mutableListOf())
        val pos4 = Pair(6,5)
        val tile5 = Tile(Type.CYAN,Cost.FOUR, mutableListOf())
        val pos5 = Pair(6,6)
        val tile6 = Tile(Type.CYAN,Cost.ONE, mutableListOf())
        val pos6 = Pair(5,6)

        // First tile
        player1.placeTileOnGrid(tile1,pos1)
        // Update comparison clusters
        compClusters.add(
            Pair(tile1.type, mutableSetOf(pos1))
        )
        // Check grid
        player1.grid.forEachIndexed { x, array ->
            array.forEachIndexed { y, tile ->
                if(Pair(x,y) == pos1){ assertEquals(tile,tile1) }else{ assertEquals(tile,Tile()) }
            }
        }
        // Compare clusters
        assertEquals(player1.clusters,compClusters.toSet())

        // Second tile which should be put in the same cluster
        player1.placeTileOnGrid(tile2,pos2)
        // Update comparison clusters
        var tempMutableSet = compClusters[0].second.toMutableSet()
        tempMutableSet.add(pos2)
        compClusters[0] = Pair(compClusters[0].first,tempMutableSet)
        // Check grid
        player1.grid.forEachIndexed { x, array ->
            array.forEachIndexed { y, tile ->
                if(Pair(x,y) == pos1){ assertEquals(tile,tile1) }else if (Pair(x,y) == pos2){ assertEquals(tile,tile2)
                }else{ assertEquals(tile,Tile()) }
            }
        }
        // Compare clusters
        assertEquals(player1.clusters,compClusters.toSet())

        // Third tile which should be put in the same cluster
        player1.placeTileOnGrid(tile3,pos3)
        // Update comparison clusters
        tempMutableSet = compClusters[0].second.toMutableSet()
        tempMutableSet.add(pos3)
        compClusters[0] = Pair(compClusters[0].first,tempMutableSet)
        // Check grid
        assertEquals(player1.grid[pos3.first][pos3.second],tile3)
        // Compare clusters
        assertEquals(player1.clusters,compClusters.toSet())

        // Fourth tile which should be put in a different cluster
        player1.placeTileOnGrid(tile4,pos4)
        // Update comparison clusters
        compClusters.add(Pair(tile4.type,setOf(pos4)))
        // Check grid
        assertEquals(player1.grid[pos4.first][pos4.second],tile4)
        // Compare clusters
        assertEquals(player1.clusters,compClusters.toSet())

        // Fifth tile which should be put in a different cluster
        player1.placeTileOnGrid(tile5,pos5)
        // Update comparison clusters
        compClusters.add(Pair(tile5.type,setOf(pos5)))
        // Check grid
        assertEquals(player1.grid[pos5.first][pos5.second],tile5)
        // Compare clusters
        assertEquals(player1.clusters,compClusters.toSet())

        //Sixth tile, after which the two cyan-clusters should be merged to one
        player1.placeTileOnGrid(tile6,pos6)
        // Update comparison clusters
        compClusters = mutableListOf(Pair(Type.CYAN,setOf(pos1,pos2,pos3,pos5,pos6)), Pair(Type.YELLOW, setOf(pos4)))
        // Check grid
        assertEquals(player1.grid[pos6.first][pos6.second],tile6)
        // Compare clusters
        assertEquals(player1.clusters,compClusters.toSet())
    }

    /**
     * Checks if placeTileOnGrid fails if the input is not correct.
     */
    @Test
    fun placeTileOnGridCaseTwo(){
        val player1 = Player("name1",PlayerColour.BLACK,PlayerType.EASY,17)

        val tile1 = Tile(Type.CYAN,Cost.FIVE, mutableListOf())
        val pos1 = Pair(3,5)
        val tile2 = Tile(Type.RED,Cost.THREE, mutableListOf())
        val pos2 = Pair(4,5)
        val tile3 = Tile(Type.CYAN,Cost.FIVE, mutableListOf())
        val pos3 = Pair(5,5)
        val tile4 = Tile(Type.YELLOW,Cost.ONE, mutableListOf())
        val pos4 = Pair(6,5)
        val tile5 = Tile(Type.CYAN,Cost.FOUR, mutableListOf())
        val pos5 = Pair(6,6)
        val tile6 = Tile(Type.BLUE,Cost.ONE, mutableListOf())
        val pos6 = Pair(5,6)
        val tile7 = Tile(Type.RED,Cost.ONE, mutableListOf())
        val pos7 = Pair(4,4)
        val tile8 = Tile(Type.RED,Cost.TWO, mutableListOf())
        val pos8 = Pair(4,6)

        // Fails if wrong type
        assertFails { player1.placeTileOnGrid(Tile(), Pair(5,4))}
        assertFails { player1.placeTileOnGrid(Tile(Type.MEEPLE,Cost.ZERO, mutableListOf()), Pair(2,1)) }

        // Fails if not a position
        assertFails { player1.placeTileOnGrid(tile1,Pair(9,0)) }
        assertFails { player1.placeTileOnGrid(tile1,Pair(0,-4)) }

        // Insert a few tiles
        player1.placeTileOnGrid(tile1,pos1)
        player1.placeTileOnGrid(tile2,pos2)
        player1.placeTileOnGrid(tile3,pos3)
        player1.placeTileOnGrid(tile4,pos4)
        player1.placeTileOnGrid(tile5,pos5)
        player1.placeTileOnGrid(tile6,pos6)
        player1.placeTileOnGrid(tile7,pos7)
        player1.placeTileOnGrid(tile8,pos8)
        assertEquals(
            player1.clusters,
            setOf(
                Pair(Type.CYAN, setOf(pos1)),
                Pair(Type.RED, setOf(pos2,pos7,pos8)),
                Pair(Type.CYAN, setOf(pos3)),
                Pair(Type.YELLOW, setOf(pos4)),
                Pair(Type.CYAN, setOf(pos5)),
                Pair(Type.BLUE, setOf(pos6))
            )
        )

        // Fails if position is not empty
        val tile9 = Tile(Type.YELLOW,Cost.SEVEN, mutableListOf())
        assertFails { player1.placeTileOnGrid(tile9,pos4) }
        assertFails { player1.placeTileOnGrid(tile9,pos8) }

        // Fails if not neighbouring
        assertFails { player1.placeTileOnGrid(tile9, Pair(3,7)) }
        assertFails { player1.placeTileOnGrid(tile9, Pair(8,5)) }
        assertFails { player1.placeTileOnGrid(tile9, Pair(0,0)) }
    }

    /**
     * Checks if placeTileOnGrid works correctly at the rim of the grid.
     */
    @Test
    fun placeTileOnGridCaseThree(){
        val player1 = Player("name1",PlayerColour.BLACK,PlayerType.EASY,17)

        val tile1 = Tile(Type.CYAN,Cost.FIVE, mutableListOf())
        val pos1 = Pair(0,1)
        val tile2 = Tile(Type.RED,Cost.THREE, mutableListOf())
        val pos2 = Pair(0,0)
        val tile3 = Tile(Type.RED,Cost.FIVE, mutableListOf())
        val pos3 = Pair(1,1)
        val tile4 = Tile(Type.YELLOW,Cost.ONE, mutableListOf())
        val pos4 = Pair(1,2)
        val tile5 = Tile(Type.YELLOW,Cost.FOUR, mutableListOf())
        val pos5 = Pair(2,2)
        val tile6 = Tile(Type.RED,Cost.ONE, mutableListOf())
        val pos6 = Pair(1,0)
        val tile7 = Tile(Type.CYAN,Cost.ONE, mutableListOf())
        val pos7 = Pair(0,2)
        val tile8 = Tile(Type.BLUE,Cost.TWO, mutableListOf())
        val pos8 = Pair(0,3)
        val tile9 = Tile(Type.CYAN,Cost.THREE, mutableListOf())
        val pos9 = Pair(2,0)

        // Insert a few tiles
        player1.placeTileOnGrid(tile1,pos1)
        player1.placeTileOnGrid(tile2,pos2)
        player1.placeTileOnGrid(tile3,pos3)
        player1.placeTileOnGrid(tile4,pos4)
        player1.placeTileOnGrid(tile5,pos5)
        player1.placeTileOnGrid(tile6,pos6)
        player1.placeTileOnGrid(tile7,pos7)
        player1.placeTileOnGrid(tile8,pos8)
        player1.placeTileOnGrid(tile9,pos9)
        assertEquals(
            player1.clusters,
            setOf(
                Pair(Type.CYAN, setOf(pos1,pos7)),
                Pair(Type.RED, setOf(pos2,pos3,pos6)),
                Pair(Type.CYAN, setOf(pos9)),
                Pair(Type.YELLOW, setOf(pos4,pos5)),
                Pair(Type.BLUE, setOf(pos8))
            )
        )
    }
}