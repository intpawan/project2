package view

import entity.Novaluna
import entity.PlayerColour
import entity.PlayerType
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color
import service.RootService
import tools.aqua.bgw.visual.ColorVisual

/**
 * Scene to adjust game settings. Number of Players, Playernames, AI difficulty, etc.
 */
class NewGameMenuScene(private val rootService: RootService) : MenuScene(1920, 1080), Refreshable {

    private var players = 0
    private var readyPlayersChosen = 0
    var comPlayers = 0
    private var humPlayers = 0

    private var playerOneButtonState = "noplayer"
    private var playerTwoButtonState = "noplayer"
    private var playerThreeButtonState = "noplayer"
    private var playerFourButtonState = "noplayer"

    var playerList = mutableListOf<Triple<String, PlayerColour, PlayerType>>()

    private var playerColors = mutableListOf<PlayerColour>(
        PlayerColour.BLUE,
        PlayerColour.BLACK,
        PlayerColour.ORANGE,
        PlayerColour.WHITE
    )

    private var difficulties = mutableListOf<PlayerType>(PlayerType.EASY, PlayerType.NORMAL, PlayerType.HARD)



    //Player 1//////////////////////////////////////////////////////////////////////////////////////////////////////////
    private val player1Button = Button(
        width = 300, height = 300,
        posX = 180, posY = 100
    ).apply {
        visual = ImageVisual("NoPlayerLogo.png")
        onMouseClicked = {
            when (playerOneButtonState) {
                "noplayer" -> {
                    playerOneButtonState = "humanplayer"
                }
                "humanplayer" -> {
                    playerOneButtonState = "aiplayer"
                }
                "aiplayer" -> {
                    playerOneButtonState = "noplayer"
                }
            }
            changePlayerState(
                playerOneButtonState,
                this,
                player1Name,
                player1Color,
                player1Difficulty,
                player1ConfirmButton
            )
        }
    }

    private val player1Name: TextField = TextField(
        width = 290, height = 35,
        posX = 180, posY = 430,
        prompt = "Name eingeben"
    ).apply {
        isVisible = false
        isDisabled = true
        font = Font(20, Color(142, 142, 142))

    }

    private val player1Color = ComboBox<PlayerColour>(
        width = 290, height = 35,
        posX = 180, posY = 480,
        prompt = "Wähle eine Farbe",
        font = Font(20, Color(250, 212, 57))
    ).apply {
        isVisible = false
        isDisabled = true
        items = playerColors
        onMouseClicked.apply {orderofColor()}
    }

    private val player1Difficulty = ComboBox<PlayerType>(
        width = 290, height = 35,
        posX = 180, posY = 530,
        prompt = "Wähle eine Schwierigkeit",
        font = Font(20, Color(250, 212, 57))
    ).apply {
        isVisible = false
        isDisabled = true
        items = difficulties
    }

    private val player1ConfirmButton: Button = Button(
        width = 290, height = 35,
        posX = 180, posY = 580,
        text = "Bereit?"
    ).apply {
        isVisible = false
        isDisabled = true
        font = Font(20, Color(142, 142, 142))
        visual = ImageVisual("ButtonFrame.png")
        onMouseClicked = {
            confirmBehaviour(this, player1Button, player1Name, player1Color, player1Difficulty, playerOneButtonState)

        }
    }

    //Player 2//////////////////////////////////////////////////////////////////////////////////////////////////////////
    private val player2Button = Button(
        width = 300, height = 300,
        posX = 600, posY = 100
    ).apply {
        visual = ImageVisual("NoPlayerLogo.png")
        onMouseClicked = {
            when (playerTwoButtonState) {
                "noplayer" -> {
                    playerTwoButtonState = "humanplayer"
                }
                "humanplayer" -> {
                    playerTwoButtonState = "aiplayer"
                }
                "aiplayer" -> {
                    playerTwoButtonState = "noplayer"
                }
            }
            changePlayerState(
                playerTwoButtonState,
                this,
                player2Name,
                player2Color,
                player2Difficulty,
                player2ConfirmButton
            )
        }
    }

    private val player2Name: TextField = TextField(
        width = 290, height = 35,
        posX = 600, posY = 430,
        prompt = "Name eingeben"
    ).apply {
        isVisible = false
        isDisabled = true
        font = Font(20, Color(142, 142, 142))
    }

