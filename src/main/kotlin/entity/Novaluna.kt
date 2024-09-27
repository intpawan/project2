package entity

/**
 * data class to represent a game of NovaLuna
 * it is characterized by a list of [highscores] and [game]
 */
data class Novaluna (var highscores: MutableList<Score>, var game : Game?){
}
