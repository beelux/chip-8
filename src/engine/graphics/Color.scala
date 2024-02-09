// scalastyle:off

package engine.graphics

/** Color in red green blue, where each color value is in the range 0-255 */
case class Color(red: Float, green: Float, blue: Float, alpha: Float) {
  // This is called on new Color(r, g, b)
  def this(red: Float, green: Float, blue: Float) = this(red, green, blue, 255)
}

/** Color companion object */
object Color {
  // This is called on Color(r, g, b) (without new)
  def apply(red: Float, green: Float, blue: Float): Color = new Color(red, green, blue)
}