    private val player2Color = ComboBox<PlayerColour>(
        width = 290, height = 35,
        posX = 600, posY = 480,
        prompt = "Wähle eine Farbe",
        font = Font(20, Color(250, 212, 57))
    ).apply {
        isVisible = false
        isDisabled = true
        items = playerColors

    }

    private val player2Difficulty = ComboBox<PlayerType>(
        width = 290, height = 35,
        posX = 600, posY = 530,
        prompt = "Wähle eine Schwierigkeit",
        font = Font(20, Color(250, 212, 57))
    ).apply {
        isVisible = false
        isDisabled = true
        items = difficulties
    }
    private val player2ConfirmButton: Button = Button(
        width = 290, height = 35,
        posX = 600, posY = 580,
        text = "Bereit?"
    ).apply {
        isVisible = false
        isDisabled = true
        font = Font(20, Color(142, 142, 142))
        visual = ImageVisual("ButtonFrame.png")
        onMouseClicked = {
            confirmBehaviour(this, player2Button, player2Name, player2Color, player2Difficulty, playerTwoButtonState)

        }
    }

    //Player 3//////////////////////////////////////////////////////////////////////////////////////////////////////////
    private val player3Button = Button(
        width = 300, height = 300,
        posX = 1020, posY = 100
    ).apply {
        visual = ImageVisual("NoPlayerLogo.png")
        onMouseClicked = {
            when (playerThreeButtonState) {
                "noplayer" -> {
                    playerThreeButtonState = "humanplayer"
                }
                "humanplayer" -> {
                    playerThreeButtonState = "aiplayer"
                }
                "aiplayer" -> {
                    playerThreeButtonState = "noplayer"
                }
            }
            changePlayerState(
                playerThreeButtonState,
                this,
                player3Name,
                player3Color,
                player3Difficulty,
                player3ConfirmButton
            )
        }
    }

    private val player3Name: TextField = TextField(
        width = 290, height = 35,
        posX = 1020, posY = 430,
        prompt = "Name eingeben"
    ).apply {
        isVisible = false
        isDisabled = true
        font = Font(20, Color(142, 142, 142))
    }

    private val player3Color = ComboBox<PlayerColour>(
        width = 290, height = 35,
        posX = 1020, posY = 480,
        prompt = "Wähle eine Farbe",
        font = Font(20, Color(250, 212, 57))
    ).apply {
        isVisible = false
        isDisabled = true
        items = playerColors
    }

    private val player3Difficulty = ComboBox<PlayerType>(
        width = 290, height = 35,
        posX = 1020, posY = 530,
        prompt = "Wähle eine Schwierigkeit",
        font = Font(20, Color(250, 212, 57))
    ).apply {
        isVisible = false
        isDisabled = true
        items = difficulties
    }
    private val player3ConfirmButton: Button = Button(
        width = 290, height = 35,
        posX = 1020, posY = 580,
        text = "Bereit?"
    ).apply {
        isVisible = false
        isDisabled = true
        font = Font(20, Color(142, 142, 142))
        visual = ImageVisual("ButtonFrame.png")
        onMouseClicked = {
            confirmBehaviour(this, player3Button, player3Name, player3Color, player3Difficulty, playerThreeButtonState)
        }
    }

    //Player 4//////////////////////////////////////////////////////////////////////////////////////////////////////////
    private val player4Button = Button(
        width = 300, height = 300,
        posX = 1440, posY = 100
    ).apply {
        visual = ImageVisual("NoPlayerLogo.png")
        onMouseClicked = {
            when (playerFourButtonState) {
                "noplayer" -> {
                    playerFourButtonState = "humanplayer"
                }
                "humanplayer" -> {
                    playerFourButtonState = "aiplayer"
                }
                "aiplayer" -> {
                    playerFourButtonState = "noplayer"
                }
            }
            changePlayerState(
                playerFourButtonState,
                this,
                player4Name,
                player4Color,
                player4Difficulty,
                player4ConfirmButton
            )
        }
    }

    private val player4Name: TextField = TextField(
        width = 290, height = 35,
        posX = 1440, posY = 430,
        prompt = "Name eingeben",
    ).apply {
        isVisible = false
        isDisabled = true
        font = Font(20, Color(142, 142, 142))
    }

    private val player4Color = ComboBox<PlayerColour>(
        width = 290, height = 35,
        posX = 1440, posY = 480,
        prompt = "Wähle eine Farbe",
        font = Font(20, Color(250, 212, 57))
    ).apply {
        isVisible = false
        isDisabled = true
        items = playerColors
    }

