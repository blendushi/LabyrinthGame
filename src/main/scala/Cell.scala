import scala.collection.mutable.ListBuffer

class Cell (val x:Int , val y:Int){
  var visited: Boolean = false
  var walls: Set[Wall] = Set(LeftWall, RightWall, TopWall , BottomWall)

  def isVisited: Boolean = visited

  def markVisited: Unit = visited = true 

  //checks if this cell has wall with other cell 
  def hasWallWCell(other: Cell):Boolean =
    if this.x == other.x then
      if this.y > other.y then walls.contains(TopWall)
      else walls.contains(BottomWall)
    else if this.y == other.y then
      if this.x > other.x then walls.contains(LeftWall)
      else walls.contains(RightWall)
    else false

  //removes wall in between this cell and other cell
  def removeWallWCell(other: Cell): Unit =
    if this.x == other.x then
      if this.y > other.y then
        this.walls -= TopWall
        other.walls -= BottomWall
      else
        this.walls -= BottomWall
        other.walls -= TopWall
    else if this.y == other.y then
      if this.x > other.x then
        this.walls -= LeftWall
        other.walls -= RightWall
      else
        this.walls -= RightWall
        other.walls -= LeftWall

  //get unvisited neighbours
  def getUnvisitedNeighbours(cells: Array[Array[Cell]]): List[Cell] =
    getNeighbours(cells).filter(cell => !cell.visited)

  //get all the neighbours
  def getNeighbours(cells: Array[Array[Cell]]): List[Cell] =
    val neighbours = ListBuffer[Cell]()

    if x > 0 then neighbours += cells(x-1)(y)
    if x < cells(0).length - 1 then neighbours += cells(x+1)(y)
    if y > 0 then neighbours += cells(x)(y-1)
    if y < cells.length - 1 then neighbours += cells(x)(y+1)

    neighbours.toList
  
  //make a tunnel between this cell and other cell
  def makeTunnel(other: Cell) =
    new Tunnel(this,other)
}

sealed trait Wall
case object LeftWall extends Wall
case object RightWall extends Wall
case object TopWall extends Wall
case object BottomWall extends Wall


