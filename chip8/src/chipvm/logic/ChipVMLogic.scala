package chipvm.logic

import engine.random.{RandomGenerator, ScalaRandomGen}
import chipvm.logic.ChipVMLogic._
import engine.graphics.Color

import scala.collection.immutable.List
import scala.collection.immutable.ArraySeq
import java.awt.event.KeyEvent._
import scala.io.Source

case class ChipVMLogic(memory: Array[Short], // 4 kilobytes (using Bytes) - using Short because of signedness of Byte
                  var display: Display, // 64x32 display
                  var pc: Int, // 12-bit (max 4096)
                  var i: Int, // index register
                  stack: List[Int], // stack of 16-bit addresses
                  delayTimer: Int, // 8-bit timer, decreased at 60 times per second
                  soundTimer: Int, // same, but BEEP as long as not 0
                  variableRegisters: Array[Short] // 16 8-bit registers (0-F / 0-15)
                 ) {
  def timerTick(): ChipVMLogic = {
    val newDelayTimer = if (delayTimer == 0) 0 else delayTimer - 1
    val newSoundTimer = if (soundTimer == 0) 0 else soundTimer - 1

    copy(delayTimer = newDelayTimer,
         soundTimer = newSoundTimer)
  }

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
    val file = Source.fromFile(filePath, "ISO8859-1")

    file.map(_.toShort).copyToArray(memory, 512, 3584)

    file.close()
  }

  /**
   * Fetch / decode / execute loop
   *
   * Do not update the display unless necessary
   *
   * @return Boolean state: should the display be updated?
   */
  def loop(): Boolean = {
    val instruction = (memory(pc), memory(pc + 1))
    val nibbles = ( (memory(pc) & 0xF0) >> 4,
                    (memory(pc) & 0x0F),
                    (memory(pc + 1) & 0xF0) >> 4,
                    (memory(pc + 1) & 0x0F))
    lazy val _NNN = (nibbles._2 << 8) + instruction._2

    pc = (pc + 2).toShort

    nibbles._1 match {
      case 0x0 => nibbles._2 match {
        case 0x0 => nibbles._3 match {
          case 0xE => nibbles._4 match {
            case 0x0 => cls()
            case 0xE => ret()
            case _ => print("unknown instruction")
          }
          case _ => print("unknown instruction")
        }
        case _ => print("unknown instruction")
      }
      case 0x1 =>
        movePC(_NNN)
      case 0x2 =>
        callSubroutine(_NNN)
      case 0x6 =>
        val register = nibbles._2
        val value = instruction._2

        set(register.toShort, value)
      case 0x7 =>
        val register = nibbles._2
        val value = instruction._2

        add(register.toShort, value)
      case 0xA =>
        setIndex(_NNN)
      case 0xD =>
        val x = variableRegisters(nibbles._2) % 64
        val y = variableRegisters(nibbles._3) % 32
        val height = nibbles._4

        draw(x, y, height)
    }


    // You cannot directly compare Byte to HEX, because Java Bytes are also "signed"
    // 0xaf == 175
    // 0xaf.toByte == -81
    // https://stackoverflow.com/a/22278028
    (instruction._1 == 0x0.toByte && instruction._2 == 0xe0.toByte) || nibbles._1 == 0xD.toByte
  }

  def cls(): Unit = {
    display = display.clear()
  }

  def ret(): Unit = {
    // TODO
  }

  def movePC(_NNN: Int) : Unit = {
    pc = _NNN
  }

  def callSubroutine(_NNN: Int): Unit = {
    val newStack = stack.prepended(pc)
    movePC(_NNN)
    copy(stack = newStack)
  }

  def setIndex(_NNN: Int): Unit = {
    i = _NNN
  }

  def add(register: Short, value: Short): Unit = {
    variableRegisters(register) = (variableRegisters(register) + value).toShort
  }

  def set(register: Short, value: Short): Unit = {
    variableRegisters(register) = value
  }

  def draw(x: Int, y: Int, height: Int): Unit = {
    val data = memory.slice(i, i + height)

    val newDisplay = {
      data.zipWithIndex.foldLeft(display)((acc, byte) => {
        val line = byteToBool(byte._1)

        val curY = y + byte._2

        if (curY < Height) {
          line.zipWithIndex.foldLeft(acc)((acc, bit) => {
            val curX = x + bit._2

            if (bit._1 && curX < Width) {
              acc.flip(curX, curY)
            } else acc
          })
        } else acc
      })
    }

    variableRegisters(15) = if (display.collision) 1 else 0
    display = newDisplay.clearCollision()
  }

  def byteToBool(input: Int) = {
    (0 until 8).foldLeft(new Array[Boolean](8))((acc, el) => {
      acc(el) = ((input >> el) & 1) != 0
      acc
    }).toSeq.reverse
  }
  def fixSigned(byte: Int): Int = byte & 255
}

object ChipVMLogic {

  val InstructionsPerSecond: Int = 700
  val TimerFrequency: Int = 60 // Hz
  val BackgroundColor: Color = Color(146, 104, 33)
  val ForegroundColor: Color = Color(247, 206, 70)

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
    ArraySeq[Byte](0xF0.toByte, 0x80.toByte, 0xF0.toByte, 0x80.toByte, 0x80.toByte) // F
  )

  val DrawSizeFactor = 1.0 // increase this to make the game bigger

  val Width: Int = 64
  val Height: Int = 32
  val DefaultDims: Dimensions = Dimensions(width = Width, height = Height)

  def apply() = new ChipVMLogic(new Array[Short](4096), Display(), 512, 0, List[Int](), 0, 0, new Array[Short](16))
}