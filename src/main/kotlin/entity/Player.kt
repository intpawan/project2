package entity

import kotlinx.serialization.Serializable
import kotlin.math.abs

/**
 * Class to represent a player
 *
 * It is characterized by the player's [name], [playerColour], [playerType] and the players [tokens].
 *
 * Each player also has a [grid], a 9x9-[Array] (or more specific: Array of Arrays) representing the place the player
 * places his tiles.
 * Finally, each player has a [Set] of [Type]-Pair<Int,Int>-[Pair]s called [clusters]. A cluster, meaning a
 * [Type]-Pair<Int,Int>-[Pair] is a number of neighbouring tiles of the same colour that can be used to complete a task.
 * So clusters contains all the collections of such neighbouring tiles. (More precisely, a cluster contains only the
 * positions, the tiles can than be accessed over [grid].)
 *
 * IMPORTANT: Adding a [Tile] to [grid] should only be done with [placeTileOnGrid].
 *
 * @property name Name of the player
 * @property playerColour [PlayerColour] of the player.
 * @property playerType [PlayerType] of the player, i.e. human or AI with certain difficulty.
 * @property tokens Tokens of the player.
 * @property choseTip Flag to show wether the player chose a tip during the game.
 * @property grid Player's grid where the [Tile]s can be placed. Adding to grid should only happen with addTileToGrid.
 * @property clusters Contains all the collections of neighbouring tiles of the same colour.
 */
