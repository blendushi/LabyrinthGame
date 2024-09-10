import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.*
import scalafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import scalafx.scene.layout.{BorderPane, HBox, Pane}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.control.TextArea
import scalafx.scene.input.KeyCode.*
import scalafx.scene.text.Font

import scala.collection.mutable.ListBuffer
import scala.util.Try

object LabyrinthUI extends JFXApp3 {
  val cellSize = 20 // Size of each cell in pixels
  var labyrinthWidth = 25 // Width of the labyrinth in cells
  var labyrinthHeight = 25 // Height of the labyrinth in cells
  var canvasWidth = cellSize * labyrinthWidth + 200 // Width of the canvas in pixels
  var canvasHeight = cellSize * labyrinthHeight + 200 // Height of the canvas in pixels

  // Create the canvas
  //val canvasStart = new Canvas(canvasWidth, canvasHeight)
  var canvas = new Canvas(canvasWidth, canvasHeight) //canvas for the labyrinth
  val gc = canvas.graphicsContext2D
  var canvas2 = new Canvas(canvasWidth, canvasHeight)// canvas for solution
  val gc2 = canvas2.graphicsContext2D

  // Create the labyrinth
  var labyrinth: Labyrinth = new Labyrinth(labyrinthWidth, labyrinthHeight)


  // Generate and draw the labyrinth
  labyrinth.generate()

  //draw the "rat" which is a blue box
  val rectangle =
    new Rectangle:
      x = labyrinth.start.x * cellSize +7
      y = labyrinth.start.y * cellSize +7
      width = 10
      height = 10
      fill = Color.Blue

