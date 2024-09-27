package service

import entity.*

/**
 * Service layer class that provides the logic for actions related to the single player
 *
 * @property rootService Instance of [RootService] to access the other service layer classes and the entity layer.
 */
class PlayerActionService(val rootService: RootService): AbstractRefreshingService() {


    /**
     * Refills the tiles of the moonwheel starting at the meeple if there are
     * tiles left on the stack.
     *
     * This method works directly on the currently active game. It calls [refillTiles] with the current state as
     * argument, which does the actual refilling.
     * refilling on the current [State] which is passed as an argument.
     *
     * NOTE: Do not use outside the service-layer. Outside the service layer please use the other [refillTiles]-method
     * with the [State]-argument.
     *
     * @exception IllegalStateException There is no currently active game.
     * @exception IllegalArgumentException if the moonwheel does not have a meeple
     */
    fun refillTiles(){
        val currentGame : Game? = rootService.novaluna.game
        checkNotNull(currentGame){ "There is no currently active game."}
        refillTiles(currentGame.currentState)
        //onAllRefreshables { refreshAfterRefillTiles() }
    }

    /**
     * Refills the tiles of the moonwheel starting at the meeple if there are
     * tiles left on the stack.
     *
     * This method works on the [State] [currentState] and manipulates it directly.
     *
     * @param currentState The state in which the [Moonwheel] should be refilled.
     * @exception IllegalArgumentException if the moonwheel does not have a meeple
     */
    fun refillTiles(currentState: State){
        val moonwheel : Moonwheel = currentState.moonwheel

        val meeple = Tile(Type.MEEPLE, Cost.ZERO, mutableListOf<Task>(), 69)
        val posMeeple : Int = moonwheel.tiles.indexOf(meeple)
        require(posMeeple != -1){ "Meeple does not exist on Moonwheel"}

        val stack : MutableList<Tile> = currentState.stack
        for(i in (posMeeple+1)..(11+posMeeple)){
            val currentTile = moonwheel.tiles[i % 12]
            if(currentTile == Tile() && stack.size != 0){
                moonwheel.tiles[i % 12] = stack[0]
                stack.removeFirst()
            }
        }

        onAllRefreshables { refreshAfterRefillTiles() }
    }


    /**
     * Undoes the move of the current player by setting the current state to the previous state
     * only if he is same the current player of the previous state.
     *
     * 'choseUndo' of the currentGame is set to true.
     *
     * @exception IllegalStateException There is no currently active game.
     * @exception IllegalArgumentException
     *    if current player of current state is not the same in the previous state
     * @exception IllegalArgumentException if current state is already the oldest state
     */
    fun undo(){
        val currentGame : Game? = rootService.novaluna.game
        checkNotNull(currentGame){ "There is no currently active game."}
        val currentPlayer = currentGame.currentState.currentPlayer
        val indexCurrentState = currentGame.states.indexOf(currentGame.currentState)
        require(indexCurrentState != 0){
            "Cannot perform 'undo'. Current state is already the oldest state"
        }
        val previousState = currentGame.states[indexCurrentState-1]
        val previousPlayer : Player = previousState.currentPlayer
        require(currentPlayer == previousPlayer){
            "Cannot perform 'undo'. Player of the previous state is not equal" +
            "to the player of the current"
        }
        currentGame.choseUndo = true
        currentGame.currentState = previousState
        onAllRefreshables { refreshAfterUndo() }
    }


    /**
     * Redoes the move the current player by setting the current state to the next state
     * when undo has been used before. Only possible if the current state is not the last state.
     *
     * @exception IllegalStateException There is no currently active game.
     * @exception IllegalArgumentException if current player of current state is not the same
     * in the next state
     * @exception IllegalArgumentException if current state is already the newest state
     */
    fun redo() {
        val currentGame : Game? = rootService.novaluna.game
        checkNotNull(currentGame){ "There is no currently active game."}
        val currentPlayer = currentGame.currentState.currentPlayer
        val indexCurrentState = currentGame.states.indexOf(currentGame.currentState)
        require(indexCurrentState != (currentGame.states.size-1)){
            "Cannot perform 'redo'. Current state is already the newest state"
        }
        val nextState = currentGame.states[indexCurrentState+1]
        val nextPlayer : Player = nextState.currentPlayer
        require(currentPlayer == nextPlayer){
            "Cannot perform 'redo'. Player of the next state is not equal to the player of the current"
        }
        currentGame.currentState = nextState
        onAllRefreshables { refreshAfterRedo() }
    }


