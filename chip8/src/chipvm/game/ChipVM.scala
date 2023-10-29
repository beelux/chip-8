// scalastyle:off
package chipvm.game

import engine.GameBase
import engine.graphics.{Point, Rectangle}
import processing.core.PApplet
import processing.event.KeyEvent
import chipvm.logic._
import chipvm.game.ChipVM._
import chipvm.logic.instructions.Instruction._
import chipvm.logic.{Point => GridPoint}

class ChipVM extends GameBase {
  var gameLogic : ChipVMLogic = ChipVMLogic()
  val updateTimer = new UpdateTimer(ChipVMLogic.TimerFrequency.toFloat)
  val gridDims : Dimensions = ChipVMLogic.DefaultDims
  val widthInPixels: Int = (WidthCellInPixels * gridDims.width).ceil.toInt
  val heightInPixels: Int = (HeightCellInPixels * gridDims.height).ceil.toInt
  val screenArea: Rectangle = Rectangle(Point(0, 0), widthInPixels.toFloat, heightInPixels.toFloat)
  val widthPerCell: Float = screenArea.width / gridDims.width
  val heightPerCell: Float = screenArea.height / gridDims.height


  override def draw(): Unit = {
      if (updateState())
        drawDisplay()
  }

  private def drawDisplay(): Unit = {
    setFillColor(ChipVMLogic.BackgroundColor)
    drawRectangle(screenArea)

    setFillColor(ChipVMLogic.ForegroundColor)
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
    gameLogic = gameLogic.keyPressed(event.getKeyCode)
  }

  override def keyReleased(event: KeyEvent): Unit = {
    gameLogic = gameLogic.keyReleased(event.getKeyCode)
  }

  override def settings(): Unit = {
    pixelDensity(displayDensity())
    size(widthInPixels, heightInPixels)
  }

  override def setup(): Unit = {
    noStroke() // Disable stroke around rectangle

    // Demo roms - Kept in source code as a list
    //gameLogic = gameLogic.readROM("roms/2-ibm-logo-1.ch8")
    //gameLogic = gameLogic.readROM("roms/3-corax+.ch8")
    //gameLogic = gameLogic.readROM("roms/4-flags.ch8")
    //gameLogic = gameLogic.readROM("roms/5-quirks.ch8")
    //gameLogic = gameLogic.readROM("roms/6-keypad.ch8")
    //gameLogic = gameLogic.readROM("roms/7-fontTest.ch8")

    gameLogic = gameLogic.readROM()
    this.frameRate(ChipVMLogic.InstructionsPerSecond.toFloat)

    drawDisplay()
    updateTimer.init()
  }

  /**
   * Updates the state of the game
   * @return if the display was modified and needs to be redrawn
   */
  private def updateState(): Boolean = {
    gameLogic = checkTimers()

    val instruction = gameLogic.fetchDecode()
    gameLogic = gameLogic.execute(instruction)

    modifiesDisplay(instruction)
  }

  private def checkTimers(): ChipVMLogic = {
    if (updateTimer.timeForTick()) {
      val newLogic = gameLogic.timerTick()
      updateTimer.advanceFrame()

      newLogic
    } else gameLogic
  }
}

object ChipVM {
  val WidthCellInPixels: Double = 15 * ChipVMLogic.DrawSizeFactor
  val HeightCellInPixels: Double = WidthCellInPixels

  def main(args:Array[String]): Unit = {
    PApplet.main("chipvm.game.ChipVM")
  }
}