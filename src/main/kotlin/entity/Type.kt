package entity
import java.util.*
/**
 * There are 6 types of [Tile]
 * Meeple is a special type of [Tile] and is a Moon
 * Empty [Tile] are empty tiles (initially all tiles of the player are empty)
 * [Tile] got a colour, which is [CYAN], [BLUE], [RED] or [YELLOW]
 */
enum class Type {
    CYAN,
    BLUE,
    RED,
    YELLOW,
    MEEPLE,
    EMPTY
    ;

    companion object{
        /**
         * A set of values for colors of the [Tile]
         */
        fun shortType() : Set<Type> {
            return EnumSet.of(CYAN, BLUE, RED, YELLOW)
        }
    }
}

