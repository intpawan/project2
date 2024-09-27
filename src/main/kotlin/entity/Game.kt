package entity

import kotlinx.serialization.Serializable

/**
 * Game class
 * @property aiTournament defines weather game is an AI tournament
 * @property tournamentProtocol is a protocol to store the moves of both AI and human player
 * @property choseUndo defines weather player has chosen Undo action to cancel changes which are made
 * during the current move
 * @property aiSpeed value defines AI difficulty level
 * @property states all states are stored there in order to make undo/redo move
 * @property currentState for the current state
 */
@Serializable
data class Game(var currentState : State,
                var aiTournament: Boolean,
                var tournamentProtocol: String,
                var choseUndo: Boolean,
                var aiSpeed: Int,
                val states: MutableList<State>)