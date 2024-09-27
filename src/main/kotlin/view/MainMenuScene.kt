package view

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.dialog.FileDialog
import tools.aqua.bgw.dialog.FileDialogMode
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color
import javax.security.auth.Refreshable

/**
 * MenuScene shown when the application is started.
 */
class MainMenuScene : MenuScene(1920, 1082), view.Refreshable{

    val newGameButton = Button(
        width = 290, height = 75,
        posX = 640, posY = 580,
        text = "Starte neues Spiel"
    ).apply {
        buttonChanges(this)


    }

    val loadGameButton = Button(
        width = 290, height = 75,
        posX = 990, posY = 580,
        text = "Spiel laden"
    ).apply {
        buttonChanges(this)
    }

    val newTournamentButton = Button(
        width = 640, height = 75,
        posX = 640, posY = 695,
        text = "Starte neues KI Turnier"
    ).apply {
        buttonChanges(this)
    }

    val highscoreButton = Button(
        width = 290, height = 75,
        posX = 640, posY = 810,
        text = "Highscores"
    ).apply {
        buttonChanges(this)
    }

    val closeButton = Button(
        width = 290, height = 75,
        posX = 990, posY = 810,
        text = "Beenden"
    ).apply {
        font = Font(20, Color(225,0,0))
        visual = ImageVisual("CloseButton.png")
        onMouseEntered = {
            visual = ImageVisual("CloseButtonActivated.png")
            font = Font(20, Color(0,0,0))
        }
        onMouseExited = {
            font = Font(20, Color(225,0,0))
            visual = ImageVisual("CloseButton.png")
        }
    }

    val howToPlayLabel = Label(
        width = 165, height = 40,
        posX = 1705, posY = 990
    ).apply {
        visual = ImageVisual("HowToPlay.png")
        isVisible = false
        isDisabled = true
        onMouseEntered = {
            visual = ImageVisual("HowToPlayActivated.png")
        }
        onMouseExited = {
            visual = ImageVisual("HowToPlay.png")
        }
    }

    private val titleLabel = Label(
        width = 800, height = 188,
        posX = 560, posY = 130,
        text = "Nova Luna"
    ).apply {
        font = Font(160, Color(255,212, 57))
    }

    private val moonLabel = Label(
        width = 600, height = 484,
        posX = 660, posY = 0
    ).apply {
        visual = ImageVisual("MoonMM.png")
    }

    private fun buttonChanges(button: Button){
        button.font = Font(20, Color(142,142,142))
        button.visual = ImageVisual("ButtonFrame.png")
        button.onMouseEntered = {
            button.visual = ImageVisual("ButtonFrameActivated.png")
            button.font = Font(20, Color(250,212,57))
        }
        button.onMouseExited = {
            button.font = Font(20, Color(142,142,142))
            button.visual = ImageVisual("ButtonFrame.png")
        }
    }

    init {
        background = ColorVisual(47,47,47)
        opacity = 1.0
        addComponents(
            newGameButton, loadGameButton, newTournamentButton, highscoreButton, closeButton,
            howToPlayLabel, moonLabel, titleLabel
        )
    }
}