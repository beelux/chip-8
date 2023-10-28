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
import chipvm.logic.instructions.Instruction._
import chipvm.logic.{Point => GridPoint}
import ddf.minim.ugens.{Oscil, Waves}
import ddf.minim.{AudioOutput, Minim}

class ChipVM extends GameBase {
  var out: AudioOutput = minim.getLineOut()
  var gameLogic : ChipVMLogic = ChipVMLogic(out = out)
  val updateTimer = new UpdateTimer(ChipVMLogic.TimerFrequency.toFloat)
  val gridDims : Dimensions = ChipVMLogic.DefaultDims
  val widthInPixels: Int = (WidthCellInPixels * gridDims.width).ceil.toInt
  val heightInPixels: Int = (HeightCellInPixels * gridDims.height).ceil.toInt
  val screenArea: Rectangle = Rectangle(Point(0, 0), widthInPixels.toFloat, heightInPixels.toFloat)
  val widthPerCell: Float = screenArea.width / gridDims.width
  val heightPerCell: Float = screenArea.height / gridDims.height


  override def draw(): Unit = {
      if (updateState())
        drawGrid()
  }

  def drawGrid(): Unit = {
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
    gameLogic.keyPressed(event.getKeyCode)
  }

  override def keyReleased(event: KeyEvent): Unit = {
    gameLogic.keyReleased(event.getKeyCode)
  }

  override def settings(): Unit = {
    pixelDensity(displayDensity())
    size(widthInPixels, heightInPixels)
  }

  override def setup(): Unit = {
    noStroke() // Disable stroke around rectangle
    gameLogic = gameLogic.readROM("roms/1-chip8-logo.ch8")
    //gameLogic = gameLogic.readROM("roms/2-ibm-logo-1.ch8")
    //gameLogic = gameLogic.readROM("roms/3-corax+.ch8")
    //gameLogic = gameLogic.readROM("roms/4-flags.ch8")
    gameLogic = gameLogic.readROM("roms/5-quirks.ch8")
    //gameLogic = gameLogic.readROM("roms/6-keypad.ch8")
    //gameLogic = gameLogic.readROM("roms/sir.ch8")
    gameLogic = gameLogic.readROM("roms/morse_demo.ch8")
    gameLogic = gameLogic.readROM("roms/war.ch8")
    //gameLogic = gameLogic.readROM("roms/audio.ch8")
    this.frameRate(ChipVMLogic.InstructionsPerSecond.toFloat)

    updateTimer.init()
  }

  def updateState(): Boolean = {
    gameLogic = checkTimers()

    val instruction = gameLogic.fetchDecode()
    gameLogic = gameLogic.execute(instruction)

    modifiesDisplay(instruction)
  }

  def checkTimers(): ChipVMLogic = {
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