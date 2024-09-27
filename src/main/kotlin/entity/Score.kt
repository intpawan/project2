package entity

import kotlinx.serialization.Serializable

/**
 * class to store the score of one individual player and the name of the winner.
 * it is characterized by the [avgScore] of the players and a [winner] of the game
 */
@Serializable
data class Score (var avgScore: Double, var winner: String)