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
  val updateTimer = new UpdateTimer(ChipVMLogic.FramesPerSecond.toFloat)
  val gridDims : Dimensions = ChipVMLogic.DefaultDims
  val widthInPixels: Int = (WidthCellInPixels * gridDims.width).ceil.toInt
  val heightInPixels: Int = (HeightCellInPixels * gridDims.height).ceil.toInt
  val screenArea: Rectangle = Rectangle(Point(0, 0), widthInPixels.toFloat, heightInPixels.toFloat)
  val widthPerCell: Float = screenArea.width / gridDims.width
  val heightPerCell: Float = screenArea.height / gridDims.height

  override def draw(): Unit = {
    updateState()
    drawGrid()
  }

  def drawGrid(): Unit = {
    for (p <- gridDims.allPointsInside) {
      drawCell(getCell(p), gameLogic.getCellType(p))
    }

    def getCell(p : GridPoint): Rectangle = {
      val leftUp = Point(screenArea.left + p.x * widthPerCell,
        screenArea.top + p.y * heightPerCell)
      Rectangle(leftUp, widthPerCell, heightPerCell)
    }

    def drawCell(area: Rectangle, cellType: CellType): Unit = {
      val color = blockToColor(cellType)
      setFillColor(color)
      drawRectangle(area)
    }

    def blockToColor(color: CellType): Color =
      color match {
        case Fill => Color.White
        case Empty => Color.Black
      }
  }

  override def keyPressed(event: KeyEvent): Unit = {
    event.getKeyCode match {
      case VK_1   => println("1")
      case VK_2   => println("2")
      case VK_3   => println("3")
      case VK_4   => println("4")
      case VK_Q   => println("Q")
      case VK_W   => println("W")
      case VK_E   => println("E")
      case VK_R   => println("R")
      case VK_A   => println("A")
      case VK_S   => println("S")
      case VK_D   => println("D")
      case VK_F   => println("F")
      case VK_Z   => println("Z")
      case VK_X   => println("X")
      case VK_C   => println("C")
      case VK_V   => println("V")
      case _      => ()
    }
  }

  override def settings(): Unit = {
    pixelDensity(displayDensity())
    size(widthInPixels, heightInPixels)
  }

  override def setup(): Unit = {
    updateTimer.init()
  }

  def updateState(): Unit = {
    if (updateTimer.timeForNextFrame()) {
      gameLogic.moveDown()
      updateTimer.advanceFrame()
    }
  }
}

object ChipVM {
  val WidthCellInPixels: Double = 15 * ChipVMLogic.DrawSizeFactor
  val HeightCellInPixels: Double = WidthCellInPixels

  def main(args:Array[String]): Unit = {
    PApplet.main("chipvm.game.ChipVM")
  }
}