    private val player4Difficulty = ComboBox<PlayerType>(
        width = 290, height = 35,
        posX = 1440, posY = 530,
        prompt = "Wähle eine Schwierigkeit",
        font = Font(20, Color(250, 212, 57))
    ).apply {
        isVisible = false
        isDisabled = true
        items = difficulties
    }
    private val player4ConfirmButton: Button = Button(
        width = 290, height = 35,
        posX = 1440, posY = 580,
        text = "Bereit?"
    ).apply {
        isVisible = false
        isDisabled = true
        font = Font(20, Color(142, 142, 142))
        visual = ImageVisual("ButtonFrame.png")
        onMouseClicked = {
            confirmBehaviour(this, player4Button, player4Name, player4Color, player4Difficulty, playerFourButtonState)
        }
    }

    //Menüfunktionen////////////////////////////////////////////////////////////////////////////////////////////////////
    val startButton = Button(
        width = 290, height = 75,
        posX = 815, posY = 785,
        text = "Spiel starten"
    ).apply {
        font = Font(20, Color(142, 142, 142))
        visual = ImageVisual("ButtonFrame.png")
        onMouseEntered = {
            visual = ImageVisual("ButtonFrameActivated.png")
            font = Font(20, Color(250, 212, 57))
        }
        onMouseExited = {
            font = Font(20, Color(142, 142, 142))
            visual = ImageVisual("ButtonFrame.png")
        }
        onMouseClicked = {

        }

    }

    val backButton = Button(
        width = 65, height = 65,
        posX = 30, posY = 30
    ).apply {
        visual = ImageVisual("BackButton.png")
        onMouseEntered = {
            visual = ImageVisual("BackButtonActivated.png")
        }
        onMouseExited = {
            visual = ImageVisual("BackButton.png")
        }
    }

    private val firstGameLabel = Label(
        width = 210, height = 45,
        posX = 100, posY = 785,
        text = "Erstes Spiel:"
    ).apply {
        font = Font(20, Color(250, 212, 57))
    }

    val firstGameCheckBox = CheckBox(
        posX = 320, posY = 785,
        height = 45, width = 45
    ).apply {
        scale = 1.5

    }

    private val simulationSpeedLabel = Label(
        width = 480, height = 45,
        posX = 100, posY = 855,
        text = "Simulationsgeschwindigkeit:",
        alignment = Alignment.CENTER_LEFT
    ).apply {
        font = Font(20, Color(250, 212, 57))
        isVisible = false
    }

    val simulationSpeedField: TextField = TextField(
        width = 90, height = 45,
        posX = 570, posY = 855,
        prompt = "In s"
    ).apply {
        font = Font(25, Color(142, 142, 142))
        isVisible = false
        isDisabled = true
    }

    private val errorLabel = Label(
        width = 600, height = 45,
        posX = 660, posY = 720
    ).apply {
        font = Font(25, Color(225, 0, 0))

    }

    //Spieler Reihenfolge///////////////////////////////////////////////////////////////////////////////////////////////
    private val order = Label(
        width = 260, height = 45,
        posX = 1420, posY = 710,
        text = "Reihenfolge"
    ).apply {
        font = Font(20, Color(250, 212, 57))
    }

    private val diceButton = Button(
        width = 50, height = 50,
        posX = 1680, posY = 708
    ).apply {
        visual = ImageVisual("dice.png")
        onMouseClicked = {
            playerList.shuffle()
            when(players){
                1 -> firstButton.text = playerList.first().first
                2 -> {firstButton.text = playerList.first().first
                    secondButton.text = playerList.elementAt(1).first}
                3 -> {firstButton.text = playerList.first().first
                    secondButton.text = playerList.elementAt(1).first
                    thirdButton.text = playerList.elementAt(2).first}
                4 -> {firstButton.text = playerList.first().first
                    secondButton.text = playerList.elementAt(1).first
                    thirdButton.text = playerList.elementAt(2).first
                    fourthButton.text = playerList.elementAt(3).first}

                else -> errorLabel.text = "Kein Spieler bereit"
            }
        }
    }

    private val firstButton = Button(
        width = 300, height = 40,
        posX = 1400, posY = 785
    ).apply {
        isVisible = false
        isDisabled = true
        font = Font(20, Color(142, 142, 142))
        visual = ImageVisual("ButtonFrame.png")
        onMouseClicked = {
            orderBehaviour(this)
        }
    }

