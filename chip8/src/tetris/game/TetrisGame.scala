// DO NOT MODIFY FOR BASIC SUBMISSION
// scalastyle:off

package tetris.game

import java.awt.event
import java.awt.event.KeyEvent._

import engine.GameBase
import engine.graphics.{Color, Point, Rectangle}
import processing.core.{PApplet, PConstants}
import processing.event.KeyEvent
import tetris.logic._
import tetris.game.TetrisGame._
import tetris.logic.{Point => GridPoint}

class TetrisGame extends GameBase {

  var gameLogic : TetrisLogic = TetrisLogic()
  val updateTimer = new UpdateTimer(TetrisLogic.FramesPerSecond.toFloat)
  val gridDims : Dimensions = gameLogic.gridDims
  val widthInPixels: Int = (WidthCellInPixels * gridDims.width).ceil.toInt
  val heightInPixels: Int = (HeightCellInPixels * gridDims.height).ceil.toInt
  val screenArea: Rectangle = Rectangle(Point(0, 0), widthInPixels.toFloat, heightInPixels.toFloat)

  override def draw(): Unit = {
    updateState()
    drawGrid()
    if (gameLogic.isGameOver) drawGameOverScreen()
  }

  def drawGameOverScreen(): Unit = {
    setFillColor(Color.Red)
    drawTextCentered("GAME OVER!", 20, screenArea.center)
  }

  def drawGrid(): Unit = {

    val widthPerCell = screenArea.width / gridDims.width
    val heightPerCell = screenArea.height / gridDims.height

    for (p <- gridDims.allPointsInside) {
      drawCell(getCell(p), gameLogic.getCellType(p))
    }

    def getCell(p : GridPoint): Rectangle = {
      val leftUp = Point(screenArea.left + p.x * widthPerCell,
        screenArea.top + p.y * heightPerCell)
      Rectangle(leftUp, widthPerCell, heightPerCell)
    }

    def drawCell(area: Rectangle, tetrisColor: CellType): Unit = {
      val color = tetrisBlockToColor(tetrisColor)
      setFillColor(color)
      drawRectangle(area)
    }

  }

  /** Method that calls handlers for different key press events.
   * You may add extra functionality for other keys here.
   * See [[event.KeyEvent]] for all defined keycodes.
   *
   * @param event The key press event to handle
   */
  override def keyPressed(event: KeyEvent): Unit = {

    event.getKeyCode match {
      case VK_A     => gameLogic.rotateLeft()
      case VK_S     => gameLogic.rotateRight()
      case VK_UP    => gameLogic.rotateRight()
      case VK_DOWN  => gameLogic.moveDown()
      case VK_LEFT  => gameLogic.moveLeft()
      case VK_RIGHT => gameLogic.moveRight()
      case VK_SPACE => gameLogic.doHardDrop()
      case _        => ()
    }

  }

  override def settings(): Unit = {
    pixelDensity(displayDensity())
    // If line below gives errors try size(widthInPixels, heightInPixels, PConstants.P2D)
    size(widthInPixels, heightInPixels)
  }

  override def setup(): Unit = {
    // Fonts are loaded lazily, so when we call text()
    // for the first time, there is significant lag.
    // This prevents it from happening during gameplay.
    text("", 0, 0)

    // This should be called last, since the game
    // clock is officially ticking at this point
    updateTimer.init()
  }

  def updateState(): Unit = {
    if (updateTimer.timeForNextFrame()) {
      gameLogic.moveDown()
      updateTimer.advanceFrame()
    }
  }

  def tetrisBlockToColor(color: CellType): Color =
    color match {
      case ICell => Color.LightBlue
      case OCell => Color.Yellow
      case LCell => Color.Orange
      case JCell => Color.Blue
      case SCell => Color.Green
      case Empty  => Color.Black
      case TCell => Color.Purple
      case ZCell => Color.Red
    }
}

object TetrisGame {


  val WidthCellInPixels: Double = 15 * TetrisLogic.DrawSizeFactor
  val HeightCellInPixels: Double = WidthCellInPixels

  def main(args:Array[String]): Unit = {
    PApplet.main("tetris.game.TetrisGame")
  }

}