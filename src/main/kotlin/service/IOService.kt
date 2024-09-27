package service

import entity.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.File

/**
 * Service layer class that provides reading and writing to the [SAVE_FILE] and [HIGHSCORE_FILE].
 * It also parses the [CSV_File]. All Files have a default path.
 *
 * @property rootService Instance of [RootService] to access the other service layer classes and the entity layer.
 *
 * @property CSV_FILE file containing the information for the tiles with their tasks
 * @property SAVE_FILE file containing all information of the game last saved
 * @property HIGHSCORE_FILE file containing the list of highscores
 *
 *
 */
class IOService(val rootService: RootService) : AbstractRefreshingService() {
    var CSV_FILE : File = File("src/main/resources/default_CSV_SaveFile/nl_tiles.csv")
    var PROTOCOL_FILE : File = File("src/main/resources/default_CSV_SaveFile/protocol.csv")
    var SAVE_FILE : File = File("src/main/resources/default_CSV_SaveFile/gameSaved.json")
    var HIGHSCORE_FILE : File = File("src/main/resources/default_CSV_SaveFile/highscoreSaved.json")


    /**
     * Reads the saved game from [SAVE_FILE] and creates a [Game] object as the new
     * current game
     *
     * @throws IllegalArgumentException if Game-[File] does not end with .json
     * or does not exist
     */
    fun loadGame() {
        val file = this.SAVE_FILE
        require(file.extension.equals("json")){
            "The game file should end with .json"
        }
        require(file.exists()) {
            "The game file does not exist"
        }
        val gameFileString : String = this.SAVE_FILE.readText()
        val loadGame : Game = Json.decodeFromString<Game>(gameFileString)
        this.rootService.novaluna.game = loadGame
    }

    /**
     *  Reads the list of highscores the [HIGHSCORE_FILE]
     *
     * @throws IllegalArgumentException if Highscore-[File] does not end with .json or exist
     */
    fun loadHighscore() {
        val fileExtension = this.HIGHSCORE_FILE.extension
        require(fileExtension.equals("json")){
            "The highscore file should end with .json"
        }
        this.HIGHSCORE_FILE.forEachLine { println(it) }
        val highscoreFileString : String = this.HIGHSCORE_FILE.readText()
        val loadHighscores : MutableList<Score> =
            Json.decodeFromString(ListSerializer(Score.serializer()), highscoreFileString) as MutableList<Score>
        this.rootService.novaluna.highscores = loadHighscores
    }


    /**
     * Overwrites the [SAVE_FILE] with the currentGame converted as a Json object
     *
     * @throws IllegalArgumentException if current game does not exist or the Game-[File] does not
     * end on .json or exist
     */
    fun saveGame() {
        val novaluna = this.rootService.novaluna
        val saveGame = novaluna.game
        require(saveGame != null){ "Cannot save game. The current game is not defined" }
        val fileExtension = this.SAVE_FILE.extension
        require(fileExtension.equals("json")){
            "The game file should end with .json"
        }
        val gameString = Json.encodeToString(saveGame)
        this.SAVE_FILE.writeText(gameString)
    }


    /**
     * Overwrites the [HIGHSCORE_FILE] with the current highscore list converted as a Json object
     *
     * @throws IllegalArgumentException the Highscore-[File] does not end on .json
     */
    fun saveHighscore(){
        val novaluna = this.rootService.novaluna
        val saveHighscores = novaluna.highscores
        val fileExtension = this.HIGHSCORE_FILE.extension
        require(fileExtension.equals("json")){
            "The highscore file should end with .json"
        }
        val highscoresString = Json.encodeToString(saveHighscores)
        this.HIGHSCORE_FILE.writeText(highscoresString)
    }


   /**
    * Reads from the given CSV-[File] and creates a list of all lines
    * where every line is split by comma.
    * Skips the first line of the CSV-[File] with "ID,colour,cost,task1,task2,task3"
    *
    * @throws IllegalArgumentException if CSV-[File] does not end with .csv
    * @return the list of all lines in the CSV-[File] which are split by comma
    */
   fun parseCSV() : MutableList<MutableList<String>>{
        val fileExtension = CSV_FILE.extension
        require(fileExtension.equals("csv")){
            "The config file should end with .csv"
        }
        val linesCSV : MutableList<MutableList<String>> = mutableListOf()
        val reader : BufferedReader = CSV_FILE.inputStream().bufferedReader()
        var firstLine = true
        reader.lines().forEach{
            if(firstLine){
               firstLine  = false
            }
            else{
                linesCSV.add(it.split(",") as MutableList<String>)
            }
        }
        return linesCSV
    }

