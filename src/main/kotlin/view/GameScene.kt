package view

import service.RootService
import tools.aqua.bgw.components.container.Area
import tools.aqua.bgw.components.container.LinearLayout
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.gamecomponentviews.TokenView
import tools.aqua.bgw.components.layoutviews.GridPane
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color
import java.awt.TextField

/**
 * The Game itself.
 */
class GameScene(private val rootService: RootService) : BoardGameScene(1920, 1082), Refreshable {
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

    //TODO(Temporärer Button um zu gameFinishedScene zu kommen)
    val finButton = Button(
        width = 150, height = 75,
        posX = 400, posY = 0,
        text = "Zu FinishScene"
    ).apply {
        font = Font(15, Color(255, 0, 0))
        isVisible=false
    }

    val undoButton = Button(
        width = 30, height = 65,
        posX = 300, posY = 30
    ).apply {
        visual = ImageVisual("Undo.png")
        isVisible=false
        onMouseClicked = { rootService.playerActionService.undo() }
    }

    val redoButton = Button(
        width = 30, height = 65,
        posX = 200, posY = 30
    ).apply {
        visual = ImageVisual("Redo.png")
        isVisible=false
        onMouseClicked = { rootService.playerActionService.redo() }
    }

    val endTurnButton = Button(
        width = 290, height = 75,
        posX = 830, posY = 340,
        text = "Zug beenden"
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
    }

    val fillTilesButton = Button(
        width = 290, height = 75,
        posX = 830, posY = 420,
        text = "Plättchen auffüllen"
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

    }

    val tipButton = Button(
        width = 290, height = 75,
        posX = 830, posY = 500,
        text = "Tipp anfordern"
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
    }

    val closedTiles: LinearLayout<CardView> = LinearLayout<CardView>(
        height = 150,
        width = 290,
        posX = 830,
        posY = 580,
        spacing = 0,
        alignment = Alignment.CENTER,
        visual = ColorVisual(255, 255, 255, 50)
    )

    val tipLabel = Label(
        posX = 720, posY = 740,
        height = 50,
        width = 480,
        alignment = Alignment.CENTER,
        isWrapText = true,
        font = Font(20, Color(250, 212, 57))
    )

    //PlayerNameLabels//////////////////////////////////////////////////////////////////////////////////////////////////////
    private val player1Label = Label(
        width = 200, height = 65,
        posX = 30, posY = 100,
        text = "Spieler 1",
        font = Font(20, Color(250, 212, 57)),
        alignment = Alignment.CENTER_LEFT
    ).apply {
        isVisible = false
    }

    private val player2Label = Label(
        width = 200, height = 65,
        posX = 1690, posY = 100,
        text = "Spieler 2",
        font = Font(20, Color(250, 212, 57)),
        alignment = Alignment.CENTER_RIGHT
    ).apply {
        isVisible = false
    }

    private val player3Label = Label(
        width = 200, height = 65,
        posX = 30, posY = 950,
        text = "Spieler 3",
        font = Font(20, Color(250, 212, 57)),
        alignment = Alignment.CENTER_LEFT
    ).apply {
        isVisible = false
    }

    private val player4Label = Label(
        width = 200, height = 65,
        posX = 1690, posY = 950,
        text = "Spieler 4",
        font = Font(20, Color(250, 212, 57)),
        alignment = Alignment.CENTER_RIGHT
    ).apply {
        isVisible = false
    }

    val playerNameLabelList = listOf(player1Label, player2Label, player3Label, player4Label)

    //PlayerTokenLabels/////////////////////////////////////////////////////////////////////////////////////////////////
    private val player1TokensLabel = Label(
        width = 100, height = 65,
        posX = 230, posY = 100, text = "1",
        font = Font(20, Color(250, 212, 57)),
        alignment = Alignment.CENTER_RIGHT
    ).apply {
        isVisible = false
    }

    private val player2TokensLabel = Label(
        width = 100, height = 65,
        posX = 1590, posY = 100, text = "1",
        font = Font(20, Color(250, 212, 57)),
        alignment = Alignment.CENTER_LEFT
    ).apply {
        isVisible = false
    }

    private val player3TokensLabel = Label(
        width = 100, height = 65,
        posX = 230, posY = 950, text = "21",
        font = Font(20, Color(250, 212, 57)),
        alignment = Alignment.CENTER_RIGHT
    ).apply {
        isVisible = false
    }

    private val player4TokensLabel = Label(
        width = 100, height = 65,
        posX = 1590, posY = 950, text = "21",
        font = Font(20, Color(250, 212, 57)),
        alignment = Alignment.CENTER_LEFT
    ).apply {
        isVisible = false
    }

