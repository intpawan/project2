package entity

import java.util.*

/**
 * Enum class to represent the cost of a tile.
 */
enum class Cost {
    ZERO,
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    ;

    /**
     * provides a simple character to represent the cost.
     * Returns one of: 1,2,3,4,5,6,7.
     */
   override fun toString() =
        when(this) {
            ZERO -> "0"
            ONE -> "1"
            TWO -> "2"
            THREE -> "3"
            FOUR -> "4"
            FIVE -> "5"
            SIX -> "6"
            SEVEN -> "7"
      }

}