    /**
     * Manipulates [currentState] according to the following move: Place [chosenTile] on the grid position [pos] on the
     * current [Player]'s grid.
     *
     * It is checked whether the tile selection was allowed and whether [pos] is eligible. If one of the checks fails,
     * an exception is thrown.
     *
     * Then, the player position and the tile on the moonwheel are updated, the player's grid is updated and finally his
     * tokens are placed. The player's actions are protocolled.
     *
     * Note again that this is done directly on [currentState]. The [State] passed as an argument is altered in this
     * method.
     *
     * @param chosenTile The [Tile] chosen by the player.
     * @param pos The grid position where the player wants to place the tile.
     * @param currentState The [State] to be manipulated in the above specified way.
     * @exception IllegalStateException The [Moonwheel] is not consistent, either because the chosenTile is not exactly
     * once on the moonwheel or because the meeple is not exactly once on the moonwheel.
     * @exception IllegalStateException The players on the moonwheel are not consistent.
     * @exception IllegalArgumentException [pos] is not an eligible grid position.
     * @exception IllegalArgumentException [chosenTile] is not eligible for placement in the grid, either because it is
     * not a coloured tile or because it is not in the range of selectable tiles.
     */
    fun manipulateStateMakeMove(chosenTile: Tile, pos: Pair<Int, Int>, currentState: State){
        // Check pos and chosenTile for consistency:
        // Checks that pos is an eligible grid position for the current player.
        // tileSelectionAllowed checks for the consistency of chosenTile, the moonwheel at large and the selection.
        require(currentState.currentPlayer.isEligibleGridPosition(pos)) {
            "pos is not an eligible position."
        }
        require(tileSelectionAllowed(chosenTile,currentState)) {"The tile selection was not allowed."}

        // Update the player position and the tiles on the moonwheel.
        updateMoonwheelTiles(chosenTile,currentState)
        updatePlayerPosition(chosenTile,currentState)

        // Place the tile on the grid and place the tokens.
        currentState.currentPlayer.placeTileOnGrid(chosenTile,pos)
        val taskProtocol = placeTokens(currentState)

        // Protocol the move
        protocolMove(chosenTile,pos,taskProtocol,currentState)
    }

    /**
     * Method that implements the move of the current player given his selected tile and the position he wants to place
     * it.
     *
     * It is checked whether the tile selection was allowed and whether [pos] is eligible.
     * If so, the current state is cloned. All subsequent changes are performed on this clone.
     *
     * Then, the player position and the tile on the moonwheel are updated, the player's grid is updated and finally his
     * tokens are placed. The player's actions are protocolled.
     * All this is done not directly in makeMove but through the method [manipulateStateMakeMove].
     *
     * Note that this method does call neither endMove nor endGame, because the player should be able to end his move
     * separately, so that he can see and evaluate the result of his move.
     * Thus, the currentPlayer is not changed in this method.
     *
     * @param chosenTile The [Tile] chosen by the player.
     * @param pos The grid position where the player wants to place the tile.
     * @exception IllegalStateException There is no currently active game.
     * @exception IllegalStateException The [Moonwheel] is not consistent, either because the chosenTile is not exactly
     * once on the moonwheel or because the meeple is not exactly once on the moonwheel.
     * @exception IllegalStateException The players on the moonwheel are not consistent.
     * @exception IllegalArgumentException [pos] is not an eligible grid position.
     * @exception IllegalArgumentException [chosenTile] is not eligible for placement in the grid, either because it is
     * not a coloured tile or because it is not in the range of selectable tiles.
     */
    fun makeMove(chosenTile: Tile, pos: Pair<Int, Int>){
        // Get current game and check if it exists. Do not get the current player, because the state is cloned later.
        val currentGame = rootService.novaluna.game
        checkNotNull(currentGame){ "There is no currently active game."}

        // This is also done in manipulateState, but still it should be done here before anything is cloned, otherwise
        // the states are altered despite the move being not allowed.
        // Check pos and chosenTile for consistency:
        // Checks that pos is an eligible grid position for the current player.
        // tileSelectionAllowed checks for the consistency of chosenTile, the moonwheel at large and the selection.
        require(currentGame.currentState.currentPlayer.isEligibleGridPosition(pos)) {
            "pos is not an eligible position."
        }
        require(tileSelectionAllowed(chosenTile,currentGame.currentState)) {"The tile selection was not allowed."}

        // Clone the state so that the changes are done on the new state.
        cloneUpdateState()

        manipulateStateMakeMove(chosenTile,pos,currentGame.currentState)

        onAllRefreshables { refreshAfterMakeMove() }
    }