    val playerTokenLabelList = listOf(player1TokensLabel, player2TokensLabel, player3TokensLabel, player4TokensLabel)

    //PlayerColorLabels/////////////////////////////////////////////////////////////////////////////////////////////////
    private val p1colorLabel = Label(
        posX = 0, posY = 510,
        width = 360, height = 50
    )
    private val p2colorLabel = Label(
        posX = 1560, posY = 510,
        width = 360, height = 50
    )
    private val p3colorLabel = Label(
        posX = 0, posY = 560,
        width = 360, height = 50
    )
    private val p4colorLabel = Label(
        posX = 1560, posY = 560,
        width = 360, height = 50
    )

    val  playerColorList = listOf(p1colorLabel, p2colorLabel, p3colorLabel, p4colorLabel)

    //MoonWheel/////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Oben
    private val tokenPos22 = LinearLayout<CardView>(
        posX = 710, posY = 190,
        width = 100, height = 100,
        visual = ColorVisual.GRAY,
        orientation = Orientation.VERTICAL,
        alignment = Alignment.CENTER,
        spacing = -50
    )
    private val tokenPos23 = LinearLayout<CardView>(
        posX = 810, posY = 190,
        width = 100, height = 100,
        visual = ColorVisual.GRAY,
        orientation = Orientation.VERTICAL,
        alignment = Alignment.CENTER,
        spacing = -50
    )
    private val tokenPos0 = LinearLayout<CardView>(
        posX = 910, posY = 190,
        width = 100, height = 100,
        visual = ColorVisual.GRAY,
        orientation = Orientation.VERTICAL,
        alignment = Alignment.CENTER,
        spacing = -50
    )
    private val tokenPos1 = LinearLayout<CardView>(
        posX = 1010, posY = 190,
        width = 100, height = 100,
        visual = ColorVisual.GRAY,
        orientation = Orientation.VERTICAL,
        alignment = Alignment.CENTER,
        spacing = -50
    )
    private val tokenPos2 = LinearLayout<CardView>(
        posX = 1110, posY = 190,
        width = 100, height = 100,
        visual = ColorVisual.GRAY,
        orientation = Orientation.VERTICAL,
        alignment = Alignment.CENTER,
        spacing = -50
    )
    private val tokenPos3 = LinearLayout<CardView>(
        posX = 1210, posY = 190,
        width = 100, height = 100,
        visual = ColorVisual.GRAY,
        orientation = Orientation.VERTICAL,
        alignment = Alignment.CENTER,
        spacing = -50
    )

    //Rechts
    private val tokenPos4 = LinearLayout<CardView>(
        posX = 1210, posY = 290,
        width = 100, height = 100,
        visual = ColorVisual.GRAY,
        orientation = Orientation.VERTICAL,
        alignment = Alignment.CENTER,
        spacing = -50
    )
    private val tokenPos5 = LinearLayout<CardView>(
        posX = 1210, posY = 390,
        width = 100, height = 100,
        visual = ColorVisual.GRAY,
        orientation = Orientation.VERTICAL,
        alignment = Alignment.CENTER,
        spacing = -50
    )
    private val tokenPos6 = LinearLayout<CardView>(
        posX = 1210, posY = 490,
        width = 100, height = 100,
        visual = ColorVisual.GRAY,
        orientation = Orientation.VERTICAL,
        alignment = Alignment.CENTER,
        spacing = -50
    )
    private val tokenPos7 = LinearLayout<CardView>(
        posX = 1210, posY = 590,
        width = 100, height = 100,
        visual = ColorVisual.GRAY,
        orientation = Orientation.VERTICAL,
        alignment = Alignment.CENTER,
        spacing = -50
    )
    private val tokenPos8 = LinearLayout<CardView>(
        posX = 1210, posY = 690,
        width = 100, height = 100,
        visual = ColorVisual.GRAY,
        orientation = Orientation.VERTICAL,
        alignment = Alignment.CENTER,
        spacing = -50
    )
    private val tokenPos9 = LinearLayout<CardView>(
        posX = 1210, posY = 790,
        width = 100, height = 100,
        visual = ColorVisual.GRAY,
        orientation = Orientation.VERTICAL,
        alignment = Alignment.CENTER,
        spacing = -50
    )

