// scalastyle:off

package engine.graphics

case class Rectangle(leftUp: Point, width: Float, height: Float) {
  def top: Float = leftUp.y
  def bottom: Float = top + height
  def left: Float = leftUp.x
  def right: Float = left + width

  def contains(p : Point) : Boolean =
    p.x >= left && p.x < right && p.y >= top && p.y < bottom
}