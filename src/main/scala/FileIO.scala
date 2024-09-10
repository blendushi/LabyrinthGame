import java.io.{BufferedWriter, File, FileNotFoundException, FileWriter, IOException}
import scala.util.{Failure, Success}
import scala.io.Source
import scala.collection.mutable

class FileIO {

  /**
   * Saves the current game state to a file.
   *
   * @throws IOException if an I/O error occurs.
   */
  @throws(classOf[IOException])
  def saveGame(labyrinth: Labyrinth) = {
    val file = new File("C:/Users/blend/OneDrive - Aalto University/labyrinthGame/saves/game.txt")
    val writer = new BufferedWriter(new FileWriter(file))
    try {
      // Write the labyrinth size
      writer.write(s"labyrinth w:${labyrinth.width},h:${labyrinth.height}\n")

      // Write the state of each cell
      labyrinth.cells.foreach { row =>
        row.foreach { cell =>
          //val state = if (cell.visited) "1" else "0"
          val walls = cell.walls.map(wallToString).mkString(",")
          val x = cell.x
          val y = cell.y
          writer.write(s"cell x:$x,y:$y,walls:$walls\n")
        }
      }

      // Write the list of real tunnels
      labyrinth.realTunnels.foreach { tunnel =>
        writer.write(s"tunnel cell 1 x: ${tunnel.cell1.x},tunnel cell 1 y: ${tunnel.cell1.y},tunnel cell 2 x: ${tunnel.cell2.x},tunnel cell 2 y: ${tunnel.cell2.y}\n")
      }

      writer.write(s"rectangle x: ${((LabyrinthUI.rectangle.x-7)/LabyrinthUI.cellSize).toInt},y: ${((LabyrinthUI.rectangle.y-7)/LabyrinthUI.cellSize).toInt}")
      

    } finally {
      writer.close()
    }
  }
  
  private def wallToString(wall: Wall): String = wall match {
      case TopWall => "T"
      case RightWall => "R"
      case BottomWall => "B"
      case LeftWall => "L"
    }
}
