// DO NOT MODIFY FOR BASIC SUBMISSION
// scalastyle:off

package engine.graphics

case class Point(x: Float, y: Float) {
  def +(rhs: Point) : Point = Point(x + rhs.x, y + rhs.y)
  def -(rhs : Point) : Point = Point(x - rhs.x, y - rhs.y)
}
