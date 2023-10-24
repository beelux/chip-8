// scalastyle:off
package chipvm.game

import java.awt.event
import java.awt.event.KeyEvent._

import engine.GameBase
import engine.graphics.{Color, Point, Rectangle}
import processing.core.{PApplet, PConstants}
import processing.event.KeyEvent
import chipvm.logic._
import chipvm.game.ChipVM._
import chipvm.logic.{Point => GridPoint}

class ChipVM extends GameBase {
  var gameLogic : ChipVMLogic = ChipVMLogic()
  val newTimer = new java.util.Timer()
  val gridDims : Dimensions = ChipVMLogic.DefaultDims
  val widthInPixels: Int = (WidthCellInPixels * gridDims.width).ceil.toInt
  val heightInPixels: Int = (HeightCellInPixels * gridDims.height).ceil.toInt
  val screenArea: Rectangle = Rectangle(Point(0, 0), widthInPixels.toFloat, heightInPixels.toFloat)
  val widthPerCell: Float = screenArea.width / gridDims.width
  val heightPerCell: Float = screenArea.height / gridDims.height
  var timer: Int = 10

  override def draw(): Unit = {
      if (updateState())
        drawGrid()
  }

  def drawGrid(): Unit = {
    setFillColor(Color.Black)
    drawRectangle(screenArea)

    setFillColor(Color.White)
    for (p <- gameLogic.display.onCells) {
      drawRectangle(getCell(p))
    }

    def getCell(p : GridPoint): Rectangle = {
      val leftUp = Point(screenArea.left + p.x * widthPerCell,
        screenArea.top + p.y * heightPerCell)
      Rectangle(leftUp, widthPerCell, heightPerCell)
    }
  }

  override def keyPressed(event: KeyEvent): Unit = {
    gameLogic.keyPressed(event.getKeyCode)
  }

  override def settings(): Unit = {
    pixelDensity(displayDensity())
    size(widthInPixels, heightInPixels)
  }

  override def setup(): Unit = {

    gameLogic.readROM("roms/1-chip8-logo.ch8")
    this.frameRate(600)

    //instructionTimer.init()
    //timerTimer.init()
  }

  def updateState(): Boolean = {
    if(timer==0) {
      timer=10
      gameLogic.timerTick()
    } else timer -= 1

    val result = gameLogic.loop
    result
  }
}

object ChipVM {
  val WidthCellInPixels: Double = 15 * ChipVMLogic.DrawSizeFactor
  val HeightCellInPixels: Double = WidthCellInPixels

  def main(args:Array[String]): Unit = {
    PApplet.main("chipvm.game.ChipVM")
  }
}