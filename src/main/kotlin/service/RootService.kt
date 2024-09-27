package service

import entity.Novaluna
import view.Refreshable

/**
 * Main class of the service layer. Provides access to all other service classes and holds the current [Novaluna] state.
 *
 * @property gameService Instance of [GameService] to access the service-methods.
 * @property playerActionService Instance of [PlayerActionService] to access the playeraction-methods.
 * @property ioService Instance of [IOService] to access the io-methods.
 * @property aiService Instance of [AIService] to access the ai-methods.
 * @property novaluna The current [Novaluna] game holding all the data.
 */
class RootService(val novaluna: Novaluna) {

    val gameService = GameService(this)
    val playerActionService = PlayerActionService(this)
    val ioService = IOService(this)
    val aiService = AIService(this)


    /**
     * Adds the provided [newRefreshable] to all services connected
     * to this root service
     */
    fun addRefreshable(newRefreshable: Refreshable){
        gameService.addRefreshable(newRefreshable)
        playerActionService.addRefreshable(newRefreshable)
        ioService.addRefreshable(newRefreshable)
        aiService.addRefreshable(newRefreshable)
    }


    /**
     * Adds each of the provided [newRefreshables] to all services
     * connected to this root service
     */
    fun addRefreshable(vararg newRefreshables: Refreshable){
        newRefreshables.forEach{ addRefreshable(it) }
    }
}