    //Unten
    private val tokenPos10 = LinearLayout<CardView>(
        posX = 1110, posY = 790,
        width = 100, height = 100,
        visual = ColorVisual.GRAY,
        orientation = Orientation.VERTICAL,
        alignment = Alignment.CENTER,
        spacing = -50
    )
    private val tokenPos11 = LinearLayout<CardView>(
        posX = 1010, posY = 790,
        width = 100, height = 100,
        visual = ColorVisual.GRAY,
        orientation = Orientation.VERTICAL,
        alignment = Alignment.CENTER,
        spacing = -50
    )
    private val tokenPos12 = LinearLayout<CardView>(
        posX = 910, posY = 790,
        width = 100, height = 100,
        visual = ColorVisual.GRAY,
        orientation = Orientation.VERTICAL,
        alignment = Alignment.CENTER,
        spacing = -50
    )
    private val tokenPos13 = LinearLayout<CardView>(
        posX = 810, posY = 790,
        width = 100, height = 100,
        visual = ColorVisual.GRAY,
        orientation = Orientation.VERTICAL,
        alignment = Alignment.CENTER,
        spacing = -50
    )
    private val tokenPos14 = LinearLayout<CardView>(
        posX = 710, posY = 790,
        width = 100, height = 100,
        visual = ColorVisual.GRAY,
        orientation = Orientation.VERTICAL,
        alignment = Alignment.CENTER,
        spacing = -50
    )
    private val tokenPos15 = LinearLayout<CardView>(
        posX = 610, posY = 790,
        width = 100, height = 100,
        visual = ColorVisual.GRAY,
        orientation = Orientation.VERTICAL,
        alignment = Alignment.CENTER,
        spacing = -50
    )

    //Links
    private val tokenPos16 = LinearLayout<CardView>(
        posX = 610, posY = 690,
        width = 100, height = 100,
        visual = ColorVisual.GRAY,
        orientation = Orientation.VERTICAL,
        alignment = Alignment.CENTER,
        spacing = -50
    )
    private val tokenPos17 = LinearLayout<CardView>(
        posX = 610, posY = 590,
        width = 100, height = 100,
        visual = ColorVisual.GRAY,
        orientation = Orientation.VERTICAL,
        alignment = Alignment.CENTER,
        spacing = -50
    )
    private val tokenPos18 = LinearLayout<CardView>(
        posX = 610, posY = 490,
        width = 100, height = 100,
        visual = ColorVisual.GRAY,
        orientation = Orientation.VERTICAL,
        alignment = Alignment.CENTER,
        spacing = -50
    )
    private val tokenPos19 = LinearLayout<CardView>(
        posX = 610, posY = 390,
        width = 100, height = 100,
        visual = ColorVisual.GRAY,
        orientation = Orientation.VERTICAL,
        alignment = Alignment.CENTER,
        spacing = -50
    )
    private val tokenPos20 = LinearLayout<CardView>(
        posX = 610, posY = 290,
        width = 100, height = 100,
        visual = ColorVisual.GRAY,
        orientation = Orientation.VERTICAL,
        alignment = Alignment.CENTER,
        spacing = -50
    )
    private val tokenPos21 = LinearLayout<CardView>(
        posX = 610, posY = 190,
        width = 100, height = 100,
        visual = ColorVisual.GRAY,
        orientation = Orientation.VERTICAL,
        alignment = Alignment.CENTER,
        spacing = -50
    )


    val moonwheel = listOf(
        tokenPos0, tokenPos1, tokenPos2, tokenPos3, tokenPos4, tokenPos5, tokenPos6, tokenPos7,
        tokenPos8, tokenPos9, tokenPos10, tokenPos11, tokenPos12, tokenPos13, tokenPos14, tokenPos15, tokenPos16,
        tokenPos17, tokenPos18, tokenPos19, tokenPos20, tokenPos21, tokenPos22, tokenPos23
    )

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    val nextPlayerLabel = Label(
        width = 200, height = 65,
        posX = 885, posY = 280, text = "Spieler 1 ist dran!", font = Font(20, Color(250, 212, 57))
    )

    private val moonTiles12 = LinearLayout<CardView>(
        width = 150, height = 150,
        posX = 658, posY = 26,
        visual = ColorVisual(255, 255, 255, 20)
    )

    private val moonTiles1 = LinearLayout<CardView>(
        width = 150, height = 150,
        posX = 873, posY = 26,
        visual = ColorVisual(255, 255, 255, 20)
    )

    private val moonTiles2 = LinearLayout<CardView>(
        width = 150, height = 150,
        posX = 1089, posY = 26,
        visual = ColorVisual(255, 255, 255, 20)
    )

    private val moonTiles3 = LinearLayout<CardView>(
        width = 150, height = 150,
        posX = 1328, posY = 255,
        visual = ColorVisual(255, 255, 255, 20)
    )

    private val moonTiles4 = LinearLayout<CardView>(
        width = 150, height = 150,
        posX = 1328, posY = 441,
        visual = ColorVisual(255, 255, 255, 20)
    )

