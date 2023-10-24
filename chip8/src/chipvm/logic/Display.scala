package chipvm.logic

case class Display(onCells : Set[Point] = Set[Point](),
                   collision : Boolean = false) {
  def clear() : Display = {
    new Display
  }

  def clearCollision() : Display = {
    this.copy(collision = false)
  }

  def flip(point: Point) : Display = {
    val isCellOn = onCells.contains(point)
    val newCollision = isCellOn || collision

    val newCells = {
      if (isCellOn) onCells - point
      else          onCells + point
    }

    this.copy(newCells, newCollision)
  }
}