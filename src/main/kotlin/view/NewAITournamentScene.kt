package view

import entity.PlayerColour
import entity.PlayerType
import service.RootService
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.dialog.ExtensionFilter
import tools.aqua.bgw.dialog.FileDialog
import tools.aqua.bgw.dialog.FileDialogMode
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color

class NewAITournamentScene(private val rootService: RootService) : MenuScene(1920, 1080), Refreshable {
 var playerList = mutableListOf<Triple<String, PlayerColour, PlayerType>>()
    private var players=0
    private var aiPlayerState ="aiplayer"
    private var humanPlayerstate="human"
    private var readyPlayersChosen = 0

    private var playerColors = mutableListOf(
        PlayerColour.BLUE,
        PlayerColour.BLACK,
        PlayerColour.ORANGE,
        PlayerColour.WHITE
    )

    //Human Player//////////////////////////////////////////////////////////////////////////////////////////////////////
    private val humanButton = Button(
        width = 300, height = 300,
        posX = 600, posY = 100
    ).apply {
        visual = ImageVisual("HumanPlayerLogo.png")
        isDisabled = true
    }

    private val humanName: TextField = TextField(
        width = 290, height = 35,
        posX = 600, posY = 430,
        text = "Gruppe 03"
    ).apply {
        isDisabled = false
        font = Font(20, Color(142, 142, 142))
    }

    private val humanColor = ComboBox<PlayerColour>(
        width = 290, height = 35,
        posX = 600, posY = 480,
        prompt = "Wähle eine Farbe",
        font = Font(20, Color(250, 212, 57))
    ).apply {
        items = playerColors
    }

    private val humanDifficulty = ComboBox<PlayerType>(
        width = 290, height = 35,
        posX = 1020, posY = 530,
        prompt = "Wähle eine Schwierigkeit",
        font = Font(20, Color(250, 212, 57))
    ).apply {
        items = mutableListOf(PlayerType.EASY, PlayerType.NORMAL, PlayerType.HARD)
        isDisabled = true
        isVisible = false
    }

    private val humanConfirmButton: Button = Button(
        width = 290, height = 35,
        posX = 600, posY = 580,
        text = "Bereit?"
    ).apply {
        font = Font(20, Color(142, 142, 142))
        visual = ImageVisual("ButtonFrame.png")
        onMouseClicked ={ confirmBehaviour(this,humanButton,humanName,humanColor, humanDifficulty, humanPlayerstate)}
    }

    //AI Player/////////////////////////////////////////////////////////////////////////////////////////////////////////
    private val aiButton = Button(
        width = 300, height = 300,
        posX = 1020, posY = 100
    ).apply {
        visual = ImageVisual("AIPlayerLogo.png")
        isDisabled = true
    }

    private val aiName: TextField = TextField(
        width = 290, height = 35,
        posX = 1020, posY = 430,
        text = "COM"
    ).apply {
        isDisabled = false
        font = Font(20, Color(142, 142, 142))
    }

    private val aiColor = ComboBox<PlayerColour>(
        width = 290, height = 35,
        posX = 1020, posY = 480,
        prompt = "Wähle eine Farbe",
        font = Font(20, Color(250, 212, 57))
    ).apply {
        items = playerColors
    }

    private val aiDifficulty = ComboBox<PlayerType>(
        width = 290, height = 35,
        posX = 1020, posY = 530,
        prompt = "Wähle eine Schwierigkeit",
        font = Font(20, Color(250, 212, 57))
    ).apply {
        items = mutableListOf(PlayerType.EASY, PlayerType.NORMAL, PlayerType.HARD)
    }

    private val aiConfirmButton: Button = Button(
        width = 290, height = 35,
        posX = 1020, posY = 580,
        text = "Bereit?"
    ).apply {
        font = Font(20, Color(142, 142, 142))
        visual = ImageVisual("ButtonFrame.png")
        onMouseClicked = {
            confirmBehaviour(this, aiButton, aiName, aiColor, aiDifficulty,aiPlayerState )
        }
    }
    //Menüfunktionen////////////////////////////////////////////////////////////////////////////////////////////////////

