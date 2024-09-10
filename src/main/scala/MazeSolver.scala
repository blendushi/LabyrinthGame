import scala.collection.mutable.ListBuffer

class MazeSolver(val labyrinth: Labyrinth) {
  private var path: ListBuffer[Cell] = ListBuffer.empty

  def solve(startingCell: Cell, endingCell: Cell): List[Cell] = {
    // Reset the path to empty
    path = ListBuffer.empty
    // Mark all cells as unvisited
    labyrinth.cells.foreach(_.foreach(_.visited = false))
    // Call the recursive solve function
    solveRecursive(startingCell, endingCell)
    // Return the path as a list
    path.toList
  }

  private def solveRecursive(currentCell: Cell, endingCell: Cell): Boolean = {
    // Mark the current cell as visited
    currentCell.markVisited
    // Add the current cell to the path
    path += currentCell
    // If the current cell is the ending cell, we're done
    if (currentCell == endingCell) {
      return true
    }
    // Find all unvisited neighboring cells
    val neighbors = currentCell.getUnvisitedNeighbours(labyrinth.cells).filter(a => !currentCell.hasWallWCell(a))
      .filter(_.isVisited == false)
    // Sort the neighbors randomly
    val randomNeighbors = scala.util.Random.shuffle(neighbors)
    // For each unvisited neighbor, try to solve the labyrinth from that cell
    for (neighbor <- randomNeighbors) {
      if (solveRecursive(neighbor, endingCell)) {
        // If the labyrinth was solved from the neighbor, we're done
        return true
      }
    }
    // If we reach this point, there are no unvisited neighbors that lead to the ending cell, so we need to backtrack
    // Remove the current cell from the path
    path.remove(path.length - 1)
    // Mark the current cell as unvisited
    currentCell.visited = false 
    // Return false to indicate that we didn't solve the labyrinth from this cell
    false
  }
}