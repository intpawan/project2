package service

import entity.*


/**
 * [AIService] provides all actions that can NPC in NovaLuna Game perform.
 * @property rootService provides necessary access to other services, current game, state
 * We habe implemented AI using MiniMax algorithm that calculates the best combination of [Tile]
 * and suitable position for it. Tiles that will be checked are 3 [Tiles] on the [Moonwheel] that
 * [Player] can choose when it is his turn. The Algorithm works recursively.
 */

class AIService(private val rootService: RootService): AbstractRefreshingService() {

    /**
     * method to make an AI move
     * @param state for the minimax algorithm, that will provide the best option for a move
     * consisting of [Tile] to place and suitable position for it
     * @param difficulty of AI will influence the depth parameter of the minimax algorithm.
     * There are three options
     * case 1: [PlayerType.EASY] minimax algorithm will be executed with depth 3
     * case 2: [PlayerType.NORMAL] minimax algorithm will be executed with depth 6
     * case 3: [PlayerType.HARD] minimax algorithm will be executed with depth 10
     */
    fun aiMove(state: State, difficulty:PlayerType ) : Pair<Tile, Pair<Int,Int>>{


        if (difficulty== PlayerType.HARD){

            val move:Pair<Pair<Tile, Pair<Int,Int>>,Int> = minimax(state,3,-999999999,999999999,aiplayer = true)
           // rootService.playerActionService.makeMove(move.first.first, move.first.second)
            return Pair(move.first.first, move.first.second)

        }else if (difficulty== PlayerType.NORMAL){

            val move:Pair<Pair<Tile, Pair<Int,Int>>,Int> = minimax(state,2,-999999999,999999999,aiplayer = true)
            //rootService.playerActionService.makeMove(move.first.first, move.first.second)
            return Pair(move.first.first, move.first.second)

        }else if (difficulty== PlayerType.EASY){

            val move:Pair<Pair<Tile, Pair<Int,Int>>,Int> = minimax(state,1,-999999999,999999999,aiplayer = true)
           // rootService.playerActionService.makeMove(move.first.first, move.first.second)
            return Pair(move.first.first, move.first.second)

        }

       return Pair(Tile(),Pair(0,0))
    }

    /**
     * Method getAITip that gives optimal possibble [Tile] and position for this [Tile].
     * Tip will be computed by AI [PlayerType.HARD]
     * @param state
     * @return pair of [Tile] to place and suitable position for it
     */
    fun getAITip(state: State): Pair<Tile, Pair<Int,Int>>{
        val tip = minimax(state,2,-999999999,999999999,aiplayer = true)
        return Pair(tip.first.first, tip.first.second)
    }


    /**
     * additionally method isTerminal is necessary for minimax algorithm and actually is a termination condition
     * for recursion.
     * @param state that should be checked
     * @return boolean true if
     * case 1: there is no [Tiles] in stack and there is no [Tiles] on [Moonwheel]
     * case 2: current player has won a game and has no token
     * otherwise output will be false
     */

    fun isTerminal(state: State): Boolean {

        // var emptyMoonwheel : Boolean = false
        var meepelPosition  = 0
        for(i in 0 .. 11 ){

            if (state.moonwheel.tiles[i].type == Type.MEEPLE){
                meepelPosition = i
            }

        }

        var numberOfEmptyTiles = 0
        for (j in meepelPosition+1 .. meepelPosition+11){
            if(state.moonwheel.tiles[j%state.moonwheel.tiles.size].type == Type.EMPTY ){

                numberOfEmptyTiles++

            }
        }

        if(numberOfEmptyTiles == 11){
            return true

        }else if(state.currentPlayer.tokens==0){
            return true
        }
        return false

    }
    /**
     * highScore method that calculates [Score] for a winning player
     * @param game is a [Novaluna.game] for which high score has to be calculated
     * @return Name of [Player] and his score
     * case 1: if [Player] has chosen Undo then the whole game will not appear in highscore list
     * case 2: if [Player] has requested a tip then only his tokens will not be used for the calculation
     * of average score
     */
    fun highScore(game: Game):Pair<String,Double>{

        var scoreAndPlayer: Pair<String,Double> = Pair("",0.0)
        var haveWinner = false
        val meeplePosition = game.currentState.moonwheel.tiles.indexOf(
            game.currentState.moonwheel.tiles.first { it.type == Type.MEEPLE })

        var numberOfEmptyTiles = 0
        for (j in meeplePosition+1 .. meeplePosition+11){
            if(game.currentState.moonwheel.tiles[j%game.currentState.moonwheel.tiles.size].type == Type.EMPTY ){

                numberOfEmptyTiles++
            }
        }

        if(!game.choseUndo){
            for(player in game.currentState.players){
                if(player.tokens == 0 && !player.choseTip){
                    haveWinner = true
                    scoreAndPlayer= scoreAndPlayer.copy(first = player.name)

                    var a = 0.0

                    for(p in game.currentState.players){

                        a += p.tokens
                    }
                    scoreAndPlayer= scoreAndPlayer.copy(second = a/(game.currentState.players.size - 1))
                }
            }
            if (numberOfEmptyTiles == 11 && !haveWinner){

                var minPlayer = game.currentState.players[0]

                for(player in game.currentState.players) {

                    if (!player.choseTip && !game.currentState.players[0].choseTip){ minPlayer = player }
                    else { return scoreAndPlayer}

                }

                for(p in 0 until game.currentState.players.size){

                    if(game.currentState.players[p].tokens < minPlayer.tokens && !game.currentState.players[p].choseTip){

                        minPlayer = game.currentState.players[p]
                    }
                }
                scoreAndPlayer= scoreAndPlayer.copy(first = minPlayer.name)
                var b = 0.0

                for(p in game.currentState.players){

                    if (p!= minPlayer) {
                        b += p.tokens
                    }

                }
                scoreAndPlayer= scoreAndPlayer.copy(second = b/(game.currentState.players.size - 1))
            }
        }
        return scoreAndPlayer
    }