    private val secondButton = Button(
        width = 300, height = 40,
        posX = 1400, posY = 854
    ).apply {
        isVisible = false
        isDisabled = true
        font = Font(20, Color(142, 142, 142))
        visual = ImageVisual("ButtonFrame.png")
        onMouseClicked = {
            orderBehaviour(this)
        }
    }

    private val thirdButton = Button(
        width = 300, height = 40,
        posX = 1400, posY = 924
    ).apply {
        isVisible = false
        isDisabled = true
        font = Font(20, Color(142, 142, 142))
        visual = ImageVisual("ButtonFrame.png")
        onMouseClicked = {
            orderBehaviour(this)
        }
    }

    private val fourthButton = Button(
        width = 300, height = 40,
        posX = 1400, posY = 994
    ).apply {
        isVisible = false
        isDisabled = true
        font = Font(20, Color(142, 142, 142))
        visual = ImageVisual("ButtonFrame.png")
        onMouseClicked = {
            orderBehaviour(this)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private fun changePlayerState(
        player: String,
        playerButton: Button,
        nameField: TextField,
        colorBox: ComboBox<PlayerColour>,
        diffBox: ComboBox<PlayerType>,
        conf: Button
    ) {
        if (player == "noplayer") {
            comPlayers--
            players--
            humPlayers--
            playerButton.visual = ImageVisual("NoPlayerLogo.png")

            nameField.isDisabled = true
            nameField.isVisible = false

            colorBox.isDisabled = true
            colorBox.isVisible = false

            diffBox.isDisabled = true
            diffBox.isVisible = false

            conf.isDisabled = true
            conf.isVisible = false
        } else if (player == "humanplayer") {
            players++
            humPlayers++
            playerButton.visual = ImageVisual("HumanPlayerLogo.png")

            nameField.text = ""
            nameField.isDisabled = false
            nameField.isVisible = true

            colorBox.isDisabled = false
            colorBox.isVisible = true

            conf.isDisabled = false
            conf.isVisible = true
        } else if (player == "aiplayer") {
            comPlayers++
            humPlayers--
            playerButton.visual = ImageVisual("AIPlayerLogo.png")

            nameField.text = "COM$comPlayers"
            nameField.isDisabled = true

            diffBox.isDisabled = false
            diffBox.isVisible = true


        }
        if (comPlayers > 0 && humPlayers  <= 0) {
            simulationSpeedLabel.isVisible = true
            simulationSpeedField.isDisabled = false
            simulationSpeedField.isVisible = true
        } else {
            simulationSpeedLabel.isVisible = false
            simulationSpeedField.isDisabled = true
            simulationSpeedField.isVisible = false
        }
        playerOrderBoxes()
    }

    private fun playerOrderBoxes() {
        when (players) {
            0 -> {
                firstButton.isDisabled = true
                firstButton.isVisible = false
            }
            1 -> {
                firstButton.isDisabled = false
                firstButton.isVisible = true

                secondButton.isDisabled = true
                secondButton.isVisible = false
            }
            2 -> {
                secondButton.isDisabled = false
                secondButton.isVisible = true

                thirdButton.isDisabled = true
                thirdButton.isVisible = false
            }
            3 -> {
                thirdButton.isDisabled = false
                thirdButton.isVisible = true

                fourthButton.isDisabled = true
                fourthButton.isVisible = false
            }
            4 -> {
                fourthButton.isDisabled = false
                fourthButton.isVisible = true
            }
        }
    }

    private fun confirmBehaviour(
        conf: Button,
        playerButton: Button,
        name: TextField,
        color: ComboBox<PlayerColour>,
        difficulty: ComboBox<PlayerType>,
        state: String
    ) {
        if (conf.text == "Bereit?") {
            confirm(conf, playerButton, name, color, difficulty)
        } else {
            unconfirm(conf, playerButton, name, color, difficulty, state)
        }
    }

    private fun confirm(
        conf: Button,
        playerButton: Button,
        name: TextField,
        color: ComboBox<PlayerColour>,
        difficulty: ComboBox<PlayerType>
    ) {
        if (name.text != "" && color.selectedItem != null && (difficulty.selectedItem != null && !difficulty.isDisabled || difficulty.isDisabled)) {
            var diff = difficulty.selectedItem
            conf.visual = ImageVisual("ConfirmButton.png")
            conf.text = "BEREIT!"
            conf.font = Font(20, Color(78, 242, 0))

            if (difficulty.isDisabled) {
                diff = PlayerType.HUMAN
            }




            playerButton.isDisabled = true
            name.isDisabled = true
            color.isDisabled = true
            difficulty.isDisabled = true


            if (checkNames() || orderofColor()) {
                errorLabel.text = "Namen oder Farben nicht mehrmals verwenden!"
                startButton.isDisabled=true
            } else {
                errorLabel.text=""
                startButton.isVisible=true
                startButton.isDisabled=false
            }

            /*if(orderofColor())
            {
                errorLabel.text="Es dürfen nicht zwei mal dieselben Namen oder Farben vorkommen,Bitte ändern!"
                startButton.isDisabled=true
            }
            else
            {
                errorLabel.text=""
                startButton.isVisible=true
                startButton.isDisabled=false
            } */

            playerList.add(Triple(name.text, color.selectedItem, diff) as Triple<String, PlayerColour, PlayerType>)


        } else {
            errorLabel.text = "Der Spieler wurde nicht vollständig konfiguriert!"
        }
    }

    private fun unconfirm(
        conf: Button,
        playerButton: Button,
        name: TextField,
        color: ComboBox<PlayerColour>,
        difficulty: ComboBox<PlayerType>,
        state: String
    ) {
        var isAI = false
        var diff = PlayerType.HUMAN

        conf.font = Font(20, Color(142, 142, 142))
        conf.visual = ImageVisual("ButtonFrame.png")
        conf.text = "Bereit?"
        playerButton.isDisabled = false
        name.isDisabled = false
        color.isDisabled = false

        if (state == "aiplayer") {
            difficulty.isDisabled = false
            isAI = true
        }
        if (isAI) {
            diff = difficulty.selectedItem!!
        }
        playerList.remove(Triple(name.text, color.selectedItem, diff))
    }

    /**
     * function the change the order of the players.
     */
    private fun orderBehaviour(button: Button) {
        if (playerList.size > 0) {
            button.text = playerList[readyPlayersChosen].first
            if (readyPlayersChosen == playerList.size - 1 ) {
                readyPlayersChosen = 0
            } else {
                readyPlayersChosen++
            }
            errorLabel.text = ""
        } else {
            errorLabel.text = "Es ist noch keiner der Spieler bereit!"
        }
    }

    init {
        background = ColorVisual(47, 47, 47)
        opacity = 1.0
        addComponents(
            backButton, startButton, player1Button, player2Button, player3Button, player4Button,
            player1Name, player2Name, player3Name, player4Name, player1Color, player2Color, player3Color, player4Color,
            simulationSpeedLabel, firstGameCheckBox, simulationSpeedField,
            player1Difficulty, player2Difficulty, player3Difficulty, player4Difficulty,
            order, diceButton, firstGameLabel,
            firstButton, secondButton, thirdButton, fourthButton,
            player1ConfirmButton, player2ConfirmButton, player3ConfirmButton, player4ConfirmButton, errorLabel

        )

    }

    fun reset()
    {
        player1Name.text=""
        player2Name.text=""
        player3Name.text=""
        player4Name.text=""

        player1Color.isVisible=false
        player2Color.isVisible=false
        player3Color.isVisible=false
        player4Color.isVisible=false

        player1Difficulty.isVisible=false
        player2Difficulty.isVisible=false
        player3Difficulty.isVisible=false
        player4Difficulty.isVisible=false

        player1Button.visual= ImageVisual("NoPlayerLogo.png")
        player2Button.visual = ImageVisual("NoPlayerLogo.png")
        player3Button.visual = ImageVisual("NoPlayerLogo.png")
        player4Button.visual = ImageVisual("NoPlayerLogo.png")

        firstGameCheckBox.checked=false
        player1ConfirmButton.isVisible=false
        player2ConfirmButton.isVisible=false
        player3ConfirmButton.isVisible=false
        player4ConfirmButton.isVisible=false
        player1ConfirmButton.isDisabled=false
        player2ConfirmButton.isDisabled=false
        player3ConfirmButton.isDisabled=false
        errorLabel.text=""
        firstButton.isVisible=false
        secondButton.isVisible=false
        thirdButton.isVisible=false
        fourthButton.isVisible=false
        simulationSpeedField.text=""
        startButton.isDisabled=true


        players=0
        readyPlayersChosen=0
        comPlayers=0
        humPlayers=0







    }

    fun checkNames() : Boolean
    {
        if (players==2)
        {
            if (player1Name.text == player2Name.text)
              {
               return true
            }
            errorLabel.text=""
            startButton.isDisabled=false
        }
        else if (players==3) {
            if (player1Name.text == player2Name.text || player1Name.text == player3Name.text
                || player2Name.text == player3Name.text )
            {
                return true
            }
            errorLabel.text=""
            startButton.isDisabled=false

        }

        else if (players == 4)
        {
            if ((player1Name.text == player2Name.text || player1Name.text == player3Name.text
                        || player2Name.text == player3Name.text) ||
                player1Name.text == player4Name.text || player2Name.text== player4Name.text ||
                player3Name.text == player4Name.text)
            {

              return true
            }
            errorLabel.text=""
            startButton.isDisabled=false
        }
        return false
    }

    fun orderofColor() : Boolean {
        if (players == 2) {
            if (player1Color.selectedItem?.equals(player2Color.selectedItem) == true) {
                return true
                //startButton.isDisabled=true
                //errorLabel.text= "Spieler dürfen nicht dieselbe Farbe haben!"


            }

        } else if (players == 3) {
            if (player1Color.selectedItem?.equals(player2Color.selectedItem) == true ||
                player1Color.selectedItem?.equals(player3Color.selectedItem) == true
                || player2Color.selectedItem?.equals(player3Color.selectedItem) == true
            ) {
                return true
            }

        } else if (players == 4) {
            if (player1Color.selectedItem?.equals(player2Color.selectedItem) == true ||
                player1Color.selectedItem?.equals(player3Color.selectedItem) == true
                || player2Color.selectedItem?.equals(player3Color.selectedItem) == true
                || player1Color.selectedItem?.equals(player4Color.selectedItem) == true
                || player2Color.selectedItem?.equals(player4Color.selectedItem) == true
                || player3Color.selectedItem?.equals(player4Color.selectedItem) == true
            ) {
                return true
            }

        }
        return false
    }

    fun checkDifficulty() : Boolean
    {
        if (players==2)
        {
            if (playerOneButtonState=="aiplayer" && playerTwoButtonState =="aiplayer" &&
                    player1Difficulty.selectedItem?.equals(player2Difficulty.selectedItem) == false)
            {

                return true
            }

        }
        else if (players==3) {
            if (
                playerOneButtonState == "aiplayer" && playerTwoButtonState == "aiplayer" &&
                player1Difficulty.selectedItem?.equals(player2Difficulty.selectedItem) == false ||

                playerOneButtonState == "aiplayer" && playerThreeButtonState == "aiplayer" &&
                player1Difficulty.selectedItem?.equals(player3Difficulty.selectedItem) == false
                ||
                playerTwoButtonState == "aiplayer" && playerThreeButtonState == "aiplayer" &&
                player2Difficulty.selectedItem?.equals(player3Difficulty.selectedItem) == false
            )
            {

                return true
            }

        }
        else if (players == 4)
        {
            if (playerOneButtonState == "aiplayer" && playerTwoButtonState == "aiplayer" &&
                player1Difficulty.selectedItem?.equals(player2Difficulty.selectedItem) == false
                ||
                playerOneButtonState == "aiplayer" && playerThreeButtonState == "aiplayer" &&
                player1Difficulty.selectedItem?.equals(player3Difficulty.selectedItem) == false
                || playerTwoButtonState == "aiplayer" && playerThreeButtonState == "aiplayer" &&
                player2Difficulty.selectedItem?.equals(player3Difficulty.selectedItem) == false
                || playerOneButtonState == "aiplayer" && playerFourButtonState == "aiplayer" &&
                player1Difficulty.selectedItem?.equals(player4Difficulty.selectedItem) == false
                || playerTwoButtonState == "aiplayer" && playerFourButtonState == "aiplayer" &&
                player2Difficulty.selectedItem?.equals(player4Difficulty.selectedItem) == false
                || playerThreeButtonState == "aiplayer" && playerFourButtonState == "aiplayer" &&
                player3Difficulty.selectedItem?.equals(player4Difficulty.selectedItem) == false
            ) {
                return true
            }

        }
        return false
    }

    /**
     * function to start an only AI game.
     */
    fun startNewAISimGame()
    {

        for (players in playerList)
        {
            if (players.third != PlayerType.HUMAN )
            {
                rootService.gameService.startNewAISimulationGame(playerList,simulationSpeedField.toString().toInt())
            }
        }
    }





}