@Serializable
data class Player(val name: String,
             val playerColour: PlayerColour,
             val playerType: PlayerType,
             var tokens: Int) {
    // ATTENTION: Name of attribute tokenColour from the diagram was changed to playerColour.

    var choseTip: Boolean = false
    val grid = Array<Array<Tile>>(9){
        Array<Tile>(9){
            Tile()
        }
    }
    val clusters : MutableSet<Pair<Type,Set<Pair<Int,Int>>>> = mutableSetOf()

    /**
     * Function to clone a player.
     */
    fun clone(): Player{
        val tempChoseTip = choseTip
        val tempGrid = grid
        val tempClusters = clusters
        return Player("" + this.name,this.playerColour,this.playerType,this.tokens).apply {
            this.choseTip = tempChoseTip
            tempGrid.forEachIndexed { x, array ->
                array.forEachIndexed { y, tile ->
                    this.grid[x][y] = tile.clone()
                }
            }
            tempClusters.forEach { typeSetPair ->
                // The Type can be directly copied.
                val newType = typeSetPair.first
                // Turn the set to copy into a list.
                // Then create a new list by copying all the elements. The elements are Pair<Int,Int>, thus a new Pair
                // is created containing the same Int-values.
                val oldSetAsList = typeSetPair.second.toList()
                val newSetAsList = MutableList(oldSetAsList.size){
                    Pair(oldSetAsList[it].first,oldSetAsList[it].second)
                }

                clusters.add(Pair(newType,newSetAsList.toSet()))
            }
        }
    }

    /**
     * Returns the [Type] of the [Tile] at [pos] in the [grid].
     *
     * Does not check whether the [Type] is MEEPLE, which would be an inconsistent state.
     *
     * @param pos Position for which the type is checked.
     * @return Type of the tile at [pos].
     * @exception IllegalArgumentException If pos is not a position.
     */
    private fun typeOfGridPosition(pos: Pair<Int,Int>): Type{
        require(isPosition(pos)) {"pos is not a Position. Coordinates of pos: (${pos.first},${pos.second})"}
        return grid[pos.first][pos.second].type
    }

    /**
     * Checks if the [grid] position is eligible for placing a [Tile].
     *
     * This is the case if the position itself is empty and
     * (1) it has a neighbour which already contains a colored tile or
     * (2) the grid is completely empty.
     *
     * @param pos Grid position to check for eligibility.
     * @return True if position is eligible for tile placement, else false.
     * @exception IllegalArgumentException If pos is not a position.
     */
    fun isEligibleGridPosition(pos: Pair<Int,Int>): Boolean{
        // Check that pos is actually a position.
        require(isPosition(pos)) {"pos is not a Position. Coordinates of pos: (${pos.first},${pos.second})"}

        // Checks first if the grid position is already occupied. If this is the case, false is returned.
        // If the position is empty, checks if any of the neighbours is not empty
        // or if the entire grid is empty.
        // Is one of these conditions true, returns true. Else false.
        return !Type.shortType().contains(typeOfGridPosition(pos)) &&
                (getNeighbours(pos).any { Type.shortType().contains(grid[it.first][it.second].type) }
                        || grid.all { array -> array.all { tile -> tile.type == Type.EMPTY } })
    }

    /**
     * Updates [clusters] given the element of [grid] at [pos].
     *
     * The method collects all clusters in [clusters] that are of the same [Type] as the [Tile] at [pos] and contain a
     * position that is a neighbour of [pos]. These clusters are replaced in [clusters] by the union of all these
     * clusters and the new position [pos].
     * If no relevant clusters in [clusters] exists, a new one-element cluster of the [Type] of the respective [Tile]
     * and containing only [pos] is added to [clusters].
     *
     * @param pos The [grid] position used in the updating of clusters.
     * @exception IllegalArgumentException If [pos] is not a position.
     * @exception IllegalArgumentException If the [Type] of [Tile] at [pos] is not a colour (so is [Type.EMPTY]
     * or [Type.MEEPLE]).
     * @exception IllegalStateException If no new cluster was added. This should never be the case if the method is
     * correct.
     *
     */
    private fun updateClusters(pos: Pair<Int,Int>){
        // Check that pos is actually a position.
        require(isPosition(pos)) {"pos is not a Position. Coordinates of pos1: (${pos.first},${pos.second})"}
        // Get the type at pos.
        val type = typeOfGridPosition(pos)
        // Check that the type is appropriate, meaning that it is a colour.
        require(Type.shortType().contains(type)) {"Wrong type, should be colour but is: $type"}

        // Initialize the new cluster-set with the position.
        val newClusterSet = mutableSetOf(pos)

        /* Checks for every cluster whether it is relevant.
        A cluster is relevant if (1) it's type is equal to the value of the argument 'type' and (2) it contains
        at least one position that is a neighbour of type.
        All relevant cluster-sets are unionized in newClusterSet and then removed from clusters. */
        clusters.removeAll { cluster ->
            val relevant =
                cluster.first == type && cluster.second.any {
                    areNeighbours(it,pos)
                }
            if (relevant){
                newClusterSet += cluster.second
            }
            relevant
        }

        // Finally, add the new cluster to clusters
        val added = clusters.add(Pair(type,newClusterSet))

        // Check that an element was added, which always has to be the case
        check(added) {"No new cluster added."}
    }

    /**
     * Places [Tile] tile in the [Player]'s [grid] at the position [pos] and updates the [clusters].
     *
     * @param tile [Tile] to add to the [grid].
     * @param pos Position where [tile] is to be added.
     * @exception IllegalArgumentException [Type] of [tile] is not a colour (so is [Type.EMPTY] or [Type.MEEPLE]).
     * @exception IllegalArgumentException If pos is not a position.
     * @exception IllegalArgumentException If pos is not an eligible position.
     */
    fun placeTileOnGrid(tile: Tile, pos: Pair<Int,Int>){
        require(Type.shortType().contains(tile.type)) {"Type of tile should be colour, but is instead: ${tile.type}"}
        require(isPosition(pos)) {"pos is not a Position. Coordinates of pos: (${pos.first},${pos.second})"}
        require(isEligibleGridPosition(pos)) {"pos is not a eligible grid position."}

        // Place the tile in the grid and update the clusters.
        grid[pos.first][pos.second] = tile
        updateClusters(pos)
    }

    companion object{
        /**
         * Checks whether pos is a grid position.
         */
        fun isPosition(pos: Pair<Int,Int>): Boolean{
            return pos.first in 0..8 && pos.second in 0..8
        }

        /**
         * Gets all neighbours of a position.
         */
        fun getNeighbours(pos: Pair<Int,Int>): Set<Pair<Int,Int>>{
            // Check that pos is actually a position.
            require(isPosition(pos)) {"pos is not a Position. Coordinates of pos1: (${pos.first},${pos.second})"}

            // Returns the list of neighbours. First, all possible neighbours are created. Then the ones that do not exist
            // are filtered out.
            return listOf(
                Pair(pos.first,pos.second-1),
                Pair(pos.first,pos.second+1),
                Pair(pos.first-1,pos.second),
                Pair(pos.first+1,pos.second)
            ).filter {
                isPosition(it)
            }.toSet()
        }

        /**
         * Checks if pos1 and pos2 are neighbours. If they are the same, they are not neighbours.
         */
        fun areNeighbours(pos1: Pair<Int,Int>, pos2: Pair<Int,Int>): Boolean{
            // Check that pos1, pos2 are actually positions.
            require(isPosition(pos1)) {"pos1 is not a Position. Coordinates of pos1: (${pos1.first},${pos1.second})"}
            require(isPosition(pos2)) {"pos1 is not a Position. Coordinates of pos1: (${pos2.first},${pos2.second})"}

            // Calculate the differences in absolutes for x and y.
            val xDiff = abs(pos1.first - pos2.first)
            val yDiff = abs(pos1.second - pos2.second)

            // Check that exactly one is equal 1, the other equal 0. (Here one could also check xDiff + yDiff == 1.)
            return (xDiff == 1 && yDiff == 0) || (xDiff == 0 && yDiff == 1)
        }
    }
}