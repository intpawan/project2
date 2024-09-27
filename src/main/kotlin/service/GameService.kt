package service

import entity.*
import java.io.File
import kotlin.random.Random

/**
 * Service layer class that provides the logic for actions not directly related to a single player.
 *
 * @property rootService Instance of [RootService] to access the other service layer classes and the entity layer.
 */
class GameService(private val rootService: RootService): AbstractRefreshingService() {

    /**
     * Method to start a standard game (neither the first game, nor a tournament or a simulation game).
     *
     * It is assumed that the player information in [players] is consistent. This is not checked. Consistent means:
     * The [String]s in [players] are permitted names.
     * Every [PlayerColour] is unique.
     *
     * The order of the triple list [players] is set as the initial playing order.
     *
     * @param players [List] of [Triple]s containing the information for the [Player]s to be created.
     *
     * @throws IllegalStateException If there is already a currently active game.
     * @throws IllegalArgumentException If the length of [players] is not between 2 and 4.
     */
    fun startNewGame(players: List<Triple<String, PlayerColour, PlayerType>>){
        startNewGameHelper(players = players)
    }

    /**
     * Method to start a first game (with reduced tokens if there are three or four players).
     *
     * It is assumed that the player information in [players] is consistent. This is not checked. Consistent means:
     * The [String]s in [players] are permitted names.
     * Every [PlayerColour] is unique.
     *
     * The order of the triple list [players] is set as the initial playing order.
     *
     * @param players [List] of [Triple]s containing the information for the [Player]s to be created.
     *
     * @throws IllegalStateException If there is already a currently active game.
     * @throws IllegalArgumentException If the length of [players] is not between 2 and 4.
     */
    fun startNewFirstGame(players: List<Triple<String, PlayerColour, PlayerType>>){
        val startingTokens = when(players.size){
            2 -> 18
            3 -> 18
            4 -> 16
            else -> throw IllegalArgumentException("Number of players to be created is not between 2 and 4.")
        }
        startNewGameHelper(players = players, startingTokens = startingTokens)
    }

    /**
     * Method to start an AI-tournament-game (with reduced tokens if there are three or four players).
     *
     * It is assumed that the player information in [players] is consistent. This is not checked. Consistent means:
     * The [String]s in [players] are permitted names.
     * Every [PlayerColour] is unique.
     * There are exactly two players: One KI player of the highest difficulty and one human player
     *
     * The order of the triple list [players] is set as the initial playing order.
     *
     * @param players [List] of [Triple]s containing the information for the [Player]s to be created.
     * @param gameConfigFile CSV-[File] containing the [Tile]-Stack information.
     *
     * @throws IllegalStateException If there is already a currently active game.
     * @throws IllegalArgumentException If the length of [players] is not between 2 and 4.
     */
    fun startNewAITournamentGame(players: List<Triple<String, PlayerColour, PlayerType>>, gameConfigFile: File){
        val speedTournamentShuffle = Triple(0,true,false)
        startNewGameHelper(
            players = players,
            gameConfigFile = gameConfigFile,
            //aiTournament = true,
            //shuffleTiles = false
            speedTournamentShuffle = speedTournamentShuffle
        )
    }

    /**
     * Method to start an AI-simulation-game (with reduced tokens if there are three or four players).
     *
     * It is assumed that the player information in [players] is consistent. This is not checked. Consistent means:
     * The [String]s in [players] are permitted names.
     * Every [PlayerColour] is unique.
     * Every player is an ai-player.
     *
     * The order of the triple list [players] is set as the initial playing order.
     *
     * @param players [List] of [Triple]s containing the information for the [Player]s to be created.
     * @param aiSpeed Integer denoting the simulation speed of the AI.
     *
     * @throws IllegalStateException If there is already a currently active game.
     * @throws IllegalArgumentException If the length of [players] is not between 2 and 4.
     * @throws IllegalArgumentException If [aiSpeed] is negative.
     */
    fun startNewAISimulationGame(players: List<Triple<String, PlayerColour, PlayerType>>, aiSpeed: Int){
        val speedTournamentShuffle = Triple(aiSpeed,false,true)
        startNewGameHelper(
            players = players,
            //aiSpeed = aiSpeed
            speedTournamentShuffle = speedTournamentShuffle
        )
    }

