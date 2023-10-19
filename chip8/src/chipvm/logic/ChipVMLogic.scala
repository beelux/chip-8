package chipvm.logic

import engine.random.{RandomGenerator, ScalaRandomGen}
import chipvm.logic.ChipVMLogic._

import java.awt.event.KeyEvent._

class ChipVMLogic(val randomGen: RandomGenerator,
                  val board: Array[Array[CellType]]) {

  def this(random: RandomGenerator) = this(random, makeEmptyBoard)
  def this() = this(new ScalaRandomGen(), makeEmptyBoard)

  def moveDown(): Unit = ()

  def keyPressed(keyCode : Int): Unit = {
    keyCode match {
      case VK_1 => println("1")
      case VK_2 => println("2")
      case VK_3 => println("3")
      case VK_4 => println("4")
      case VK_Q => println("Q")
      case VK_W => println("W")
      case VK_E => println("E")
      case VK_R => println("R")
      case VK_A => println("A")
      case VK_S => println("S")
      case VK_D => println("D")
      case VK_F => println("F")
      case VK_Z => println("Z")
      case VK_X => println("X")
      case VK_C => println("C")
      case VK_V => println("V")
      case _ => ()
    }
  }

  def getCellType(p : Point): CellType = board(p.x)(p.y)
}

object ChipVMLogic {

  val InstructionsPerSecond: Int = 700

  val DrawSizeFactor = 1.0 // increase this to make the game bigger

  /** Makes empty board
   * It is a 2D array for performance reasons (O(1) access time)
   * -> performance is important, as we do 700 instructions per second
   * @return
   */
  def makeEmptyBoard: Array[Array[CellType]] = {
    val emptyColumn : Array[CellType] = Array.fill(Height)(Empty)
    Array.fill(Width)(emptyColumn.clone())
  }

  val Width: Int = 64
  val Height: Int = 32
  val DefaultDims : Dimensions = Dimensions(width = Width, height = Height)

  def apply() = new ChipVMLogic(new ScalaRandomGen(), makeEmptyBoard)
}