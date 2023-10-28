package chipvm.logic

import scala.collection.immutable

case class Display(onCells : immutable.Set[Point] = immutable.Set[Point](),
                   collision : Boolean = false) {
  def clear() : Display = {
    this.copy(immutable.Set[Point](), collision)
  }

  def clearCollision() : Display = {
    this.copy(collision = false)
  }

  def flip(point: Point) : Display = {
    val isCellOn = onCells.contains(point)
    val newCollision = isCellOn || collision

    val newCells = {
      if (isCellOn) onCells.-(point)
      else          onCells.+(point)
    }

    this.copy(newCells, newCollision)
  }

  def flip(x: Int, y: Int) : Display =
    flip(Point(x, y))

  def at(point: Point): CellType = {
    if (onCells.contains(point)) Fill
    else Empty
  }
}