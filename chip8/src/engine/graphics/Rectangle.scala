// DO NOT MODIFY FOR BASIC SUBMISSION
// scalastyle:off

package engine.graphics

case class Rectangle(leftUp: Point, width: Float, height: Float) {
  def top: Float = leftUp.y
  def bottom: Float = top + height
  def left: Float = leftUp.x
  def leftDown: Point = Point(left, bottom)
  def right: Float = left + width
  def rightDown: Point = Point(right, bottom)
  def rightUp: Point = Point(right, top)
  def center: Point = Point(centerX, centerY)
  def centerX: Float = leftUp.x + width / 2
  def centerY: Float = leftUp.y + height / 2
  def centerUp: Point = Point(centerX, top)
  def centerDown: Point = Point(centerX, bottom)
  def centerLeft: Point = Point(left, centerY)
  def centerRight: Point = Point(right, centerY)
  def trianglePointingUp: Triangle = Triangle(centerUp, leftDown, rightDown)
  def trianglePointingDown: Triangle = Triangle(centerDown, leftUp, rightUp)
  def trianglePointingLeft: Triangle = Triangle(centerLeft, rightDown, rightUp)
  def trianglePointingRight: Triangle = Triangle(centerRight, leftDown, leftUp)
  def grow(factor : Float) : Rectangle = {
    val newWidth = width * factor
    val newHeight = height * factor
    val diffX = (newWidth - width) / 2
    val diffY = (newHeight - height) / 2
    Rectangle(Point(left - diffX, top - diffY ), newWidth,newHeight)

  }

  def contains(p : Point) : Boolean =
    p.x >= left && p.x < right && p.y >= top && p.y < bottom

  def localPoint(globalPoint : Point) : Point =
    globalPoint - leftUp
}