  // Draw the labyrinth on the canvas
  def drawLabyrinth(): Unit = {
    gc.setFill(Color.White)
    gc.fillRect(0, 0, canvasWidth, canvasHeight)
    gc.setLineWidth(2)
    gc.setStroke(Color.Black)
    gc2.setFill(Color.White)
    gc2.fillRect(0, 0, canvasWidth, canvasHeight)
    gc2.setLineWidth(2)
    gc2.setStroke(Color.Black)

    for (i <- 0 until labyrinthHeight; j <- 0 until labyrinthWidth) {
      val cell = labyrinth.cells(i)(j)
      val x = i * cellSize
      val y = j * cellSize

      if (cell.walls.contains(LeftWall)) {
        gc.strokeLine(x, y, x, y + cellSize)
        gc2.strokeLine(x, y, x, y + cellSize)
      }
      if (cell.walls.contains(TopWall)) {
        gc.strokeLine(x, y, x + cellSize, y)
        gc2.strokeLine(x, y, x + cellSize, y)
      }
      if (cell.walls.contains(RightWall)) {
        gc.strokeLine(x + cellSize, y, x + cellSize, y + cellSize)
        gc2.strokeLine(x + cellSize, y, x + cellSize, y + cellSize)

      }
      if (cell.walls.contains(BottomWall)) {
        gc.strokeLine(x, y + cellSize, x + cellSize, y + cellSize)
        gc2.strokeLine(x, y + cellSize, x + cellSize, y + cellSize)

      }
    }
    for (tunnel <- labyrinth.realTunnels; cell <- labyrinth.tunnels) {
      if (tunnel.cell1.x > cell.x && cell.walls.contains(RightWall) && cell.walls.contains(LeftWall) && cell.x < labyrinthWidth) {
        gc.setStroke(Color.Red)
        gc.strokeLine(cell.x * cellSize + cellSize, cell.y * cellSize , cell.x * cellSize + cellSize , cell.y * cellSize + cellSize)
        gc.strokeLine(cell.x * cellSize, cell.y * cellSize , cell.x * cellSize , cell.y * cellSize + cellSize)
        gc2.setStroke(Color.Red)
        gc2.strokeLine(cell.x * cellSize + cellSize, cell.y * cellSize , cell.x * cellSize + cellSize , cell.y * cellSize + cellSize)
        gc2.strokeLine(cell.x * cellSize, cell.y * cellSize , cell.x * cellSize , cell.y * cellSize + cellSize)
      }
      if (tunnel.cell1.x < cell.x && cell.walls.contains(LeftWall) && cell.walls.contains(RightWall) && cell.x > 0) {
        gc.setStroke(Color.Red)
        gc.strokeLine(cell.x * cellSize, cell.y * cellSize , cell.x * cellSize , cell.y * cellSize + cellSize)
        gc.strokeLine(cell.x * cellSize + cellSize, cell.y * cellSize , cell.x * cellSize + cellSize , cell.y * cellSize + cellSize)
        gc2.setStroke(Color.Red)
        gc2.strokeLine(cell.x * cellSize, cell.y * cellSize , cell.x * cellSize , cell.y * cellSize + cellSize)
        gc2.strokeLine(cell.x * cellSize + cellSize, cell.y * cellSize , cell.x * cellSize + cellSize , cell.y * cellSize + cellSize)
      }
      if (tunnel.cell1.y > cell.y && cell.walls.contains(BottomWall) && cell.walls.contains(TopWall) && cell.y < labyrinthHeight ) {
        gc.setStroke(Color.Red)
        gc.strokeLine(cell.x * cellSize , cell.y * cellSize + cellSize , cell.x * cellSize + cellSize , cell.y * cellSize + cellSize)
        gc.strokeLine(cell.x * cellSize , cell.y * cellSize  , cell.x * cellSize + cellSize , cell.y * cellSize )
        gc2.setStroke(Color.Red)
        gc2.strokeLine(cell.x * cellSize , cell.y * cellSize + cellSize , cell.x * cellSize + cellSize , cell.y * cellSize + cellSize)
        gc2.strokeLine(cell.x * cellSize , cell.y * cellSize  , cell.x * cellSize + cellSize , cell.y * cellSize )
      }
      if (tunnel.cell1.y < cell.y && cell.walls.contains(BottomWall) && cell.walls.contains(TopWall) && cell.y > 0) {
        gc.setStroke(Color.Red)
        gc.strokeLine(cell.x * cellSize , cell.y * cellSize  , cell.x * cellSize + cellSize , cell.y * cellSize )
        gc.strokeLine(cell.x * cellSize , cell.y * cellSize + cellSize , cell.x * cellSize + cellSize , cell.y * cellSize + cellSize)
        gc2.setStroke(Color.Red)
        gc2.strokeLine(cell.x * cellSize , cell.y * cellSize  , cell.x * cellSize + cellSize , cell.y * cellSize )
        gc2.strokeLine(cell.x * cellSize , cell.y * cellSize + cellSize , cell.x * cellSize + cellSize , cell.y * cellSize + cellSize)
      }
      gc.setFill(Color.Red)//the finnish (red box)
      gc.fillOval(labyrinth.exit.x *cellSize +7 , labyrinth.exit.y *cellSize +7, 10,10)
    }
  }

//here are all the different views (panes)

// labyrinth pane
  val root = new Pane {
    minWidth = canvasWidth
    minHeight = canvasHeight
    padding = Insets(10)
  }

//solved labyrinth pane
  val root2 = new Pane {
    minWidth = canvasWidth
    minHeight = canvasHeight
    children.addAll(canvas2)
    padding = Insets(10)
  }

//statr screen pane
  val startPane = new Pane {
    minWidth = canvasWidth
    minHeight = canvasHeight
  }

//choose size pane
  val sizePane = new Pane {
    minWidth = canvasWidth
    minHeight = canvasHeight
  }

//save scene pane
  val saveScene = new Pane {
    minWidth = canvasWidth
    minHeight = canvasHeight
  }

//end scene pane
  val endScene = new Pane {
    minWidth = canvasWidth
    minHeight = canvasHeight
  }

  val savedGameScene = new Pane {
    minWidth = canvasWidth
    minHeight = canvasHeight
  }

