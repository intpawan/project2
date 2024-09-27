package entity

import kotlinx.serialization.Serializable

/**
 * class to represent up to three different tasks on a [Tile].
 * characterized by a state [completed] which assigns whether a task is completed and its [colours]
 */
@Serializable
data class Task(var completed: Boolean, val colours: MutableList<Type>) {
    /**
     * This function clones this [Task] object
     */
    fun clone(): Task{
        return Task(
            this.completed,
            MutableList(colours.size){
                colours[it]
            }
        )
    }
}