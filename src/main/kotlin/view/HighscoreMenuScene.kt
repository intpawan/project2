package view

import entity.Score
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TableColumn
import tools.aqua.bgw.components.uicomponents.TableView
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color

/**
 * The Highscore.
 * Who is the best?
 */
class HighscoreMenuScene : MenuScene(1920, 1080), Refreshable{

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

    val highscoreTable = TableView<Score>(
        width = 900, height = 600,
        posX = 510, posY = 380
    )


    private val titleLabel = Label(
        width = 800, height = 188,
        posX = 560, posY = 30,
        text = "Nova Luna"
    ).apply {
        font = Font(160, Color(255,212, 57))
    }

    private val highscoreLabel = Label(
        width = 524, height = 188,
        posX = 698, posY = 150,
        text = "Highscores:"
    ).apply {
        font = Font(96, Color(255,212, 57))
    }

    private val moonLabel = Label(
        width = 600, height = 285,
        posX = 660, posY = 0
    ).apply {
        visual = ImageVisual("MoonHS.png")
    }

    init {
        background = ColorVisual(47,47,47)
        addComponents(
            backButton, highscoreTable,
            moonLabel, titleLabel, highscoreLabel
        )
        highscoreTable.columns.add(TableColumn("Name", 798) { it.winner })
        highscoreTable.columns.add(TableColumn("Punkte", 100) { "${it.avgScore}" })
    }
}