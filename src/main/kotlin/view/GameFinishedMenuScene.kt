package view

import entity.Novaluna
import entity.State
import service.RootService
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import java.awt.Color

/**
 * Class to represent the MenueScene after the game ended.
 */
class GameFinishedMenuScene(private val rootService: RootService) : MenuScene(900,900), Refreshable {
    private val headlineLabel = Label(
        width = 300, height = 25, posX = 300, posY = 50,
        text = "Spiel beendet!",
        font = Font(size = 25,Color(255,212,57))
    )

    val winnerLabel = Label(
        width = 300,
        height = 75,
        posX = 300,
        posY = 100,
        text = ""
    ).apply {
        alignment = Alignment.CENTER
        font = Font(20,Color(255,212,57))

    }

    val placeLabel = Label(width = 200, height = 35, posX = 100, posY = 250).apply {
        font = Font(20)
        alignment = Alignment.CENTER_LEFT
        text="Platz"
    }.apply {
        alignment = Alignment.CENTER
        font = Font(20,Color(255,212,57))

    }

    val scoreLabel = Label(width = 200, height = 35, posX = 600, posY = 250).apply {
        font = Font(20)
        alignment = Alignment.CENTER_LEFT
        text="Plättchen übrig"
    }.apply {
        alignment = Alignment.CENTER
        font = Font(20, Color(255, 212, 57))
    }

    val firstPlayerLabel = Label(width = 200, height = 35, posX = 100, posY = 300).apply {
        font = Font(20)
        alignment = Alignment.CENTER_LEFT
        text="1."
    }.apply {
        alignment = Alignment.CENTER
        font = Font(20, Color(255, 212, 57))
        isVisible = false
    }
    val p1Score = Label(width = 200, height = 35, posX = 600, posY = 300).apply {
        font = Font(20)
        alignment = Alignment.CENTER
        text=""}.apply {
        alignment = Alignment.CENTER
        font = Font(20,Color(255,212,57))
        isVisible = false
    }


    val secondPlayerLabel = Label(width = 200, height = 35, posX = 100, posY = 350).apply {
        font = Font(20)
        alignment = Alignment.CENTER_LEFT
        text="2."
        font = Font(20, Color(255, 212, 57))
    }.apply {
        alignment=Alignment.CENTER
        isVisible = false
    }

    val p2Score = Label(width = 200, height = 35, posX = 600, posY = 350).apply {
        font = Font(20, Color(255, 212, 57))
        alignment = Alignment.CENTER
        text=""
        isVisible = false
    }

    val thirdPlayerLabel = Label(width = 200, height = 35, posX = 100, posY = 400).apply {
        font = Font(20, Color(255, 212, 57))
        alignment = Alignment.CENTER_LEFT
        text="3."
    }.apply {
        alignment = Alignment.CENTER
        isVisible = false
    }

    val p3Score = Label(width = 200, height = 35, posX = 600, posY = 400).apply {
        font = Font(20, Color(255, 212, 57))
        alignment = Alignment.CENTER
        text=""
        isVisible = false
    }

    val fourthPlayerLabel = Label(width = 200, height = 35, posX = 100, posY = 450).apply {
        font = Font(20, Color(255, 212, 57))
        alignment = Alignment.CENTER_LEFT
        text="4."
    }.apply {
        alignment = Alignment.CENTER
        isVisible = false
    }
    val p4Score = Label(width = 200, height = 35, posX = 600, posY = 450).apply {
        font = Font(20, Color(255, 212, 57))
        alignment = Alignment.CENTER
        text=""
        isVisible = false
    }

    val nameLabel = Label(width = 100, height = 35, posX = 400, posY = 250).apply {
        font = Font(20)
        alignment = Alignment.CENTER_LEFT
        text="Name"
    }.apply {
        alignment = Alignment.CENTER
        font = Font(20, Color(255, 212, 57))
    }

    val p1NameLabel = Label(width = 100, height = 35, posX = 400, posY = 300).apply {
        font = Font(20, Color(255, 212, 57))
        alignment = Alignment.CENTER
        text=""
        isVisible = false
    }

    val p2NameLabel =Label(width = 100, height = 35, posX = 400, posY = 350).apply {
        font = Font(20, Color(255, 212, 57))
        alignment = Alignment.CENTER
        text=""
        isVisible = false
    }