    /**
     * getValidTiles method provides array consisting of [Tiles] that are available and valid to peek.
     * According to game rules [Tiles] that available to peek are 3 [Tiles] standing next to [Type.MEEPLE]
     * @param state where the calculations take place
     * @return array of [Tiles] that current [Player] is allowed to peek
     */
    fun getValidTiles(state: State): MutableList<Tile> {
        var meeplePos: Int
        val validTiles:MutableList<Tile> = mutableListOf()
        var a = 0
        for(i in 0.. 11) {
            if (state.moonwheel.tiles[i].type == Type.MEEPLE) {
                meeplePos = i

                for (j in meeplePos+1 .. meeplePos+11){
                    if(state.moonwheel.tiles[j%state.moonwheel.tiles.size].type!= Type.MEEPLE &&
                        state.moonwheel.tiles[j%state.moonwheel.tiles.size].type!= Type.EMPTY ){

                        if(a<3) {
                            validTiles.add(state.moonwheel.tiles[j % state.moonwheel.tiles.size])
                            a++

                        }
                        else return validTiles
                    }

                }

            }
        }
        return validTiles
    }

    /**
     * getPositions method provides all positions List of Pair coordinates where current [Player]
     * can place a chosen [Tile] on his [Player.grid]
     * @param player for which valid positions list is to be calculated
     * @return posList list of all valid coordinates (x,y) for a given [Player]
     **/

    fun getPositions(player: Player): MutableList<Pair<Int,Int>> {
        val grid = player.grid
        val posList: MutableList<Pair<Int,Int>> = mutableListOf()

        grid.forEachIndexed { x, array ->
            array.forEachIndexed { y, _ ->
                if(player.isEligibleGridPosition(Pair(x,y))){

                    posList.add(Pair(x,y))
                }

            }

        }
        return posList
    }


    /**
     * evaluateNextTurn method computes score that is necessary for MiniMax algorithm.
     * @param state that is current
     * @param player for which the turn has to be evaluated
     * @return tuple where first element is a pair consisting of [Tile] and it´s positions
     * and the second element is score that will be calculated if a [Player] will place
     * this [Tile] on this position
     */

    fun evaluateNextTurn(state : State,player : Player) : Pair<Pair<Tile, Pair<Int,Int>>,Int> {

        var anzahlmin =0
        var anzahlmax =0

        if(player.playerType != PlayerType.HUMAN){

            for(p in state.players){

                if(p.tokens < player.tokens && p!=player){

                    anzahlmin ++

                }
                if(p.tokens > player.tokens && p!=player){

                    anzahlmax++

                }


            }
            return if (anzahlmin>anzahlmax ){
                Pair(Pair(Tile(),Pair(0,0)),-100)


            }else if (anzahlmin<anzahlmax ){
                Pair(Pair(Tile(),Pair(0,0)),100)


            }else Pair(Pair(Tile(),Pair(0,0)),0)


        }else {

            for(p in state.players){

                if(p.tokens < player.tokens && p!=player){

                    anzahlmin ++


                }
                if(p.tokens > player.tokens && p!=player){

                    anzahlmax++

                }

            }
            return if (anzahlmin>anzahlmax ){
                Pair(Pair(Tile(),Pair(0,0)),100)


            }else if (anzahlmin<anzahlmax ){
                Pair(Pair(Tile(),Pair(0,0)),-100)


            }else Pair(Pair(Tile(),Pair(0,0)),0)


        }

    }