    /**
     * Simulates the placing of tokens on completed tasks. Operates on the [State] [currentState].
     *
     * It checks for every non-empty [Tile] on the player's grid, whether its tasks are completed.
     * If a task is found which is completed but not yet marked as completed, it is marked as completed and the number
     * of tokens of the player are reduced by 1.
     *
     * If all tokens are placed, meaning that the number of tokens is 0, no further checks for completed tasks are
     * performed (because they can not be marked with tokens anyway and are irrelevant anyway, because the game is
     * already won). Thus, in this case the number of tokens is 0 and some completed tasks are potentially not marked
     * as completed.
     */
    private fun placeTokens(currentState: State): List<Triple<Pair<Int,Int>,Int,Task>>{
        // Get current player.
        val currentPlayer = currentState.currentPlayer

        // Initialize the list tasks that tokens were placed upon.
        val taskProtocol = mutableListOf<Triple<Pair<Int,Int>,Int,Task>>()

        // For each tile in the grid look at each task.
        // If the player still has tokens and the task is not yet marked as completed:
        // If it is completed in the grid, mark it as completed and reduce the number of tokens by 1.
        currentPlayer.grid.forEachIndexed { x, array ->
            array.forEachIndexed { y, tile ->
                if(Type.shortType().contains(tile.type)){
                    tile.tasks.forEachIndexed { taskIndex,task ->
                        if(currentPlayer.tokens > 0 && !task.completed){
                            if(checkTaskForCompletion(task,Pair(x,y),currentState)){
                                task.completed = true
                                currentPlayer.tokens--
                                taskProtocol.add(Triple(Pair(x,y),taskIndex,task))
                            }
                        }
                    }
                }
            }
        }
        return taskProtocol
    }

    /**
     * Helper-method for [placeTokens].
     *
     * Check for a given task, which is contained in the tile at grid position pos, is completed.
     *
     * The task is not allowed to be already completed, otherwise an exception is thrown.
     *
     * The check is performed on the [State] [currentState].
     *
     * @param task The [Task] for which the completion is checked.
     * @param pos The grid position of the [Tile] belonging to the task.
     * @param currentState The [State] on which the check is performed.
     * @return True if task is (newly) completed, false if it is not completed.
     * @exception IllegalArgumentException [pos] is not a position.
     * @exception IllegalArgumentException [task] is already completed.
     * @exception IllegalArgumentException [task]  is not contained in the tile at [pos]
     * @exception IllegalStateException Errors in the clusters are found.
     */
    private fun checkTaskForCompletion(task: Task,  pos: Pair<Int,Int>, currentState: State): Boolean{
        require(Player.isPosition(pos)) {"pos is not a position, values are instead: (${pos.first},${pos.second}."}
        require(!task.completed) {"task is already completed."}

        // Get current game and check if it exists. Then get currentPlayer. Then get the tile at pos.
        val currentPlayer = currentState.currentPlayer
        val originTile = currentPlayer.grid[pos.first][pos.second]

        // Check if the originTile contains the task.
        require(originTile.tasks.contains(task)) {"task not contained in the tile at the position pos in the grid."}

        // Get the type of the originTile. Check that it is a colour.
        val originType = originTile.type
        require(Type.shortType().contains(originType)) {
            "tile at position pos in the grid is not of a colour, but instead: $originType"
        }

        // Get the colours in task without duplicates, so a list of the occurring colours.
        val taskColours = task.colours.distinct()

        // Associate each colour in task with the number of times it is required by task.
        val colourCountTask = taskColours.associateWith { colour ->
            task.colours.count {
                it == colour
            }
        }
        // Associate each colour in task with the cardinality of neighbouring clusters to originTile
        val colourCountClusters = taskColours.associateWith { colour ->
            if( colour == originType ){
                /* If the given colour is equal to originType, the only relevant cluster is the cluster containing the
                originTile. Because originTile is not counted, the value is reduced by one.*/
                val relevantCluster = currentPlayer.clusters.find {
                    it.second.contains(pos)
                }
                checkNotNull(relevantCluster) {"Error: No colour found containing pos."}
                check(relevantCluster.second.isNotEmpty()) {"Error: Empty cluster found."}
                relevantCluster.second.size - 1
            }else{
                /*
                A cluster is relevant if it has the specified colour and if it contains at least one element that is
                a neighbour of pos. The total cardinality is then the sum of the cardinality of all relevant clusters.
                 */
                currentPlayer.clusters.filter { clusterPair ->
                    clusterPair.first == colour && clusterPair.second.any {
                        it in Player.getNeighbours(pos)
                    }
                }.sumOf {
                    it.second.size
                }
            }
        }

        // Check if for each colour in task the neighbouring cluster cardinality is >= than the number required by task.
        return colourCountTask.all {
            it.value <= (colourCountClusters[it.key] ?: throw IllegalStateException())
        }
    }