    private val errorLabel = Label(
        width = 600, height = 45,
        posX = 660, posY = 720
    ).apply {
        font = Font(25, Color(225, 0, 0))

    }
    val startButton = Button(
        width = 290, height = 75,
        posX = 815, posY = 850,
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

    private val simulationSpeedLabel = Label(
        width = 480, height = 45,
        posX = 50, posY = 850,
        text = "Simulationsgeschwindigkeit:",
        alignment = Alignment.CENTER_LEFT
    ).apply {
        font = Font(20, Color(250, 212, 57))
    }

    private val simulationSpeedField: TextField = TextField(
        width = 90, height = 45,
        posX = 625, posY = 850,
        prompt = "In s"
    ).apply {
        font = Font(25, Color(142, 142, 142))
    }

    private val loadCSVfileLabel = Label(
        width = 300, height = 45,
        posX = 50, posY = 920,
        text = "Lade CSV-Datei:",
        alignment = Alignment.CENTER_LEFT
    ).apply {
        font = Font(20, Color(250, 212, 57))
    }

    val loadCSVField = Label(
        width = 300, height = 45,
        posX = 370, posY = 920,
        text = "Keine Datei ausgewählt"
    ).apply {
        font = Font(25, Color(142, 142, 142))
        visual = ImageVisual("ButtonFrame.png")
    }

    val loadCSVfileButton = Button(
        width = 45, height = 45,
        posX = 670, posY = 920
    ).apply {
        visual = ImageVisual("Files.png")
    }

    val fileChooser = FileDialog(
        FileDialogMode.OPEN_FILE, "Lade KI Turnier CSV-Datei",
        ""
    )

    //Spieler Reihenfolge///////////////////////////////////////////////////////////////////////////////////////////////
    private val order = Label(
        width = 260, height = 45,
        posX = 1420, posY = 775,
        text = "Reihenfolge"
    ).apply {
        font = Font(20, Color(250, 212, 57))
    }

    private val diceButton = Button(
        width = 50, height = 50,
        posX = 1680, posY = 768
    ).apply {
        visual = ImageVisual("dice.png")
        onMouseClicked = {
            randomOrder()
        }
    }

    private val firstButton = Button(
        width = 300, height = 40,
        posX = 1400, posY = 850
    ).apply {
        isVisible = true
        isDisabled = false
        font = Font(20, Color(142, 142, 142))
        visual = ImageVisual("ButtonFrame.png")
        onMouseClicked = {
            orderBehaviour(this)
        }
    }

    private val secondButton = Button(
        width = 300, height = 40,
        posX = 1400, posY = 925
    ).apply {
        isVisible = true
        isDisabled = false
        font = Font(20, Color(142, 142, 142))
        visual = ImageVisual("ButtonFrame.png")
        onMouseClicked = {
            orderBehaviour(this)
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * function for random Order of players.
     */
    private fun randomOrder() {
        val players = mutableListOf("HumanPlayer", "COM")
        val rand = (0..1).random()
        firstButton.text = players[rand]
        secondButton.text = players.first { it != players[rand] }
    }

    /**
     * function to change the order of the players.
     */
    private fun orderBehaviour(button: Button) {
        if (playerList.size > 0) {
            button.text = playerList[readyPlayersChosen].first
            if (readyPlayersChosen == playerList.size - 1) {
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
            backButton, startButton, simulationSpeedLabel, loadCSVfileLabel, simulationSpeedField, loadCSVfileButton,
            order, diceButton,
            loadCSVField, firstButton, secondButton,
            humanButton, humanName, humanColor, humanConfirmButton,
            aiButton, aiColor, aiName, aiDifficulty, aiConfirmButton,errorLabel

        )

    }

    fun reset()
    {
        humanColor.selectedItem=null
        humanConfirmButton.isDisabled=true
        aiColor.selectedItem=null
        aiDifficulty.selectedItem=null
        aiConfirmButton.isDisabled=true
        simulationSpeedField.text=""
        firstButton.isDisabled=true
        secondButton.isDisabled=true
        humanName.text=""
        aiName.text=""
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
            if (orderofColor()) {
                errorLabel.text = "Die Spieler dürfen nicht dieselbe Farbe haben"
                conf.font = Font(20, Color(142, 142, 142))
                conf.visual = ImageVisual("ButtonFrame.png")
                conf.text = "Bereit?"
                playerButton.isDisabled = false
                name.isDisabled = false
                color.isDisabled = false
                difficulty.isDisabled = false
            } else
            {
                errorLabel.text=""
            }


            /*if (checkDifficulty()) {
                errorLabel.text = "KI-Spieler müssen dieselbe Schwierigkeit haben"
                conf.font = Font(20, Color(142, 142, 142))
                conf.visual = ImageVisual("ButtonFrame.png")
                conf.text = "Bereit?"
                playerButton.isDisabled = false
                name.isDisabled = false
                color.isDisabled = false
                difficulty.isDisabled = false
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

    fun orderofColor()  : Boolean{

            if (aiColor.selectedItem?.equals(humanColor.selectedItem) == true) {
                startButton.isDisabled = true
                return true
        }
        startButton.isDisabled=false
        return false
    }
    

}