    /**
     * makeAISimulation is method that performs an action (placing [Tile] on [Player.grid])
     * in state that is a copy of the current state of a [Game]
     * @param tile that has to be placed
     * @param state that has to be cloned from a current state
     * @param position tuple of coordinates
     * @return state that will be reached py performing an action
     */

    fun makeAISimulation(tile: Tile, state: State, position: Pair<Int,Int>): State {

        val clonedState = state.clone()
        val clonedTile = clonedState.moonwheel.tiles.first {
            it == tile
        }
        // On the cloned state a move is simulated, meaning that the player is advanced, the meeple is advanced and
        // the player's grid is updated, including the placement of tokens.
        // What not happened: The currentPlayer POINTER was not advanced.
        rootService.playerActionService.manipulateStateMakeMove(clonedTile,position,clonedState)
        // The currentPlayer POINTER is advanced.
        rootService.gameService.advanceCurrentPlayer(clonedState)
        // WHAT NOT HAPPENED:
        // It was not checked, wether an endGameCondition was reached. The moonwheel was also not refilled.
        return clonedState
    }


    /**
     * aiPlayer method checks whether [Player] is NPC or human
     * @param player that has to be checked
     * @return true if [Player] is AI player, false otherwise
     */

    fun aiplayer (player: Player):Boolean{
        return player.playerType != PlayerType.HUMAN
    }

    /**
     * MiniMax algorithm is a decision algorithm that is used for minimizing the possible loss of opponent
     * and maximizing gain of AI players.
     * @param state
     * @param depth that regulated depth of decision tree
     * @param aiplayer that identifies whether current player is AI or human
     * @return tupel consisting of two elements: the first one is a tuple of [Tile] and position
     * the second one is score that will be computed for placing this [Tile] on this position
     * supposed to be maximum for AI player and minimum for human player
     */

    fun minimax(state: State, depth: Int, alpha:Int ,
                beta:Int, aiplayer: Boolean ) : Pair<Pair<Tile, Pair<Int,Int>>,Int> {

        val getValidTiles: MutableList<Tile> = getValidTiles(state) // die 3 mögliche Tiles
        val validPositions: MutableList<Pair<Int,Int>> = getPositions(state.currentPlayer)
        val isTerminal: Boolean = isTerminal(state) // unsere State ist Terminal

        if (depth == 0 || isTerminal) {
            return if (isTerminal) {
                if (state.currentPlayer.tokens==0){
                    if (state.currentPlayer.playerType != PlayerType.HUMAN){
                        Pair(Pair(Tile(),Pair(0,0)),10000)// Ai Player hat kein Scheiben mehr
                    } else Pair(Pair(Tile(),Pair(0,0)),-10000)
                } else
                    Pair(Pair(Tile(),Pair(0,0)),0)  // kein Plätchen mehr in NachziehStapel

            } else {
                evaluateNextTurn(state,state.currentPlayer) // wenn depth=0 und wir sollen die Blätter auswerten
            }
        }

        var sscore = if(aiplayer(state.currentPlayer)) -999999999 else 999999999
        var pposition:Pair<Int,Int> = Pair(0,0)
        var ttile = Tile()

        //maximizing
        if (aiplayer(state.currentPlayer)) {

            for (tile in getValidTiles) {
                for(position in validPositions){

                    val aiTurn : State = makeAISimulation(tile,state,position)

                    if (aiplayer(aiTurn.currentPlayer)){

                        val newScoreAndMove = minimax(aiTurn,depth-1, alpha, beta, true)
                        if (newScoreAndMove.second > sscore){

                            ttile = tile
                            pposition = position
                            sscore = newScoreAndMove.second
                        }
                    }

                    else if (!aiplayer(aiTurn.currentPlayer)) {
                        val newScoreAndMove  = minimax(aiTurn,depth-1, alpha, beta,false)
                        if (newScoreAndMove.second > sscore){

                            ttile = tile
                            pposition = position
                            sscore = newScoreAndMove.second
                        }
                    }

                }
            }
        }
        //Minimizing
        else {
            for (tile in getValidTiles) {

                for(position in validPositions){

                    val aiTurn : State = makeAISimulation(tile,state,position)

                    if (aiplayer(aiTurn.currentPlayer)){

                        val newScoreAndMove = minimax(aiTurn,depth-1, alpha, beta,true)

                        if (newScoreAndMove.second < sscore){
                            ttile = tile
                            pposition = position
                            sscore = newScoreAndMove.second
                        }
                    }

                    if (!aiplayer(aiTurn.currentPlayer)) {
                        val newScoreAndMove  = minimax(aiTurn,depth-1, alpha, beta,false)

                        if (newScoreAndMove.second < sscore){
                            ttile = tile
                            pposition = position
                            sscore = newScoreAndMove.second
                        }

                    }
                }

            }
        }
        return   Pair(Pair(ttile,pposition), sscore)
    }

}