    private val moonTiles5 = LinearLayout<CardView>(
        width = 150, height = 150,
        posX = 1328, posY = 655,
        visual = ColorVisual(255, 255, 255, 20)
    )

    private val moonTiles6 = LinearLayout<CardView>(
        width = 150, height = 150,
        posX = 1089, posY = 910,
        visual = ColorVisual(255, 255, 255, 20)
    )

    private val moonTiles7 = LinearLayout<CardView>(
        width = 150, height = 150,
        posX = 873, posY = 910,
        visual = ColorVisual(255, 255, 255, 20)
    )

    private val moonTiles8 = LinearLayout<CardView>(
        width = 150, height = 150,
        posX = 658, posY = 910,
        visual = ColorVisual(255, 255, 255, 20)
    )

    private val moonTiles9 = LinearLayout<CardView>(
        width = 150, height = 150,
        posX = 445, posY = 655,
        visual = ColorVisual(255, 255, 255, 20)
    )

    private val moonTiles10 = LinearLayout<CardView>(
        width = 150, height = 150,
        posX = 445, posY = 441,
        visual = ColorVisual(255, 255, 255, 20)
    )

    private val moonTiles11 = LinearLayout<CardView>(
        width = 150, height = 150,
        posX = 445, posY = 255,
        visual = ColorVisual(255, 255, 255, 20)
    )

    private val p1GridPane = GridPane<CardView>(
        posX = 0, posY = 150,
        columns = 9, rows = 9,
        visual = ColorVisual(0, 0, 0),
        layoutFromCenter = false
    ).apply {
        isVisible = false
        this.forEach {
            setRowHeight(it.rowIndex, 40)
            setColumnWidth(it.columnIndex, 40)
        }
    }

    private val p2GridPane = GridPane<CardView>(
        posX = 1560, posY = 150,
        columns = 9, rows = 9,
        visual = ColorVisual(0, 0, 0),
        layoutFromCenter = false
    ).apply {
        isVisible = false
        this.forEach {
            setRowHeight(it.rowIndex, 40)
            setColumnWidth(it.columnIndex, 40)
        }
    }

    private val p3GridPane = GridPane<CardView>(
        posX = 0, posY = 610,
        columns = 9, rows = 9,
        visual = ColorVisual(0, 0, 0),
        layoutFromCenter = false
    ).apply {
        isVisible = false
        this.forEach {
            setRowHeight(it.rowIndex, 40)
            setColumnWidth(it.columnIndex, 40)
        }
    }

    private val p4GridPane = GridPane<CardView>(
        posX = 1560, posY = 610,
        columns = 9, rows = 9,
        visual = ColorVisual(0, 0, 0),
        layoutFromCenter = false
    ).apply {
        isVisible = false
        this.forEach {
            setRowHeight(it.rowIndex, 40)
            setColumnWidth(it.columnIndex, 40)
        }
    }

    val moonTileList = listOf<LinearLayout<CardView>>(
        moonTiles1, moonTiles2, moonTiles3, moonTiles4, moonTiles5,
        moonTiles6, moonTiles7, moonTiles8, moonTiles9, moonTiles10, moonTiles11, moonTiles12
    )

    val gridPanes = listOf(p1GridPane, p2GridPane, p3GridPane, p4GridPane)

    val gridPaneList = mutableListOf<GridPane<CardView>>()

    val protocolTable = TableView<String>(
        posX = 1560, posY = 560,
        width = 360, height = 550
    ).apply {
        isVisible = false
        isDisabled = true
    }

    init {
        background = ColorVisual(47, 47, 47)
        opacity = 1.0
        addComponents(
            backButton,
            undoButton,
            redoButton,
            endTurnButton,
            fillTilesButton,
            tipButton,
            tipLabel,
            closedTiles,
            player1Label,
            player2Label,
            player3Label,
            player4Label,
            nextPlayerLabel,
            finButton,
            protocolTable,
            moonTiles1,
            moonTiles2,
            moonTiles3,
            moonTiles4,
            moonTiles5,
            moonTiles6,
            moonTiles7,
            moonTiles8,
            moonTiles9,
            moonTiles10,
            moonTiles11,
            moonTiles12,
            player1TokensLabel,
            player2TokensLabel,
            player3TokensLabel,
            player4TokensLabel,

            tokenPos0,
            tokenPos1,
            tokenPos2,
            tokenPos3,
            tokenPos4,
            tokenPos5,
            tokenPos6,
            tokenPos7,
            tokenPos8,
            tokenPos9,
            tokenPos10,
            tokenPos11,
            tokenPos12,
            tokenPos13,
            tokenPos14,
            tokenPos15,
            tokenPos16,
            tokenPos17,
            tokenPos18,
            tokenPos19,
            tokenPos20,
            tokenPos21,
            tokenPos22,
            tokenPos23,
            p1colorLabel, p2colorLabel, p3colorLabel, p4colorLabel,

            p1GridPane,
            p2GridPane,
            p3GridPane,
            p4GridPane
        )

        protocolTable.columns.add(TableColumn("Protocol", 2000) {it})
    }

