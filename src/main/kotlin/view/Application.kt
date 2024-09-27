package view

import entity.*
import service.RootService
import tools.aqua.bgw.components.container.LinearLayout
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.layoutviews.GridPane
import tools.aqua.bgw.core.BoardGameApplication
import tools.aqua.bgw.util.BidirectionalMap
import tools.aqua.bgw.util.GridIteratorElement
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual
import tools.aqua.bgw.visual.SingleLayerVisual
import java.awt.Color
import java.io.File
import java.lang.NumberFormatException

/**
 * The main class of the view layer, connecting all other scenes.
 */
class Application : BoardGameApplication("Nova Luna"), Refreshable {


    val rootService = RootService(Novaluna(mutableListOf(), null))

    //BidirectionalMap to connect Tiles with their CardView
    private val cardMap: BidirectionalMap<Tile, CardView> = BidirectionalMap()

    //BidirectionalMap to connect Players with their Grids
    private var playerGridMap: BidirectionalMap<Player, GridPane<CardView>> = BidirectionalMap()

    private var playerTokenMap: BidirectionalMap<Player, CardView> = BidirectionalMap()

    //Reflects whether a grid is enlarged or not
    var isFocused = false

    //Shows whether the currentPlayer has already made a move this turn
    var madeMove = false

    private lateinit var currentTip: Pair<Tile, Pair<Int, Int>>

