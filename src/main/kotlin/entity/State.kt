package entity

import kotlinx.serialization.Serializable

/**
 * class to save the actual state
 * @property [stack] which is the list of usable tiles
 * @property [moonwheel] which is the actual state of the moonwheel
 * @property [currentPlayer] which is a pointer to the actual player
 * @property [players] which is a list of players in the games
 * @property [protocol] which is one line of the protocol for the ai tournament
 *
 */
@Serializable
data class State (var stack : MutableList<Tile>,
             var moonwheel: Moonwheel,
             var currentPlayer: Player,
             var players: MutableList<Player>,
             var protocol : String
             ) {
    /**
     * function to clone a game state
     */
   fun clone() : State{
        val newStack : MutableList<Tile> = this.stack.map{ it -> it.clone()}.toMutableList()
        val newPlayers : MutableList<Player> = this.players.map{ it -> it.clone()}.toMutableList()
        val newCurrentPlayer = newPlayers[players.indexOf(currentPlayer)]
        return State(newStack, this.moonwheel.clone(), newCurrentPlayer, newPlayers, protocol)
    }
}