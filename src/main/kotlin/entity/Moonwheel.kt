package entity

import kotlinx.serialization.Serializable
import tools.aqua.bgw.util.Stack

/**
 * class to represent the moonwheel itself
 * characterized by the [playerPositions] of the players in the game and
 * the [tiles] which a lying around the wheel including the meeple
 *
 * Class to represent the Moonwheel.
 *
 * Note that the length of both arrays is important in the context of the game:
 * The array tile has to be of length 12, corresponding to the number of tiles placed around the moonwheel.
 * The array playerPositions has to be of length 24, corresponding to the number of playerPositions of the moonwheel.
 *
 * Note also that empty positions in the array tiles are marked by Type.EMPTY-Tiles, not by null.
 *
 * @param tiles Array containing the tiles around the Moonwheel.
 * @param playerPositions Array representing the playerPositions.
 */
@Serializable
data class Moonwheel (
    val tiles : Array<Tile> = Array(12){Tile()},
    val playerPositions : Array<MutableList<Player>> = Array(24){
        mutableListOf<Player>()
    }) {


    /**
     * function to clone,
     */
     /*fun clone(): Moonwheel{

        val clonedPlayerPositions = playerPositions
        var tmp : MutableList<Player>
        val clonedTile = tiles

        for (i in 0..clonedPlayerPositions.size-1) {

            tmp   = clonedPlayerPositions[i].popAll() as MutableList<Player>
            for (j in 0..tmp.size-1) {
                tmp[j] = tmp[j].clone()
            }
            clonedPlayerPositions[i].pushAll(tmp)
        }

        var tmpTiles : MutableList<Tile> = mutableListOf() //emptyList<Tile>() as MutableList<Tile>
        for (i in 0..tiles.size-1){

            tmpTiles.add(clonedTile[i].clone())

        }
        return Moonwheel(clonedPlayerPositions, tmpTiles)
    }*/
    /**
     * Method to create a deep clone of the moonwheel.
     *
     * @return The deep clone of the moonwheel.
     */
    fun clone(): Moonwheel{
        // Create an array with empty tiles, which is later filled with the cloned tiles.
        val clonedTiles = Array<Tile>(12){
            Tile()
        }

        // Fill the array clonedTiles with the cloned tiles from tiles
        tiles.forEachIndexed { index, tile ->
            clonedTiles[index] = tile.clone()
        }

        // Create an array of empty mutable lists, which is later filled with the players.
        val clonedPlayerPositions = Array<MutableList<Player>>(24){
            mutableListOf<Player>()
        }

        // Clone every list in playerPositions and insert them in clonedPlayerPositions
        playerPositions.forEachIndexed { index, playerList ->
            playerList.forEach {
                clonedPlayerPositions[index].add(it.clone())
            }
        }
        return Moonwheel(clonedTiles,clonedPlayerPositions)
    }

    /**
     * vergleicht ob tiles auf dem moonwheel identisch sind.
     */
    override fun equals(other: Any?): Boolean {
        val otherWheel : Moonwheel = other as Moonwheel
        for(i in this.playerPositions.indices){
            var players = playerPositions[i]
            var otherPlayers = otherWheel.playerPositions[i]
            if(players.size != otherPlayers.size) return false
            var equalPlayers = players.zip(otherPlayers).all{ (x,y) -> x == y}
            if(!equalPlayers) return false
      }
      return this.tiles.contentEquals(otherWheel.tiles)
    }

    override fun hashCode(): Int {
        var result = tiles.contentHashCode()
        result = 31 * result + playerPositions.contentHashCode()
        return result
    }

}
