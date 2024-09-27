package service

import view.Refreshable

/**
 * [Refreshable] implementation that refreshes nothing, but remembers
 * if a refresh method has been called (since last [reset])
 */
class RefreshableTest: Refreshable {

    var refreshAfterStartNewGameCalled = false
    var refreshAfterEndGameCalled = false
    var refreshAfterMakeMoveCalled = false
    var refreshAfterResetCalled = false
    var refreshAfterEndMoveCalled = false
    var refreshAfterUndoCalled = false
    var refreshAfterRedoCalled = false
    var refreshAfterGetTipCalled = false
    var refreshAfterRefillTilesCalled = false
    var refreshAfterMakeAITurnCalled = false


    /**
     * override for refreshAfterStartNewGame
     */
    override fun refreshAfterStartNewGame(){
        refreshAfterStartNewGameCalled = true
    }

    /**
     * override for refreshAfterEndGame
     */
    override fun refreshAfterEndGame(){
        refreshAfterEndGameCalled = true
    }

    /**
     * override for refreshAfterMakeMove
     */
    override fun refreshAfterMakeMove(){
        refreshAfterMakeMoveCalled = true
    }

    /**
     * override for refreshAfterReset
     */
    override fun refreshAfterReset(){
        refreshAfterResetCalled = true
    }

    /**
     * override for refreshAfterEndMove
     */
    override fun refreshAfterEndMove(){
        refreshAfterEndMoveCalled = true
    }

    /**
     * override for refreshAfterUndo
     */
    override fun refreshAfterUndo(){
        refreshAfterUndoCalled = true
    }

    /**
     * override for refreshAfterRedo
     */
    override fun refreshAfterRedo(){
        refreshAfterRedoCalled = true
    }

    /**
     * override for refreshAfterGetTip
     */
    override fun refreshAfterGetTip(){
        refreshAfterGetTipCalled = true
    }

    /**
     * override for refreshAfterRefillTiles
     */
    override fun refreshAfterRefillTiles(){
        refreshAfterRefillTilesCalled = true
    }

    /**
     * override for refreshAfterMakeAITurn
     */
    override fun refreshAfterMakeAITurn(){
        refreshAfterMakeAITurnCalled = true
    }

    /**
     * resets all *Called properties to false
     */
    fun reset() {
        refreshAfterStartNewGameCalled = false
        refreshAfterEndGameCalled = false
        refreshAfterMakeMoveCalled = false
        refreshAfterResetCalled = false
        refreshAfterEndMoveCalled = false
        refreshAfterUndoCalled = false
        refreshAfterRedoCalled = false
        refreshAfterGetTipCalled = false
        refreshAfterRefillTilesCalled = false
        refreshAfterMakeAITurnCalled = false
    }
}