    //Scenes////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private val mmScene = MainMenuScene().apply {
        closeButton.onMouseClicked = { exit() }
        howToPlayLabel.onMouseClicked = { println("Öffne Spielanleitung...") }
        highscoreButton.onMouseClicked = {
            showMenuScene(highscoreScene)
            rootService.novaluna.highscores.forEach {
                highscoreScene.highscoreTable.items.add(it)
            }
        }
        newGameButton.onMouseClicked = { showMenuScene(newGameMenuScene) }
        newTournamentButton.onMouseClicked = { showMenuScene(newAITournamentScene) }
        loadGameButton.onMouseClicked = {
            rootService.ioService.loadGame()
            println("Lade Spiel!")

            addPlayers()
            addClosedCards()
            addMoonTiles()
            addTokens()
            addGridAfterLoad()

            println("Spiel gestartet mit ${rootService.novaluna.game!!.currentState.players.size} Spielern")

            refreshAllGrids()
            placeTokens()
            makeMove(rootService.novaluna.game!!.currentState.currentPlayer)

            getToGameScene()
        }
    }

    private val highscoreScene = HighscoreMenuScene().apply {
        backButton.onMouseClicked = {
            backToMainMenu()
            highscoreTable.items.clear()
        }
    }

    private val newGameMenuScene = NewGameMenuScene(rootService).apply {
        backButton.onMouseClicked = {
            reset()
            backToMainMenu()
        }
        startButton.onMouseClicked = {
            if (comPlayers == playerList.size) {
                var speed = 1
                try {
                    speed = simulationSpeedField.text.toInt()
                }
                catch (e : NumberFormatException){
                    println("Die AI-Geschwindigkeit muss eine Zahl sein")
                }
                rootService.gameService.startNewAISimulationGame(playerList, speed)
            } else {
                if (firstGameCheckBox.checked) {
                    rootService.gameService.startNewFirstGame(players = playerList)
                } else {
                    rootService.gameService.startNewGame(players = playerList)
                }
            }
        }
    }


    private val newAITournamentScene = NewAITournamentScene(rootService).apply {
        var filePath = ""
        backButton.onMouseClicked = { backToMainMenu() }
        //startButton.onMouseClicked = { rootService.gameService.startNewAITournamentGame(players = playerList)}
        loadCSVfileButton.onMouseClicked = {
            val csvFile = showFileDialog(fileChooser)
            if (!csvFile.isEmpty) {
                filePath = csvFile.get().first().toString()
                loadCSVField.text = filePath
                println(csvFile)
            }
        }
        startButton.onMouseClicked = {
            gameScene.protocolTable.isVisible = true
            gameScene.protocolTable.isDisabled = false
            rootService.gameService.startNewAITournamentGame(playerList, File(filePath))
        }
    }

    private val gameScene = GameScene(rootService).apply {
        backButton.onMouseClicked = {
            rootService.ioService.saveGame()
            println("Spiel gespeichert!")
            backToMainMenu()
        }
        tipButton.onMouseClicked = {
            val game = rootService.novaluna.game
            checkNotNull(game)
            currentTip = rootService.aiService.getAITip(game.currentState)
            tipLabel.text = createTip(currentTip)
        }
        finButton.onMouseClicked = {
            calculateScores()
            showMenuScene(gameFinishedMenuScene)
        }
        fillTilesButton.onMouseClicked = {
            val game = rootService.novaluna.game
            checkNotNull(game)
            rootService.playerActionService.refillTiles(game.currentState)
             /*if (checkTileIsNotEmpty() <= 3) {
                 System.out.print("test")
           } else {
                 rootService.playerActionService.refillTiles(game.currentState)
                 //}
                 //   }
             } */

        }
            endTurnButton.onMouseClicked = {
                rootService.gameService.endMove()
            }
            if (madeMove) {
                endTurnButton.isDisabled = false
            }
            undoButton.onMouseClicked = { rootService.playerActionService.undo() }
            redoButton.onMouseClicked = { rootService.playerActionService.redo() }
        }


    private val gameFinishedMenuScene = GameFinishedMenuScene(rootService).apply {
        backtoMenuButton.onMouseClicked = {
            reset()
            newGameMenuScene.reset()
            backToMainMenu()
        }
        quitButton.onMouseClicked = { exit() }
    }

    //Initializer///////////////////////////////////////////////////////////////////////////////////////////////////////
    init {
        rootService.addRefreshable(
            this,
            mmScene,
            gameScene,
            newGameMenuScene,
            highscoreScene,
            newAITournamentScene,
            gameFinishedMenuScene
        )
        this.showMenuScene(mmScene)
        this.showGameScene(gameScene)

        rootService.ioService.loadHighscore()
    }

    //Functions/////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun backToMainMenu() {
        this.hideMenuScene()
        this.showMenuScene(mmScene)

    }


    private fun getToGameScene() {
        this.hideMenuScene()
        this.showGameScene(gameScene)
    }

    private fun addTokens(){
        val currentGame = rootService.novaluna
        checkNotNull(currentGame)
        val game = currentGame.game
        checkNotNull(game)
        val currentState = game.currentState

        currentState.players.forEach {
            val tokenView = CardView(
                height = 40, width = 60,
                front = ImageVisual("Tokens/Token${it.playerColour.toString()}.png"),
                back = ColorVisual.TRANSPARENT
            )

            playerTokenMap.add(it, tokenView)

            gameScene.moonwheel[0].add(playerTokenMap.forward(it),0)

        }
        gameScene.moonwheel[0].forEach {
            it.showFront()
        }
    }


    //Function to connect [Tile]s with their respective graphics and add them to the stack of closedTiles
    private fun addClosedCards() {
        val currentGame = rootService.novaluna
        checkNotNull(currentGame)
        val game = currentGame.game
        checkNotNull(game)
        val currentState = game.currentState

        val tileBack = ImageVisual("Tiles/TileBack.png")

        currentState.stack.forEach { it ->
            val tileFront = ImageVisual("Tiles/ID${it.id}.png")

            val tileView = CardView(
                height = 150,
                width = 150,
                front = tileFront,
                back = tileBack
            ).apply { it.toString() }

            cardMap.add(it, tileView)
        }
        currentState.stack.forEach {
            gameScene.closedTiles.add(cardMap.forward(it))
        }
        gameScene.closedTiles.forEach { it.showBack() }
    }

    //Function to fill a grid with CardViews according to the content of the Players grid from entity
    private fun refreshGrid(grid: GridPane<CardView>) {
        val player = playerGridMap.backward(grid)

        for (i in 0..8) {
            for (j in 0..8) {
                if (grid[i, j] == null) {
                    if (player.grid[i][j].id != 0) {

                        val tempTile = player.grid[i][j]
                        tempTile.tasks.forEach {
                            it.completed = false
                        }

                        cardMap.forward(tempTile).scale = 0.26
                        grid[i, j] = cardMap.forward(tempTile)
                        grid[i, j]!!.showFront()
                    } else {
                        grid[i, j] = CardView(
                            height = 150,
                            width = 150,
                            front = ColorVisual(Color.WHITE),//ImageVisual("Tiles/ID0.png"),
                            back = ColorVisual(Color.GREEN),
                        ).apply {
                            scale = 0.26
                        }

                        grid[i, j]!!.showFront()
                    }

                } else if (player.grid[i][j].id == 0){
                    grid[i, j]!!.frontVisual = ColorVisual(Color.WHITE)
                }

            }
        }
    }

    //Refreshes all currently visible grids
    private fun refreshAllGrids() {
        refreshPlayerGridMap()
        gameScene.gridPaneList.forEach {
            refreshGrid(it)
        }
    }

    //Refreshes the linearLayouts to reflect the state of the moonWheel
    private fun refreshMoonTiles() {
        val currentGame = rootService.novaluna
        checkNotNull(currentGame) {"Kein novaluna"}
        val game = currentGame.game
        checkNotNull(game) {"Kein game"}
        val currentState = game.currentState

        if (cardMap.isNotEmpty()) {
            for (i in 0..11) {


                if (gameScene.moonTileList[i].isEmpty() && currentState.moonwheel.tiles[i].id != 0) {

                    if (gameScene.closedTiles.contains(cardMap.forward(game.currentState.moonwheel.tiles[i]))){
                        gameScene.closedTiles.remove(cardMap.forward(game.currentState.moonwheel.tiles[i]))
                    }

                    gameScene.moonTileList[i].add(cardMap.forward(currentState.moonwheel.tiles[i]))
                    gameScene.moonTileList[i].forEach { it.showFront() }
                }
            }
        }

    }

    private fun refreshPlayerGridMap() {
        val currentGame = rootService.novaluna
        checkNotNull(currentGame)
        val game = currentGame.game
        checkNotNull(game)
        val currentState = game.currentState

        val tmpBinMap: BidirectionalMap<Player, GridPane<CardView>> = BidirectionalMap()

        for (i in 0 until currentState.players.size) {
            tmpBinMap.add(currentState.players[i], gameScene.gridPaneList[i])
        }

        playerGridMap = tmpBinMap
    }

    private fun refreshPlayerTokenMap() {
        val currentGame = rootService.novaluna
        checkNotNull(currentGame)
        val game = currentGame.game
        checkNotNull(game)
        val currentState = game.currentState

        val tmpBinMap: BidirectionalMap<Player, CardView> = BidirectionalMap()
        val prevState = game.states.indexOf(currentState)

        for (i in 0 until currentState.players.size) {
            tmpBinMap.add(currentState.players[i], playerTokenMap.forward(game.states[prevState-1].players[i]))
        }

        playerTokenMap = tmpBinMap
    }

    //Fills the LinearLayouts around the moonWheel with Tiles
    private fun addMoonTiles() {
        val currentGame = rootService.novaluna
        checkNotNull(currentGame)
        val game = currentGame.game
        checkNotNull(game)
        val currentState = game.currentState

        for (i in 0..11) {
            val tile = currentState.moonwheel.tiles[i]
            val tileBack = ImageVisual("Tiles/TileBack.png")
            val tileFront = ImageVisual("Tiles/ID${tile.id}.png")
            val tileView = CardView(
                height = 150,
                width = 150,
                front = tileFront,
                back = tileBack
            ).apply { tile.toString() }

            cardMap.add(tile, tileView)
        }
        refreshMoonTiles()
    }

    //Makes it so that UI elements for all partaking players are made visible
    fun addPlayers() {
        val currentGame = rootService.novaluna
        checkNotNull(currentGame)
        val game = currentGame.game
        checkNotNull(game)
        val currentState = game.currentState

        for (i in 0 until currentState.players.size) {
            gameScene.playerNameLabelList[i].isVisible = true
            gameScene.playerNameLabelList[i].text = currentState.players[i].name

            gameScene.playerTokenLabelList[i].isVisible = true
            gameScene.playerTokenLabelList[i].text = currentState.players[i].tokens.toString()

            gameScene.gridPaneList.add(gameScene.gridPanes[i])

            gameScene.playerColorList[i].visual = when (currentState.players[i].playerColour) {
                PlayerColour.WHITE -> ColorVisual.WHITE
                PlayerColour.ORANGE -> ColorVisual(255, 119, 45)
                PlayerColour.BLUE -> ColorVisual(25, 71, 255)
                else -> ColorVisual(64, 64, 64)
            }
        }

        refreshPlayerGridMap()

        gameScene.nextPlayerLabel.text = game.currentState.currentPlayer.name + " ist dran! "

        gameScene.gridPaneList.forEach {
            it.isVisible = true
        }
    }

    //Fulfills a makeMove cycle
    private fun makeMove(currentPlayer: Player) {
        refreshPlayerGridMap()
        val state = rootService.novaluna.game?.currentState
        checkNotNull(state)
        val meeple = Tile(Type.MEEPLE, Cost.ZERO, mutableListOf(), 69)
        val meeplePos = state.moonwheel.tiles.indexOf(meeple)
        val playerGrid = playerGridMap.forward(currentPlayer)

        //Check whether current player is human or ai player
        if (currentPlayer.playerType == PlayerType.HUMAN) {
            //Restricts access to the first three tiles after the meeple
            for (i in 0..11) {
                //Gives the Player the ability to choose a Tile, saves it and removes it from the moonWheel and moves the meeple
                if (i != meeplePos && state.moonwheel.tiles[i].id != 0 &&
                    rootService.playerActionService.tileSelectionAllowed(state.moonwheel.tiles[i], state)
                ) {
                    gameScene.moonTileList[i].forEach {
                        it.onMouseClicked = { _ ->
                            if (!isFocused) {
                                val tmp = cardMap.backward(it)
                                it.removeFromParent()

                                gameScene.moonTileList[meeplePos].forEach { cv ->
                                    cv.removeFromParent()
                                }

                                //Saves the values of the grid and enlarges it
                                val oldPosX = playerGrid.posX
                                val oldPosY = playerGrid.posY
                                val oldScale = playerGrid.scale

                                playerGrid.scale = 2.5
                                playerGrid.posX = 780.0
                                playerGrid.posY = 360.0

                                isFocused = true

                                //If grid is enlarged, the player can choose a tile from their grid
                                if (isFocused) {
                                    playerGrid.forEach { cell ->
                                        checkNotNull(cell.component)

                                        if (currentPlayer.isEligibleGridPosition(Pair(cell.columnIndex, cell.rowIndex))){
                                            cell.component!!.frontVisual = ColorVisual(250, 212, 57)
                                        }

                                        cell.component!!.onMouseClicked = { _ ->
                                            if (currentPlayer.isEligibleGridPosition(Pair(cell.columnIndex, cell.rowIndex))) {
                                                isFocused = false
                                                //On click on a tile the grid restores its size and replaces it with the saved tile
                                                playerGrid.scale = oldScale
                                                playerGrid.posX = oldPosX
                                                playerGrid.posY = oldPosY

                                                val coordinates = Pair(cell.columnIndex, cell.rowIndex)
                                                cell.component!!.removeFromParent()

                                                println("Zeile: ${coordinates.first}, Spalte: ${coordinates.second}")

                                                //Makes the move, so all partaking datastructures get updated
                                                println("Sollte Menschlicher Spieler sein")
                                                println("Ist ${currentPlayer.toString()}")
                                                println("Tile: $tmp, Koordinaten: $coordinates")
                                                println("Grid: ${playerGrid.name}")
                                                rootService.playerActionService.makeMove(tmp, coordinates)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //Colors the Tiles you can not choose from to make seeing which moves are allowed easier
                } else if (i != meeplePos && state.moonwheel.tiles[i].id != 0) {
                    gameScene.moonTileList[i].forEach {
                        it.frontVisual =
                            CompoundVisual(it.frontVisual as SingleLayerVisual, ColorVisual(58, 58, 58, 200))
                        it.onMouseClicked = {}
                    }
                }
            }
        } else {
            //TODO(KI Spieler eingaben hinzufügen)
            val aiMove = rootService.aiService.aiMove(state, currentPlayer.playerType)
            println("Tile: ${aiMove.first.toString()}, Position: ${aiMove.second.toString()}")

            gameScene.moonTileList[meeplePos].forEach {
                it.removeFromParent()
            }

            val tempTile = aiMove.first
            tempTile.tasks.forEach {
                it.completed = false
            }

            gameScene.moonTileList.forEach {
                if (it.contains(cardMap.forward(aiMove.first))){
                    it.clear()
                }
            }

            playerGrid.forEach {
                if (it.columnIndex == aiMove.second.first && it.rowIndex == aiMove.second.second){
                    it.component!!.removeFromParent()
                }
            }
            rootService.playerActionService.makeMove(aiMove.first, aiMove.second)
        }
    }

    private fun refreshMoonWheelPos(){
        refreshPlayerTokenMap()
        val currentGame = rootService.novaluna
        checkNotNull(currentGame)
        val game = currentGame.game
        checkNotNull(game)
        val currentState = game.currentState

        gameScene.moonwheel.forEach {
            it.clear()
        }

        gameScene.moonwheel.forEachIndexed { i, it ->
            if (currentState.moonwheel.playerPositions[i].isNotEmpty()){
                currentState.moonwheel.playerPositions[i].forEach { player ->
                    it.add(playerTokenMap.forward(player), 0)
                }
            }
        }
    }


    fun UndoMove() {
        val currentGame: Game? = rootService.novaluna.game
        checkNotNull(currentGame)
        val currentPlayer = currentGame!!.currentState.currentPlayer
        val playerGrid = playerGridMap.forward(currentPlayer)
        val indexCurrentState = currentGame.states.indexOf(currentGame.currentState)
        val currentState = currentGame.currentState

        if (currentPlayer.playerType == PlayerType.HUMAN) {
            if (madeMove)
            if (indexCurrentState != 0) {
                if(currentState == currentGame.states[indexCurrentState-1])

                 playerGrid.forEach { cell ->
                    checkNotNull(cell.component)

                     cell.component!!.removeFromParent()
                     rootService.playerActionService.undo()
                     refreshMoonTiles()
                     /*
                      * Idee: Lösche das Tile aus dem Grid über die Koordinaten und
                      * setze diese dann neu wo es besser passt
                      */

                }
            }
        }

    }

    fun redoMove() {
        val currentGame: Game? = rootService.novaluna.game
        checkNotNull(currentGame)
        val currentPlayer = currentGame!!.currentState.currentPlayer
        val playerGrid = playerGridMap.forward(currentPlayer)
        var indexCurrentState = currentGame.states.first()

        if (currentGame.states.size != 0) {
            indexCurrentState = currentGame.states.component2()
            playerGrid.forEach { it ->
                it.component!!.removeFromParent()




            } /*Idee mit dem zweiten Index sollte man auf den vorherigen State kommen? und dieser graphisch dargestellt,
                 das dieser über die coordinates in das Grid gesetzt wird*/
        }
    }

    private fun calculateScores(){
        val currentGame = rootService.novaluna
        checkNotNull(currentGame)
        val game = currentGame.game
        checkNotNull(game)
        val currentState = game.currentState

        val scores = mutableListOf<Score>()
        val playerList: MutableList<Player> = currentState.players

        for(i in 0 until currentState.players.size) {
            val player = playerList.minByOrNull { it -> it.tokens }
            checkNotNull(player)
            scores.add(Score(player.tokens.toDouble(), player.name))
            playerList.remove(player)
        }

        for (i in 0 until scores.size){
            gameFinishedMenuScene.playerLabelList[i].isVisible = true
            gameFinishedMenuScene.playerNameList[i].isVisible = true
            gameFinishedMenuScene.playerScoreList[i].isVisible = true

            gameFinishedMenuScene.playerNameList[i].text = scores[i].winner
            gameFinishedMenuScene.playerScoreList[i].text = scores[i].avgScore.toString()
        }
    }

    private fun createTip(placement: Pair<Tile, Pair<Int, Int>>): String{
        val spalte = when (placement.second.first){
            0 -> "A"
            1 -> "B"
            2 -> "C"
            3 -> "D"
            4 -> "E"
            5 -> "F"
            6 -> "G"
            7 -> "H"
            else -> "I"
        }
        val zeile = (placement.second.second+1).toString()
        val farbe = when (placement.first.type.toString()){
            "CYAN" -> "cyane"
            "YELLOW" -> "gelbe"
            "RED" -> "rote"
            else -> "blaue"
        }
        val aufgaben = placement.first.tasks.size.toString()
        val kosten = placement.first.cost.toString()

        val tipp = "Wähle das $farbe Tile mit $aufgaben Aufgabe(n) und Kosten $kosten und platziere es auf das Feld $spalte$zeile."

        return tipp
    }

    private fun placeTokens(){
        val currentGame = rootService.novaluna
        checkNotNull(currentGame)
        val game = currentGame.game
        checkNotNull(game)
        val currentState = game.currentState

        currentState.players.forEach {
            val color = it.playerColour.toString()
            playerGridMap.forward(it).forEach { cell ->
                val cv = cell.component
                checkNotNull(cv)
                val serviceTile = it.grid[cell.columnIndex][cell.rowIndex]
                val tasksComplete = mutableListOf(0, 0, 0)

                serviceTile.tasks.forEachIndexed { i, task ->
                    if (task.completed){
                        tasksComplete[i] = 1
                    }
                }

                if (tasksComplete.contains(1)){
                    cv.frontVisual = CompoundVisual(ImageVisual("Tiles/ID${serviceTile.id}.png"),
                        ImageVisual(
                            "Tokens/Token$color${tasksComplete[0]}${tasksComplete[1]}${tasksComplete[2]}.png"))
                }
            }
        }
    }

    private fun addGridAfterLoad(){
        val currentGame = rootService.novaluna
        checkNotNull(currentGame)
        val game = currentGame.game
        checkNotNull(game)
        val currentState = game.currentState

        currentState.players.forEach {
            it.grid.forEach { array ->
                array.forEach { tile ->
                    if (tile.id != 0){
                        val tileView = CardView(
                            height = 150,
                            width = 150,
                            front = ImageVisual("Tiles/ID${tile.id}.png"),
                            back = ImageVisual("Tiles/TileBack.png")
                        ).apply {
                            tile.toString()
                            scale = 0.26
                        }

                        cardMap.add(tile, tileView)
                    }
                }
            }
        }
    }

    fun checkTileIsNotEmpty() : Int
    {
        var count =0
        if (!(rootService.novaluna.game!!.currentState.moonwheel.tiles.isEmpty()))
        {
            count++
        }
        return count
    }

    override fun refreshAfterRedo() {
        gameScene.resetAfterDo()
        refreshPlayerGridMap()
        refreshAllGrids()
        refreshMoonTiles()
        refreshPlayerTokenMap()
         addMoonTiles()
    }


    override fun refreshAfterStartNewGame() {
        hideMenuScene()
        addPlayers()
        addClosedCards()
        addMoonTiles()
        addTokens()

        println("Spiel gestartet mit ${rootService.novaluna.game!!.currentState.players.size} Spielern")

        refreshAllGrids()
        placeTokens()
        makeMove(rootService.novaluna.game!!.currentState.currentPlayer)
    }

    override fun refreshAfterMakeMove() {
        madeMove = true
        refreshPlayerGridMap()
        refreshAllGrids()
        refreshMoonTiles()
        refreshMoonWheelPos()
    }

    override fun refreshAfterUndo() {
        gameScene.resetAfterDo()
        refreshPlayerGridMap()
        refreshAllGrids()
        //refreshMoonTiles()
        //refreshPlayerTokenMap()
        //addMoonTiles()


        /*val currentGame = rootService.novaluna
        checkNotNull(currentGame)
        val game = currentGame.game
        checkNotNull(game)
        madeMove = false
        gameScene.tipLabel.text = ""

        refreshPlayerGridMap()

        game.currentState.players.forEachIndexed { i, player ->
            gameScene.playerTokenLabelList[i].text = player.tokens.toString()
        }
        placeTokens()
        refreshMoonTiles()
        refreshMoonWheelPos()
        refreshAllGrids()



        gameScene.nextPlayerLabel.text = game.currentState.currentPlayer.name + " ist dran! "

        gameScene.protocolTable.items.clear()
        gameScene.protocolTable.items.add(game.currentState.protocol)

        makeMove(game.currentState.currentPlayer)

         */



    }


    override fun refreshAfterEndMove() {
        val currentGame = rootService.novaluna
        checkNotNull(currentGame)
        val game = currentGame.game
        checkNotNull(game)
        madeMove = false
        gameScene.tipLabel.text = ""

        refreshPlayerGridMap()

        game.currentState.players.forEachIndexed { i, player ->
            gameScene.playerTokenLabelList[i].text = player.tokens.toString()
        }
        placeTokens()


        gameScene.nextPlayerLabel.text = game.currentState.currentPlayer.name + " ist dran! "

        //Returns the moonWheel tiles to their normal colorscheme after a players turn has ended
        val meeple = Tile(Type.MEEPLE, Cost.ZERO, mutableListOf(), 69)
        for (i in 0..11) {
            if (i != game.currentState.moonwheel.tiles.indexOf(meeple) && game.currentState.moonwheel.tiles[i].id != 0) {
                gameScene.moonTileList[i].forEach {
                    it.frontVisual = ImageVisual("Tiles/ID${cardMap.backward(it).id}.png")
                }
            }
        }
        gameScene.protocolTable.items.clear()
        gameScene.protocolTable.items.add(game.currentState.protocol)

        makeMove(game.currentState.currentPlayer)
    }

    override fun refreshAfterRefillTiles() {
        refreshMoonTiles()
    }

    override fun refreshAfterEndGame() {
        val game = rootService.novaluna.game
        checkNotNull(game)

        calculateScores()

        this.showMenuScene(gameFinishedMenuScene)
    }

    override fun refreshAfterReset() {
        this.hideMenuScene()
        rootService.gameService.reset()
        gameScene.reset()
        newGameMenuScene.reset()
        gameFinishedMenuScene.reset()
        newAITournamentScene.reset()
        this.showMenuScene(mmScene)
    }




}