    /**
     * Helper-method to start a new [Game].
     *
     * It is assumed that the player information in [players] is consistent. This is not checked. Consistent means:
     * It is assumed that the [String]s in [players] are permitted names.
     * Every [PlayerColour] is unique.
     *
     * The order of the triple list [players] is set as the initial playing order.
     *
     * @param players [List] of [Triple]s containing the information for the [Player]s to be created.
     * @param startingTokens Number of starting tokens. Needs to be one of the following values: 21,18,16.
     * @param gameConfigFile CSV-[File] containing the [Tile]-Stack information.
     * @param speedTournamentShuffle [Triple] containing the information regarding aiSpeed, if it is a tournament game
     * and whether the [Tile]-Stack should be shuffled (in that order).
     * @param random Used for the shuffling of the [Tile]-Stack. Can be specified for testing.
     *
     * @throws IllegalStateException If there is already a currently active game.
     * @throws IllegalArgumentException If the length of [players] is not between 2 and 4.
     * @throws IllegalArgumentException If [startingTokens] is not equal 21 or 18 or 16.
     * @throws IllegalArgumentException If the aiSpeed in [speedTournamentShuffle] is negative.
     * @throws IllegalArgumentException If the boolean flags for aiTournament and shuffleTiles in
     * [speedTournamentShuffle] are both true.
     */
    private fun startNewGameHelper(
        players: List<Triple<String, PlayerColour, PlayerType>>,
        startingTokens: Int = 21,
        gameConfigFile: File = File("src/main/resources/default_CSV_SaveFile/nl_tiles.csv"),
        //aiSpeed: Int = 0,
        //aiTournament: Boolean = false,
        //shuffleTiles: Boolean = true,
        speedTournamentShuffle: Triple<Int,Boolean,Boolean> = Triple(0,false,true),
        random: Random = Random.Default
    ){
        val aiSpeed = speedTournamentShuffle.first
        val aiTournament = speedTournamentShuffle.second
        val shuffleTiles = speedTournamentShuffle.third


        check(rootService.novaluna.game == null) { "There is already a currently active game."}
        require(players.size in 2..4) {"Number of players to be created is not between 2 and 4."}
        require(startingTokens == 21 || startingTokens == 18 || startingTokens == 16) {
            "Illegal value for starting tokens."
        }
        require(aiSpeed >= 0) {"aiSpeed is negative."}
        require(!(aiTournament && shuffleTiles)) {"The boolean flags aiTournament and shuffleTiles are both true."}

        // Create the players.
        val createdPlayers = MutableList(players.size){
            val currentTriple = players[it]
            Player(currentTriple.first,currentTriple.second,currentTriple.third,startingTokens)
        }

        // Create the tiles and shuffle them, if shuffleTiles is true.
        rootService.ioService.CSV_FILE = gameConfigFile
        val tiles = rootService.ioService.csvToTiles()
        if(shuffleTiles){
            tiles.shuffle(random)
        }

        // Create the list of states. This list only contains the initial state.
        // Note that in this initial state the moonwheel still needs to be filled.
        val states = mutableListOf(createInitialState(tiles,createdPlayers))

        // Create a game with the fitting parameters. In particular, choseUndo is false.
        rootService.novaluna.game = Game(states[0], aiTournament, String(), false, aiSpeed, states)

        // Refill the tiles of the moonwheel using the method of PlayerActionService
        rootService.playerActionService.refillTiles()
        
        // Simulate the placing of the tokens on the moonwheel by reducing the token number of each player by 1
        val currentGame = rootService.novaluna.game
        checkNotNull(currentGame)
        currentGame.states[0].players.forEach {
            it.tokens--
        }
        onAllRefreshables { refreshAfterStartNewGame() }

        rootService.ioService.PROTOCOL_FILE.writeText("")
    }

    /**
     * Helper-method to create the initial state given players and tiles.
     *
     * Initialization means:
     * The players, the current player and the tile stack are set an the moon-meeple is placed.
     * The moonwheel is not yet filled.
     */
    private fun createInitialState(stack: MutableList<Tile>, players: MutableList<Player>): State{
        val state = State(stack, Moonwheel(), players[0], players, "")
        state.moonwheel.apply {
            // It is important that the list at playerPositions[0] is not the same list as players but a shallow copy.
            // Thus, with mutableList a shallow copy (different list, same players) is created.
            playerPositions[0] = players.toMutableList()
            tiles[0] = createMoonMeeple()
        }
        return state
    }

    /**
     * Helper-method to create the moon-meeple.
     */
    private fun createMoonMeeple(): Tile{
        return Tile(
            Type.MEEPLE,Cost.ZERO, mutableListOf(),69
        )
    }

    /**
     * Makes the necessary steps after a [Player]'s moved is finished. Works on [currentState].
     *
     * NOTE: Throws an exception, if the state already satisfies an endGameCondition.
     *
     * @exception IllegalArgumentException [currentState] satisfies an endGameCondition (see [endGameConditionReached]).
     */
    private fun manipulateStateEndMove(currentState: State){
        // Check that currentState does not satisfy a game ending condition.
        require(!endGameConditionReached(currentState))

        // Refill the moonwheel if necessary
        if(currentState.moonwheel.tiles.count { Type.shortType().contains(it.type) } == 0){
            rootService.playerActionService.refillTiles(currentState)
        }

        //Advance the current player
        advanceCurrentPlayer(currentState)
    }

