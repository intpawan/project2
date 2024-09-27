package entity

/**
 * Enum class for the type of [Player]. The [Player] can choose the difficulty of a [Game].
 * Human for human [Player]
 * Easy for the slowest speed of AI
 * Normal for the medium speed of AI
 * Hard for the fastest speed of AI
 */
enum class PlayerType {
    HUMAN,
    EASY,
    NORMAL,
    HARD,
    ;
}