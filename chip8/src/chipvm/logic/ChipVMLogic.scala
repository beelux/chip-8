package chipvm.logic

import engine.random.{RandomGenerator, ScalaRandomGen}
import chipvm.logic.ChipVMLogic._

import scala.collection.mutable.Stack
import scala.collection.immutable.ArraySeq
import java.awt.event.KeyEvent._
import scala.io.Source

class ChipVMLogic(val memory: Array[Byte], // 4 kilobytes, 4096 bytes of memory
                  val display: Array[Array[CellType]], // 64x32 display
                  var pc: Short, // 12-bit (max 4096)
                  var i: Short, // index register
                  val stack: Stack[Short], // stack of 16-bit addresses
                  var delayTimer: Byte, // 8-bit timer, decreased at 60 times per second
                  var soundTimer: Byte, // same, but BEEP as long as not 0
                  val variableRegisters: Array[Byte] // 16 8-bit registers (0-F / 0-15)
                 ) {
  def moveDown(): Unit = ()

  def keyPressed(keyCode: Int): Unit = {
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

  def readROM(filePath: String): Unit = {
    val file = Source.fromFile(filePath)

    // Copy contents of file to memory
    //file.copyToArray(memory, 0, 4096)

    file.close()
  }

  /**
   * Fetch / decode / execute loop
   *
   * Do not update the display unless necessary
   * @return Boolean state: should the display be updated?
   */
  def loop: Boolean = {
    ???
  }

  def getCellType(p: Point): CellType = display(p.x)(p.y)
}

object ChipVMLogic {

  val InstructionsPerSecond: Int = 700
  val timerFrequency: Int = 60

  // Source: https://tobiasvl.github.io/blog/write-a-chip-8-emulator/
  val font: ArraySeq[ArraySeq[Byte]] = ArraySeq(
    ArraySeq[Byte](0xF0.toByte, 0x90.toByte, 0x90.toByte, 0x90.toByte, 0xF0.toByte), // 0
    ArraySeq[Byte](0x20.toByte, 0x60.toByte, 0x20.toByte, 0x20.toByte, 0x70.toByte), // 1
    ArraySeq[Byte](0xF0.toByte, 0x10.toByte, 0xF0.toByte, 0x80.toByte, 0xF0.toByte), // 2
    ArraySeq[Byte](0xF0.toByte, 0x10.toByte, 0xF0.toByte, 0x10.toByte, 0xF0.toByte), // 3
    ArraySeq[Byte](0x90.toByte, 0x90.toByte, 0xF0.toByte, 0x10.toByte, 0x10.toByte), // 4
    ArraySeq[Byte](0xF0.toByte, 0x80.toByte, 0xF0.toByte, 0x10.toByte, 0xF0.toByte), // 5
    ArraySeq[Byte](0xF0.toByte, 0x80.toByte, 0xF0.toByte, 0x90.toByte, 0xF0.toByte), // 6
    ArraySeq[Byte](0xF0.toByte, 0x10.toByte, 0x20.toByte, 0x40.toByte, 0x40.toByte), // 7
    ArraySeq[Byte](0xF0.toByte, 0x90.toByte, 0xF0.toByte, 0x90.toByte, 0xF0.toByte), // 8
    ArraySeq[Byte](0xF0.toByte, 0x90.toByte, 0xF0.toByte, 0x10.toByte, 0xF0.toByte), // 9
    ArraySeq[Byte](0xF0.toByte, 0x90.toByte, 0xF0.toByte, 0x90.toByte, 0x90.toByte), // A
    ArraySeq[Byte](0xE0.toByte, 0x90.toByte, 0xE0.toByte, 0x90.toByte, 0xE0.toByte), // B
    ArraySeq[Byte](0xF0.toByte, 0x80.toByte, 0x80.toByte, 0x80.toByte, 0xF0.toByte), // C
    ArraySeq[Byte](0xE0.toByte, 0x90.toByte, 0x90.toByte, 0x90.toByte, 0xE0.toByte), // D
    ArraySeq[Byte](0xF0.toByte, 0x80.toByte, 0xF0.toByte, 0x80.toByte, 0xF0.toByte), // E
    ArraySeq[Byte](0xF0.toByte, 0x80.toByte, 0xF0.toByte, 0x80.toByte, 0x80.toByte)  // F
  )

  val DrawSizeFactor = 1.0 // increase this to make the game bigger

  /** Makes empty display
   * It is a 2D array for performance reasons (O(1) access time)
   * -> performance is important, as we do 700 instructions per second
   *
   * @return
   */
  def makeEmptyBoard: Array[Array[CellType]] = {
    val emptyColumn: Array[CellType] = Array.fill(Height)(Empty)
    Array.fill(Width)(emptyColumn.clone())
  }

  val Width: Int = 64
  val Height: Int = 32
  val DefaultDims: Dimensions = Dimensions(width = Width, height = Height)

  def apply() = new ChipVMLogic(new Array[Byte](4096), makeEmptyBoard, 0, 0, Stack[Short](), 0, 0, Array[Byte](16))
}