  override def start(): Unit = {
    stage = new PrimaryStage {
      title = "Labyrinth"
      scene = new Scene(startPane) {
        fill = Color.Blue

      }
    }

    //start Button
    val startGameButton = new Button {
      onAction = (event) => stage.scene = new Scene(sizePane)
    }

    //save button
    val saveField = new Button(){
      onAction = (event) =>
        (new FileIO).saveGame(labyrinth)
        stage.scene = new Scene(savedGameScene)
    }

    //size choosing field
    val sizeField = new TextField() {
      onAction = (event) =>
        var value = 0

        //trying if the value given is a integer if not then sets the value to 0 so it doesnt do anything
        Try(text.value.toInt).toOption match
          case None => value = 0
          case _ => value = text.value.toInt
        if 10 <= value && value <= 30  then

          labyrinthWidth = value // new Width of the labyrinth in cells
          labyrinthHeight = value  // new Height of the labyrinth in cells
          canvasWidth = cellSize * labyrinthWidth  // new Width of the canvas in pixels
          canvasHeight = cellSize * labyrinthHeight  // new Height of the canvas in pixels
          //set these new values to the labyrinth and everything else
          canvas = new Canvas(canvasWidth, canvasHeight)
          canvas2 = new Canvas(canvasWidth, canvasHeight)
          labyrinth = new Labyrinth(labyrinthWidth, labyrinthHeight)
          labyrinth.generate()
          drawLabyrinth()
          rectangle.x = labyrinth.start.x * cellSize +7
          rectangle.y = labyrinth.start.y * cellSize +7

          // checks if the blue rectangle can move up and if there is a tunnel it goes through it
          def moveUp() =
            if ((rectangle.y - 7)/cellSize > 0).value && !labyrinth.cells(((rectangle.x - 7)/cellSize).toInt)(((rectangle.y - 7)/cellSize).toInt).hasWallWCell(labyrinth.cells(((rectangle.x - 7)/cellSize).toInt)((((rectangle.y - 7)/cellSize)-1).toInt)) then

              rectangle.y = (rectangle.y-20).toDouble
            else if ((rectangle.y - 7)/cellSize > 0).value && labyrinth.cells(((rectangle.x - 7)/cellSize).toInt)(((rectangle.y - 7)/cellSize).toInt).hasWallWCell(labyrinth.cells(((rectangle.x - 7)/cellSize).toInt)((((rectangle.y - 7)/cellSize)-1).toInt)) && labyrinth.tunnels.contains(labyrinth.cells(((rectangle.x - 7)/cellSize).toInt)((((rectangle.y - 7)/cellSize)-1).toInt)) && labyrinth.cells(((rectangle.x - 7)/cellSize).toInt)((((rectangle.y - 7)/cellSize)-1).toInt).hasWallWCell(labyrinth.cells(((rectangle.x - 7)/cellSize).toInt)((((rectangle.y - 7)/cellSize)-2).toInt)) then
              rectangle.y = (rectangle.y-40).toDouble

          // checks if the blue rectangle can move down and if there is a tunnel it goes through it
          def moveDown() =
            if ((rectangle.y - 7)/cellSize < labyrinth.height - 1).value && !labyrinth.cells(((rectangle.x - 7)/cellSize).toInt)(((rectangle.y - 7)/cellSize).toInt).hasWallWCell(labyrinth.cells(((rectangle.x - 7)/cellSize).toInt)((((rectangle.y - 7)/cellSize) +1).toInt)) then

              rectangle.y = (rectangle.y+20).toDouble
            else if ((rectangle.y - 7)/cellSize < labyrinth.height - 1).value && labyrinth.cells(((rectangle.x - 7)/cellSize).toInt)(((rectangle.y - 7)/cellSize).toInt).hasWallWCell(labyrinth.cells(((rectangle.x - 7)/cellSize).toInt)((((rectangle.y - 7)/cellSize) +1).toInt)) && labyrinth.tunnels.contains(labyrinth.cells(((rectangle.x - 7)/cellSize).toInt)((((rectangle.y - 7)/cellSize) +1).toInt)) && labyrinth.cells(((rectangle.x - 7)/cellSize).toInt)((((rectangle.y - 7)/cellSize) +1).toInt).hasWallWCell(labyrinth.cells(((rectangle.x - 7)/cellSize).toInt)((((rectangle.y - 7)/cellSize) +2).toInt)) then
              rectangle.y = (rectangle.y+40).toDouble

          // checks if the blue rectangle can move left and if there is a tunnel it goes through it
          def moveLeft() =
            if ((rectangle.x - 7)/cellSize > 0).value && !labyrinth.cells(((rectangle.x - 7)/cellSize).toInt)(((rectangle.y - 7)/cellSize).toInt).hasWallWCell(labyrinth.cells((((rectangle.x - 7)/cellSize) -1).toInt)(((rectangle.y - 7)/cellSize).toInt)) then

              rectangle.x = (rectangle.x-20).toDouble

            else if ((rectangle.x - 7)/cellSize > 0).value && labyrinth.cells(((rectangle.x - 7)/cellSize).toInt)(((rectangle.y - 7)/cellSize).toInt).hasWallWCell(labyrinth.cells((((rectangle.x - 7)/cellSize) -1).toInt)(((rectangle.y - 7)/cellSize).toInt)) && labyrinth.tunnels.contains(labyrinth.cells((((rectangle.x - 7)/cellSize) -1).toInt)(((rectangle.y - 7)/cellSize).toInt)) && labyrinth.cells((((rectangle.x - 7)/cellSize) -1).toInt)(((rectangle.y - 7)/cellSize).toInt).hasWallWCell(labyrinth.cells((((rectangle.x - 7)/cellSize) -2).toInt)(((rectangle.y - 7)/cellSize).toInt)) then
              rectangle.x = (rectangle.x-40).toDouble

          // checks if the blue rectangle can move right and if there is a tunnel it goes through it
          def moveRight() =
            if ((rectangle.x - 7)/cellSize < labyrinth.width - 1).value && !labyrinth.cells(((rectangle.x - 7)/cellSize).toInt)(((rectangle.y - 7)/cellSize).toInt).hasWallWCell(labyrinth.cells((((rectangle.x - 7)/cellSize)+1).toInt)(((rectangle.y - 7)/cellSize).toInt)) then

              rectangle.x = (rectangle.x+20).toDouble

            else if ((rectangle.x - 7)/cellSize < labyrinth.width - 1).value && labyrinth.cells(((rectangle.x - 7)/cellSize).toInt)(((rectangle.y - 7)/cellSize).toInt).hasWallWCell(labyrinth.cells((((rectangle.x - 7)/cellSize)+1).toInt)(((rectangle.y - 7)/cellSize).toInt)) && labyrinth.tunnels.contains(labyrinth.cells((((rectangle.x - 7)/cellSize)+1).toInt)(((rectangle.y - 7)/cellSize).toInt)) && labyrinth.cells((((rectangle.x - 7)/cellSize)+1).toInt)(((rectangle.y - 7)/cellSize).toInt).hasWallWCell(labyrinth.cells((((rectangle.x - 7)/cellSize)+2).toInt)(((rectangle.y - 7)/cellSize).toInt))  then
              rectangle.x = (rectangle.x+40).toDouble

          //set the scene to be the labyrinth
          stage.scene = new Scene(root) {
          fill = Color.White

          //check which key is pressed and work accordingly
          onKeyPressed = (e:KeyEvent) => {
            if e.code == KeyCode.Left then
              moveLeft()
              //if exit then the end
              if (rectangle.x < labyrinth.exit.x *cellSize +14).value && (rectangle.x > labyrinth.exit.x *cellSize).value && (rectangle.y < labyrinth.exit.y *cellSize +14).value && (rectangle.y > labyrinth.exit.y *cellSize).value then stage.scene = new Scene(endScene)
            if e.code == KeyCode.Up then
              moveUp()
              //if exit then the end
              if (rectangle.x < labyrinth.exit.x *cellSize +14).value && (rectangle.x > labyrinth.exit.x *cellSize).value && (rectangle.y < labyrinth.exit.y *cellSize +14).value && (rectangle.y > labyrinth.exit.y *cellSize).value then stage.scene = new Scene(endScene)
            if e.code == KeyCode.Down then
              moveDown()
              //if exit then the end
              if (rectangle.x < labyrinth.exit.x *cellSize +14).value && (rectangle.x > labyrinth.exit.x *cellSize).value && (rectangle.y < labyrinth.exit.y *cellSize +14).value && (rectangle.y > labyrinth.exit.y *cellSize).value then stage.scene = new Scene(endScene)
            if e.code == KeyCode.Right then
              moveRight()
              //if exit then the end
              if (rectangle.x < labyrinth.exit.x *cellSize +14).value && (rectangle.x > labyrinth.exit.x *cellSize).value && (rectangle.y < labyrinth.exit.y *cellSize +14).value && (rectangle.y > labyrinth.exit.y *cellSize).value then stage.scene = new Scene(endScene)
            if e.code == KeyCode.S then
              val solver = new MazeSolver(labyrinth)//implement the solver
              val solvedPath = solver.solve(labyrinth.cells(((rectangle.x - 7)/cellSize).toInt)(((rectangle.y - 7)/cellSize).toInt), labyrinth.exit)// list of cells that guide the user to the exit
              for (cell <- solvedPath){
                gc2.setFill(Color.Blue)
                gc2.fillOval(((rectangle.x - 7)/cellSize * cellSize+4).toInt,((rectangle.y - 7)/cellSize * cellSize+4).toInt, 10, 10)
                gc2.setFill(Color.Green)
                gc2.fillOval(cell.x * cellSize+4,cell.y * cellSize+4, 7, 7)// all those cells are marked with a green ball
              }
              stage.scene = new Scene(root2)// if we press s then show solved screen
            if e.code == KeyCode.T then stage.scene = new Scene(saveScene)// if we press save show saving scene
          }

        }
    }

    //all the labels used in different scenes
    val endLabel = new Label("You reached the end WELL DONE!")
    val tunnelLabel = new Label("The red walls work like portals you can move through the red walls (skipping a cell) if they are against each other. Use the arrows to move, To solve Press S and to save the game press T. You clear The labyrinth if you get to the Red Box")
    val sizeGameLabel = new Label("Choose size")
    val sizeLabel = new Label("write some number between 10-30. This number will be the size of the labyrinth n*n")
    val startGameLabel = new Label("Labyrinth Game")
    val solutionLabel = new Label("Here is the Solution (marked in green dots). Better luck next time!")
    val savedGameLabel = new Label("Game saved succesfully")

    //add everything needed to save scene
    saveScene.children.addAll(saveField)
    saveField.text = "Save Game"
    saveField.setLayoutX((canvasWidth-100)/2)
    saveField.setLayoutY((canvasHeight-50)/2)

    //add everything needed to end scene
    endScene.children = endLabel
    endLabel.setLayoutX((canvasWidth-380)/2)
    endLabel.setLayoutY((canvasHeight-100)/2)
    endLabel.font = new Font("Impact",30)

    //add everything needed to labyrinth scene
    root.children.addAll(canvas, rectangle, tunnelLabel)
    tunnelLabel.setLayoutX(650)
    tunnelLabel.setLayoutY(100)
    tunnelLabel.prefWidth = 300
    tunnelLabel.wrapText = true
    tunnelLabel.font = new Font(20)

    //add everything needed to solved scene
    root2.children.addAll(solutionLabel)
    solutionLabel.setLayoutX(650)
    solutionLabel.setLayoutY(100)
    solutionLabel.prefWidth = 300
    solutionLabel.wrapText = true
    solutionLabel.font = new Font(20)

    //add everything needed to start scene
    startPane.children.addAll(startGameButton,startGameLabel)
    startGameButton.text = "Start Game"
    startGameButton.setLayoutX((canvasWidth -100)/2)
    startGameButton.setLayoutY((canvasHeight -50)/2)
    startGameLabel.setLayoutX((canvasWidth-210)/2)
    startGameLabel.setLayoutY((canvasHeight-200)/2)
    startGameLabel.font = new Font("Impact",30)

    //add everything needed to size choosing scene
    sizePane.children.addAll(sizeField, sizeLabel, sizeGameLabel)
    sizeField.promptText = "Labyrinth Size"
    sizeField.setLayoutX((canvasWidth -150 )/2)
    sizeField.setLayoutY((canvasHeight -50 )/2)
    sizeLabel.setLayoutX((canvasWidth -250 )/2)
    sizeLabel.setLayoutY((canvasHeight )/2)
    sizeLabel.prefWidth = 300
    sizeLabel.wrapText = true
    sizeLabel.font = new Font(15)
    sizeGameLabel.setLayoutX((canvasWidth-150)/2)
    sizeGameLabel.setLayoutY((canvasHeight-200)/2)
    sizeGameLabel.font = new Font("Impact",30)

    //add the savedGameLabel to savedGameScene
    savedGameScene.children = savedGameLabel
    savedGameLabel.setLayoutX((canvasWidth-280)/2)
    savedGameLabel.setLayoutY((canvasHeight-100)/2)
    savedGameLabel.font = new Font("Impact",30)


  }
}