    val p3NameLabel =Label(width = 100, height = 35, posX = 400, posY = 400).apply {
        font = Font(20, Color(255, 212, 57))
        alignment = Alignment.CENTER
        text=""
        isVisible = false
    }

    val p4NameLabel =Label(width = 100, height = 35, posX = 400, posY = 450).apply {
        font = Font(20, Color(255, 212, 57))
        alignment = Alignment.CENTER
        text=""
        isVisible = false
    }

    val playerLabelList = listOf(firstPlayerLabel, secondPlayerLabel, thirdPlayerLabel, fourthPlayerLabel)
    val playerNameList = listOf(p1NameLabel, p2NameLabel, p3NameLabel, p4NameLabel)
    val playerScoreList = listOf(p1Score, p2Score, p3Score, p4Score)


    val backtoMenuButton = Button(
        width = 250, height = 60,
        posX = 350, posY = 550,
        text = "Zurück zum Hauptmenü"
    ).apply {
        visual = ColorVisual(108, 168, 59)
        onMouseEntered = {
            visual = CompoundVisual(ColorVisual(108, 168, 59), ColorVisual.BLACK.apply { transparency = 0.2 })
        }
        onMouseExited = {
            visual = ColorVisual(108, 168, 59)
        }
    }

    val quitButton = Button(
        width = 250, height = 60,
        posX = 350, posY = 625,
        text = "Spiel beenden"
    ).apply {
        visual = ColorVisual(218, 58, 48)
        onMouseEntered = {
            visual = CompoundVisual(ColorVisual(218, 58, 48), ColorVisual.BLACK.apply { transparency = 0.2 })
        }
        onMouseExited = {
            visual = ColorVisual(218, 58, 48)
        }
    }





    init {
        background = ColorVisual(47, 47, 47)
        addComponents(

            headlineLabel,firstPlayerLabel,secondPlayerLabel,thirdPlayerLabel,fourthPlayerLabel,
            p1Score, p2Score, p3Score, p4Score, winnerLabel,
            backtoMenuButton, quitButton, placeLabel,scoreLabel, nameLabel,
            p1NameLabel,p2NameLabel,p3NameLabel,p4NameLabel
        )
    }

    private fun State.gameResultString(): String {
        val p1Score = players.first().tokens
        val p2Score = players.component2().tokens
        var p3Score= 0
        var p4Score = 0
        if (players.size ==3)
        {
            p3Score =players.component3().tokens
        }
        if (players.size == 4)
        {
            p3Score=players.component3().tokens
            p4Score=players.last().tokens
        }

        val maximumScore = minOf(p1Score,p2Score,p3Score,p4Score)
        if (players.size ==2)
        {
            if (p1Score ==maximumScore) {
                winnerLabel.text=  "${players.first().name} wins the game."
            }
            else if ( p2Score == maximumScore) {
                winnerLabel.text=  "${players.last().name} wins the game."
            }

        }
        else if (players.size ==3) {
            if (p1Score == maximumScore) {
                winnerLabel.text= "${players.first().name} hat gewonnen."
            } else if (p2Score == maximumScore) {
                winnerLabel.text= "${players.component2().name} hat gewonnen."
            } else if (p3Score == maximumScore) {
                winnerLabel.text= "${players.last().name} hat gewonnen."
            }
        }
        else if (players.size ==4)
        {
            if (p1Score == maximumScore)
            {
                winnerLabel.text= "${players.first().name} hat gewonnen."
            }
            else if (p2Score == maximumScore)
            {
                winnerLabel.text= "${players.component2().name} hat gewonnen."
            }
            else if (p3Score  == maximumScore)
            {
                winnerLabel.text= "${players.component3().name} hat gewonnen."
            }
            else if (p4Score == maximumScore)
            {
                winnerLabel.text= "${players.last().name} hat gewonnen."
            }
        }
        return  "Unentschieden.Kein Sieger"

    }


    fun reset()
    {
        firstPlayerLabel.text=""
        secondPlayerLabel.text=""
        thirdPlayerLabel.text=""
        fourthPlayerLabel.text=""
        p1Score.text=""
        p2Score.text=""
        p3Score.text=""
        p4Score.text=""
        p1NameLabel.text=""
        p2NameLabel.text=""
        p3NameLabel.text=""
        p4NameLabel.text=""
        winnerLabel.text=""


    }
}