    /**
     * Update the position of the currentPlayer according to the chosen tile.
     *
     * The current player is removed from his current position and added to the new position.
     *
     * The method checks also whether the currentPlayer is contained exactly once on the playing track and that at his
     * position he is indeed the first player.
     *
     * The method performs all the updates on the [State] [currentState].
     *
     * @param chosenTile The [Tile] chosen by the current player which determines the new position.
     * @param currentState The [State] on which the the update is done.
     */
    private fun updatePlayerPosition(chosenTile: Tile, currentState: State){
        // Check that the chosen tile has a colour (so not empty or meeple).
        require(Type.shortType().contains(chosenTile.type)) {
            "chosenTile is not of a colour, but is instead of type ${chosenTile.type}"
        }

        // Get the currentGame, the current player and the moonwheel.
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
        check(currentPlayerOccurences[0].indexOf(currentPlayer) == 0) {
            "Error: currentPlayer not the first player in the position list of the moonwheel."
        }

        // Calculate the new position.
        val newPosition =
            (moonwheel.playerPositions.indexOf(currentPlayerOccurences[0]) + chosenTile.cost.toString().toInt()) % 24

        // Remove the current player from his old position
        currentPlayerOccurences[0].remove(currentPlayer)

        // Add player to the positionList at the new position.
        moonwheel.playerPositions[newPosition] =
            (mutableListOf(currentPlayer) + moonwheel.playerPositions[newPosition]).toMutableList()
    }

    /**
     * Checks whether the selection of [chosenTile] was allowed in the state [currentState]
     *
     * This method provides further consistency checks regarding [chosenTile] and the [Moonwheel].
     * It is checked whether [chosenTile] is of a colour and whether it occurs exactly once on the Moonwheel.
     * It checks whether exactly on meeple occurs on the Moonwheel.
     * Finally it checks whether the positions of [chosenTile] and the meeple are so that [chosenTile] was allowed
     * to be selected.
     *
     * Also checks whether [chosenTile] and the Meelple occurre exactly once on the moonwheel.
     *
     * All these checks are done for the [State] [currentState].
     *
     * @param chosenTile [Tile] for which selection eligibility is checked.
     * @param currentState [State] on which check is performed.
     * @return true if [chosenTile] is selectable, false else.
     * @exception IllegalArgumentException [chosenTile] is not a coloured tile.
     * @exception IllegalStateException [chosenTile] not exactly once on moonwheel.
     * @exception IllegalStateException Moonmeeple not exactly once on moonwheel.
     */
    fun tileSelectionAllowed(chosenTile: Tile, currentState: State): Boolean{
        // Check that the chosen tile has a colour (so not empty or meeple).
        require(Type.shortType().contains(chosenTile.type)) {
            "chosenTile is not of a colour, but is instead of type ${chosenTile.type}"
        }

        // Get the moonwheel tiles.
        val moonwheelTiles = currentState.moonwheel.tiles

        // Check that both the meeple and the chosen tile occur exactly once on the moonwheel
        check(moonwheelTiles.count { it == chosenTile } == 1) {"chosenTile not exactly once on moonwheel."}
        check(moonwheelTiles.count { it.type == Type.MEEPLE } == 1) {"Moonmeeple not exactly once on moonwheel."}

        // Get the position of the chosenTile and the meeple.
        // Because of the above checks, they are both exactly once on the moonwheel.
        val chosenTilePosition = moonwheelTiles.indexOf(chosenTile)
        val meeplePosition = moonwheelTiles.indexOf(moonwheelTiles.first { it.type == Type.MEEPLE })

        // THIS IS THE OLD STUFF, WHICH WAS WRONG
        // Check whether the tile selection was allowed. This is the case if the chosen tile is on one of the next three
        // position from the meeple.
        /*return chosenTilePosition in setOf(
            (meeplePosition + 1) % 12, (meeplePosition + 2) % 12, (meeplePosition + 3) % 12
        )*/

        // THIS IS THE NEW VERSION
        // Check whether the tile selection was allowed.
        // This is the case if the chosen tile is on one of the next three positions from the meeple WHICH CONTAINS A
        // TILE (which was not checked above).
        // Note that at this point, there is definitely a chosenTile and a meeple on the moonwheel which are different
        // from each other.
        // To check the condition, run over the moonwheel in clockwise order starting at the meeple position (or the
        // meeplePosition+1 and count all the tiles that lie in between the meeple and the chosenTile.
        var i = (meeplePosition + 1) % 12
        var counter = 0
        while(i != chosenTilePosition && counter < 3){
            if(Type.shortType().contains(moonwheelTiles[i].type)){
                counter++
            }
            i = (i+1) % 12
        }
        return counter < 3
    }

