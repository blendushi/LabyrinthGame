import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.Random

class Labyrinth(val width: Int, val height: Int) {

  var cells: Array[Array[Cell]] = Array.ofDim[Cell](height,width)
  var tunnels: Array[Cell] = Array[Cell]()
  var realTunnels = Array[Tunnel]()
  var start: Cell = _
  var exit: Cell = _

  def generate(): Unit =
    //Generate the Cells
    for i <- 0 until height do
      for j <- 0 until width do
        cells(i)(j) = new Cell(i,j)
    //Mark the start and the exit cells
    start = cells(height/2)(width/2)
    exit = cells(Random.nextInt(height-1))(width-1)

    //Generates the Labyrinth using growing tree algorithm
    var currentCell: Cell = start
    var unvisitedCells: List[Cell] = cells.flatten.toList.filter(_ != start)
    var visitedCells: ListBuffer[Cell] = ListBuffer[Cell](start)

    //check is visitedcells empty, if not, proceed
    while !visitedCells.isEmpty do
      currentCell.markVisited // mark current cell visited
      if currentCell != start then visitedCells += currentCell //if current cell isn't the start cell addit to visited cells

      val unvisitedNeighbours = currentCell.getUnvisitedNeighbours(cells)

      //check if unvisitedneighbours is empty, if not, proceed
      if !unvisitedNeighbours.isEmpty then

        val nextCell = unvisitedNeighbours(Random.nextInt(unvisitedNeighbours.length))//choose next cell randomly of unvisitedneighbours

        val neighbors = nextCell.getNeighbours(cells)//get neighbours of the new cell
        val visitedNeighbors = neighbors.filter(_.isVisited)// filter all the visited
        val visitedNeighbor = visitedNeighbors(Random.nextInt(visitedNeighbors.length))// choose randomly from the visited neighbours

        //here takes place the tunneling
        if !visitedNeighbors.isEmpty && Random.nextDouble() < 0.6 && !tunnels.contains(nextCell) && nextCell.hasWallWCell(currentCell) && nextCell.hasWallWCell(visitedNeighbor) then
          if (currentCell.x - 2 == visitedNeighbor.x || currentCell.x + 2 == visitedNeighbor.x || currentCell.y - 2 == visitedNeighbor.y || currentCell.y + 2 == visitedNeighbor.y) && (currentCell.x-2) >= 0 && (currentCell.x+2) <= width && (currentCell.y-2) >= 0 && (currentCell.y+2) <= height && (visitedNeighbor.x-2) >= 0 && (visitedNeighbor.x+2) <= width && (visitedNeighbor.y-2) >= 0 && (visitedNeighbor.y+2) <= height  then
            realTunnels = realTunnels :+ currentCell.makeTunnel(visitedNeighbor)
            tunnels = tunnels :+ nextCell
            currentCell = visitedNeighbor
        else
          currentCell.removeWallWCell(nextCell)
          currentCell = nextCell
      else
        visitedCells -= currentCell
        visitedCells = visitedCells.filter(_ != currentCell)
        if !visitedCells.isEmpty then
          currentCell = visitedCells(Random.nextInt(visitedCells.length))
        

  def isExit(x:Int , y:Int): Boolean = if x == exit.x && y == exit.y then true else false //is current position the ending

}