    /**
     * Makes the necessary steps after a [Player]'s moved is finished. Works on the current [Game].
     *
     * Checks if a game-ending conidition is reached. If this is the case, the respective method is called. If not, the
     * [Moonwheel] is refilled if necessary and the current player is advanced. This is done by calling
     * [manipulateStateEndMove].
     *
     * @exception IllegalStateException There is no currently active game.
     */
    fun endMove(){
        val currentGame = rootService.novaluna.game
        checkNotNull(currentGame){ "There is no currently active game."}
        if( endGameConditionReached(currentGame.currentState) ){
            endGame()
        }else{
            manipulateStateEndMove(currentGame.currentState)

            // Call the refreshables.
            onAllRefreshables { refreshAfterEndMove() }
        }
    }

    /**
     * Checks whether a condition to end the game is reached. Check is done on [currentState].
     *
     * This is the case if and only if
     * (1) the current player has 0 tokens or
     * (2) the moonwheel needs to be refilled (contains 0 coloured tiles) and the stack is empty.
     *
     * @param currentState [State] for which the conditions are checked.
     * @return True if a condition to end the game is reached. Else false.
     */
    fun endGameConditionReached(currentState: State): Boolean{
        // Get the current player and the moonwheel and stack.
        val currentPlayer = currentState.currentPlayer
        val moonwheel = currentState.moonwheel
        val stack = currentState.stack

        return currentPlayer.tokens == 0 ||
                ( moonwheel.tiles.count { Type.shortType().contains(it.type) } == 0 && stack.isEmpty() )
    }

    /**
     * Calculates the next player from the current situation on the moonwheel and advances currentState.currentPlayer
     * accordingly. Manipulation done on [currentState].
     *
     * Does not check, whether an endGameCondition is already reached. Thus, this method works on a [State] that already
     * satisfies an endGameCondition without throwing an exception. To prevent this, use [endGameConditionReached]
     * before calling this method.
     *
     * @param currentState State to be manipulated.
     * @exception IllegalStateException The [Moonwheel] is inconsistent with regard to the current player.
     */
    fun advanceCurrentPlayer(currentState: State){
        // Get the current player and the moonwheel.
        val currentPlayer = currentState.currentPlayer
        val moonwheel = currentState.moonwheel

        // Get all the occurrences of currentPlayer. Check that he occurres only once and that he's the first player
        // in his list.
        val currentPlayerOccurences = moonwheel.playerPositions.filter { playerList ->
            playerList.contains(currentPlayer)
        }
        check(
            currentPlayerOccurences.size == 1 && currentPlayerOccurences[0].count { it == currentPlayer } == 1
        ) { "Error: currentPlayer occurres more than once on the wheel." }

        // Get the current position. Because of the checks above, the value is definitely in 0..23 (and not -1).
        val positionCurrentPlayer = moonwheel.playerPositions.indexOf(currentPlayerOccurences[0])

        /* Get the next current player.
        The next current player is the first player in the first not-empty list that is found when going forward 17
        positions (or backward 7 positions) from positionCurrentPlayer and then advancing to this position.
        Note that if no such list is found before again reaching positionCurrentPlayer, then the currentPlayer stays
        the same.
         */
        var currentPlayerChanged = false
        var i = (positionCurrentPlayer + 17) % 24
        while (!currentPlayerChanged && i != positionCurrentPlayer){
            if(moonwheel.playerPositions[i].isNotEmpty()){
                currentState.currentPlayer = moonwheel.playerPositions[i][0]
                currentPlayerChanged = true
            }else{
                i = (i+1) % 24
            }
        }
    }

    /**
     * Method to end the current [Game].
     *
     * Activates the refreshables.
     * It also determines the winner and saves him to the HIGHSCORE_FILE
     *
     * @exception IllegalStateException There is no currently active game.
     */
    private fun endGame(){
        val currentGame = rootService.novaluna.game
        checkNotNull(currentGame)
        val winner : Pair<String,Double> = rootService.aiService.highScore(currentGame)
        val highScore = Score(winner.second,winner.first)
        rootService.novaluna.highscores.add(highScore)
        rootService.ioService.saveHighscore()
        rootService.ioService.saveProtocol()
        onAllRefreshables { refreshAfterEndGame() }
    }

    /**
     * Sets the current [Game] (i.e. novaluna.game) to null.
     *
     * It is not checked, whether there is a currently active game or not.
     */
    fun reset(){
        rootService.novaluna.game = null
        onAllRefreshables { refreshAfterReset() }
    }
}