    /**
     * This method should only be called directly after [tileSelectionAllowed]. Thus, in this method, no checks are done
     * regarding the consistency of the moonwheel or whether the selection was allowed in the first place. Also no
     * checks are done regarding the tile colour.
     *
     * The method performs all the updates on the [State] [currentState].
     */
    private fun updateMoonwheelTiles(chosenTile: Tile, currentState: State){
        // Get the moonwheel tiles.
        val moonwheelTiles = currentState.moonwheel.tiles

        // Get the position of the chosenTile and the meeple.
        val chosenTilePosition = moonwheelTiles.indexOf(chosenTile)
        val meeplePosition = moonwheelTiles.indexOf(moonwheelTiles.first { it.type == Type.MEEPLE })

        // Place the meeple at the position of chosenTile and place at the old meeplePosition an empty tile.
        moonwheelTiles[chosenTilePosition] = moonwheelTiles[meeplePosition]
        moonwheelTiles[meeplePosition] = Tile()
    }

    /**
     * Clones the currentState.The cloned state is added to the list of states and is the new currentState
     */
    private fun cloneUpdateState(){
        val currentGame = rootService.novaluna.game
        checkNotNull(currentGame){ "There is no currently active game."}
        val clonedCurrentState = currentGame.currentState.clone()
        currentGame.states.add(clonedCurrentState)
        currentGame.currentState = clonedCurrentState
    }

    /**
     * To see what the protocol should contain, see
     * https://sopra.cs.tu-dortmund.de/wiki/sopra/21d/projekt2/start
     * under KI-Tunier.
     *
     * @param chosenTile [Tile] chosen in the move.
     * @param pos Position were [chosenTile] was placed.
     * @param taskProtocol Protocol of tasks marked with tokens in this move. Each entry is a triple, with the [Pair]
     * marking the position of the tile the task is on, the [Int] marking which task it is on the tile and the [Task]
     * additionally giving the completed task for convenience.
     * @param currentState [State] for which the protocol is updated.
     */
    private fun protocolMove(
        chosenTile: Tile, pos: Pair<Int, Int>, taskProtocol: List<Triple<Pair<Int,Int>,Int,Task>>, currentState: State
    ){

        val builder = StringBuilder()
        builder.append("ID des ausgewählten Tiles:" + chosenTile.id + ",")
            .append("Position des ausgewählten Tiles:" + pos.toString() + ",")
            taskProtocol.forEachIndexed{ i, _ ->
                builder.append("Position der erfüllten Aufgabe:" +
                taskProtocol[i].first.toString() + "," +
                "Nummer des Tasks auf dem Tile" +
                taskProtocol[i].second + "," +
                "Farbe des Tasks" +
                taskProtocol[i].third.colours.toString() +
                "\n\n")
            }
            builder.appendLine()

        currentState.protocol += builder
    }
}