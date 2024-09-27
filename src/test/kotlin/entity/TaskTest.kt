package entity

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.test.*

/**
 * Test class for [Task]
 */
class TaskTest {
    private val task1 = Task(false, mutableListOf(Type.RED, Type.BLUE))
    private val task2 = Task(true, mutableListOf(Type.CYAN, Type.BLUE))
    private val task3 = Task(false, mutableListOf(Type.RED, Type.YELLOW))
    private val task4 = Task(false, mutableListOf(Type.CYAN, Type.YELLOW))

    /**
     * Test if getting the value of "completed"  works
     */
    @Test
    fun testGetCompleted(){
        val completedTask3 : Boolean = task3.completed
        assertFalse(completedTask3)
    }

    /**
     * Test if setting the value of "completed" works
     */
    @Test
    fun testSetCompleted(){
        task4.completed = false
        assertFalse(task4.completed)
        task3.completed = true
        assertTrue(task3.completed)
    }

    /**
     * Test if getting the values of "colours"  works
     */
    @Test
    fun testGetColours(){
        val testColours : List<Type> = task3.colours
        assertEquals(testColours[0], Type.RED)
        assertEquals(testColours[1], Type.YELLOW)
    }

    /**
     * Test if modifying the values of "colours" works
     */
    @Test
    fun testModifyColours(){
        val testColours : MutableList<Type> = mutableListOf(Type.CYAN, Type.YELLOW)
        assertEquals(task4.colours, testColours)
        testColours.add(Type.RED)
        task4.colours.add(Type.RED)
        assertEquals(task4.colours, testColours)
    }


    /**
     * Test if cloning works
     */
    @Test
    fun testClone(){
        val clonedTask1 = task1.clone()
        val clonedTask2 = task2.clone()
        assertEquals(task1, clonedTask1)
        assertNotSame(task1,clonedTask1)
        assertEquals(task2, clonedTask2)
        assertNotSame(task2,clonedTask2)
    }

    /**
     * Test if serialization works
     */
    @Test
    fun testSerialize(){
        val taskJson : String = Json.encodeToString(serializer<Task>(), task1)
        val jsonString = "{\"completed\":false,\"colours\":[\"RED\",\"BLUE\"]}"
        assertEquals(taskJson,jsonString)
        val jsonTask = Json.decodeFromString<Task>(taskJson)
        assertEquals(task1,jsonTask)
    }
}