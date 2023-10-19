package tetris.logic

// you can alter this file!

case class Dimensions(width : Int, height : Int) {
  // scanned from left to right, top to bottom
  def allPointsInside : Seq[Point] =
    for(y <- 0 until height; x <- 0 until width)
      yield Point(x,y)
}
