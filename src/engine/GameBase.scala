// DO NOT MODIFY FOR BASIC SUBMISSION
// scalastyle:off

package engine

import engine.graphics.{Color, Rectangle}
import processing.core.{PApplet}

class GameBase   extends PApplet {
  // inner class: can call current time of outer class
  class UpdateTimer(val frequency: Float) {

    val tickDuration: Float = 1000 / frequency // ms
    var nextTick: Float = Float.MaxValue

    def init(): Unit = nextTick = currentTime() + tickDuration
    def timeForTick(): Boolean = currentTime() >= nextTick
    def advanceFrame(): Unit = nextTick = nextTick + tickDuration

  }

  // ===Processing Wrappers & Abstractions===

  /** An alias for the obscurely named function millis()
    *
    * @return Current time in milliseconds since stating the program.
    */
  def currentTime(): Int = millis()

  def drawRectangle(r: Rectangle): Unit =
    rect(r.left,r.top, r.width, r.height)

  def setFillColor(c: Color): Unit =
    fill(c.red, c.green, c.blue, c.alpha)
}