    /**
     * resets the grid after redo
     */
    fun resetAfterDo() {
        moonTiles1.clear()
        moonTiles2.clear()
        moonTiles3.clear()
        moonTiles4.clear()
        moonTiles5.clear()
        moonTiles6.clear()
        moonTiles7.clear()
        moonTiles8.clear()
        moonTiles9.clear()
        moonTiles10.clear()
        moonTiles11.clear()
        moonTiles12.clear()
        tokenPos0.clear()
        tokenPos1.clear()
        tokenPos2.clear()
        tokenPos3.clear()
        tokenPos4.clear()
        tokenPos5.clear()
        tokenPos6.clear()
        tokenPos7.clear()
        tokenPos8.clear()
        tokenPos9.clear()
        tokenPos10.clear()
        tokenPos11.clear()
        tokenPos12.clear()
        tokenPos13.clear()
        tokenPos14.clear()
        tokenPos15.clear()
        tokenPos16.clear()
        tokenPos17.clear()
        tokenPos18.clear()
        tokenPos19.clear()
        tokenPos20.clear()
        tokenPos21.clear()
        tokenPos22.clear()
        tokenPos23.clear()
        protocolTable.items.clear()
        p1GridPane.forEach{ removeComponents() }
        p2GridPane.forEach{removeComponents()}
        p3GridPane.forEach{removeComponents()}
        p4GridPane.forEach { removeComponents() }

    }

    fun reset() {
        player4TokensLabel.text = ""
        player3TokensLabel.text = ""
        player2TokensLabel.text = ""
        player1TokensLabel.text = ""
        nextPlayerLabel.text = ""
        player4Label.text = ""
        player3Label.text = ""
        player2Label.text = ""
        player1Label.text = ""
        moonTiles1.clear()
        moonTiles2.clear()
        moonTiles3.clear()
        moonTiles4.clear()
        moonTiles5.clear()
        moonTiles6.clear()
        moonTiles7.clear()
        moonTiles8.clear()
        moonTiles9.clear()
        moonTiles10.clear()
        moonTiles11.clear()
        moonTiles12.clear()
        tokenPos0.clear()
        tokenPos1.clear()
        tokenPos2.clear()
        tokenPos3.clear()
        tokenPos4.clear()
        tokenPos5.clear()
        tokenPos6.clear()
        tokenPos7.clear()
        tokenPos8.clear()
        tokenPos9.clear()
        tokenPos10.clear()
        tokenPos11.clear()
        tokenPos12.clear()
        tokenPos13.clear()
        tokenPos14.clear()
        tokenPos15.clear()
        tokenPos16.clear()
        tokenPos17.clear()
        tokenPos18.clear()
        tokenPos19.clear()
        tokenPos20.clear()
        tokenPos21.clear()
        tokenPos22.clear()
        tokenPos23.clear()
        protocolTable.items.clear()
        p1GridPane.forEach{ removeComponents() }
        p2GridPane.forEach{removeComponents()}
        p3GridPane.forEach{removeComponents()}
        p4GridPane.forEach { removeComponents() }



    }

    fun resetAfterDo1()
    {

        tokenPos0.clear()
        tokenPos1.clear()
        tokenPos2.clear()
        tokenPos3.clear()
        tokenPos4.clear()
        tokenPos5.clear()
        tokenPos6.clear()
        tokenPos7.clear()
        tokenPos8.clear()
        tokenPos9.clear()
        tokenPos10.clear()
        tokenPos11.clear()
        tokenPos12.clear()
        tokenPos13.clear()
        tokenPos14.clear()
        tokenPos15.clear()
        tokenPos16.clear()
        tokenPos17.clear()
        tokenPos18.clear()
        tokenPos19.clear()
        tokenPos20.clear()
        tokenPos21.clear()
        tokenPos22.clear()
        tokenPos23.clear()
        protocolTable.items.clear()

       // p1GridPane.forEach{ removeComponents() }
        //p2GridPane.forEach{removeComponents()}
        //p3GridPane.forEach{removeComponents()}
        // p4GridPane.forEach { removeComponents() }
    }

}