    /**
     * Saves the protocol created during the game to the [PROTOCOL_FILE]
     *
     * @throws IllegalArgumentException the Protocol-[File] does not end on .csv
     */
   fun saveProtocol(){
        val novaluna = this.rootService.novaluna
        val saveGame = novaluna.game
        require(saveGame != null){ "Cannot save game. The current game is not defined" }
        val fileExtension = this.PROTOCOL_FILE.extension
        require(fileExtension.equals("csv")){
            "The game file should end with .csv"
        }
        val protocolString = saveGame.currentState.protocol
        this.PROTOCOL_FILE.writeText(protocolString)
   }

    /**
     * Converts a string into a [Cost]
     *
     * @return the fitting cost represented by the string or otherwise [Cost.ZERO]
     */
   fun stringToCost(cost : String ) : Cost {
        return when(cost) {
            "0" -> Cost.ZERO // Daniel: Added Cost.ZERO identifier
            "1" -> Cost.ONE
            "2" -> Cost.TWO
            "3" -> Cost.THREE
            "4" -> Cost.FOUR
            "5" -> Cost.FIVE
            "6" -> Cost.SIX
            "7" -> Cost.SEVEN
            else -> throw IllegalArgumentException("Illegal cost identifier: $cost") // Daniel Changed this to exception
        }
    }

    /**
     * Converts a string into a [Type]
     *
     * @return the fitting cost represented by the string or otherwise [Type.EMPTY]
     */
    fun stringToType(color: String) : Type {
        return when(color){
            "blue","b" -> Type.BLUE //Daniel: Changed this and the following from b,r,y,c to full names
            "red","r" -> Type.RED
            "yellow","y" -> Type.YELLOW
            "cyan","c" -> Type.CYAN
            else -> throw IllegalArgumentException("Illegal type identifier: $color")// Daniel:Changed this to exception
        }
    }

    /**
     * Returns a [MutableList] of [Tile]s encoded in [CSV_FILE]. Thus [CSV_FILE] has to be set accordingly, if a [File]
     * different from the default file is supposed to be decoded.
     *
     * Note that [CSV_FILE] should encode a complete set of tiles. This is not checked.
     *
     * @return The list containing the tiles in the order in which they appear in the CSV-File.
     */
    fun csvToTiles(): MutableList<Tile>{
        val linesList = parseCSV()

        /* This would check whether 68 lines are read from the file (corresponding to 68 tiles).
        But because checking beyond the number of tiles whether the correct tiles are in the deck is not supposed
        to be implemented anyway, this check does not really guarantee anything and is thus omitted.*/
        // check(linesList.size == 68) {"The passed file does not encode 68 Tiles, but instead:${linesList.size}"}

        return MutableList(linesList.size){
            lineToTile(linesList[it])
        }
    }

    /**
     * Returns a [Tile] created from one [String] which is in the format of one line of the CSV-File.
     */
    private fun lineToTile(tileInformation: List<String>): Tile{
        check(tileInformation.size == 6) {
            "Wrong format: List should contain 6 Strings, contains instead: ${tileInformation.size}"
        }
        val id = tileInformation[0].toInt()
        val type = stringToType(tileInformation[1])
        val cost = stringToCost(tileInformation[2])
        val tasks = mutableListOf<Task>()
        for(i in 3 .. 5){
            if( tileInformation[i] != "")
            tasks.add(Task(
                false,
                stringToTypeList(tileInformation[i])
            ))
        }
        return Tile(type,cost,tasks,id)
    }

    /**
     * Returns a [MutableList] of [Type]s, which is encoded from the given [String].
     */
    private fun stringToTypeList(colorLetters: String): MutableList<Type>{
        check(colorLetters.length in 1..4){
            "Wrong format: String should contain 1 to 4 characters, but instead: ${colorLetters.length}"
        }
        check(colorLetters.filterNot { listOf('b','c','r','y').contains(it) } == "" ){
            "Wrong information: The letters of the String have to be in {b,c,r,y}, but String is instead: $colorLetters"
        }
        return MutableList(colorLetters.length) {
            stringToType(colorLetters[it].toString())
        }
    }
}
