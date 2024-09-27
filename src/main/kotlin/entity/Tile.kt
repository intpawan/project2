package entity

import kotlinx.serialization.Serializable

/**
 *Data class for the games [Tile].
 *
 * It is characterized by a [Type], a [Cost] and a List of [Task]s.
 *
 * About [id]:
 * Tiles read from csv-files get the id specified there.
 * Tiles created without specifying the id get the id -1.
 * The meeple should get the id 69.
 * Empty tiles should get the id 0.
 */
@Serializable
data class Tile (val type: Type, val cost: Cost, val tasks: MutableList<Task>, val id: Int  = -1)
{
    /**
     *  Constructor for an empty tile. The cost of an empty tile is set to Zero,
     *  the lowest possible value.
     *  As there are no tasks to fulfill, the list of tasks on that tile is empty.
     *  The id of an empty tile is set to 0.
     */
    constructor(): this(Type.EMPTY, Cost.ZERO, mutableListOf<Task>(), 0)

    /**
     * function to clone, needs to be implemented.
     */
    fun clone(): Tile{
        return Tile(
            this.type,
            this.cost,
            MutableList(tasks.size){
                tasks[it].clone()
            },
            this.id
        )
    }
}



