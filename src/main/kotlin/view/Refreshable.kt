package view

/**
 * interface to update visuals after certain actions.
 */
interface Refreshable {

    /**
     * update the view after a new game is started
     */
    fun refreshAfterStartNewGame(){}

    /**
     * refresh after game is ended
     */
    fun refreshAfterEndGame(){}

    /**
     * refresh after player has made a move
     */
    fun refreshAfterMakeMove(){}

    /**
     * puts the game back to the state of origin
     */
    fun refreshAfterReset(){}

    /**
     * prepares the board for the next player
     */
    fun refreshAfterEndMove(){}


    /**
     * sets the board to the previous state after the player chooses undo
     */
    fun refreshAfterUndo(){}

    /**
     * gets back to the state of origin, after the player redos
     */
    fun refreshAfterRedo(){}

    /**
     * shows the tip of the hard AI for the player.
     */
    fun refreshAfterGetTip(){}

    /**
     * updates the moonwheel with new tiles
     */
    fun refreshAfterRefillTiles(){}

    /**
     * refreshes after the AI took a turn.
     */
    fun refreshAfterMakeAITurn(){}

}