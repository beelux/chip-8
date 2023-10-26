package chipvm.logic

import engine.random.{RandomGenerator, ScalaRandomGen}
import chipvm.logic.ChipVMLogic._
import engine.graphics.Color
import chipvm.logic.instructions._

import scala.collection.immutable.List
import scala.collection.immutable.ArraySeq
import java.awt.event.KeyEvent._
import scala.io.Source

case class ChipVMLogic(memory: Array[Short], // 4 kilobytes (using Bytes) - using Short because of signedness of Byte
                  display: Display, // 64x32 display
                  pc: Int, // 12-bit (max 4096)
                  i: Int, // index register
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

  def readROM(filePath: String): ChipVMLogic = {
    val cleanMemory = new Array[Short](4096)

    val file = Source.fromFile(filePath, "ISO8859-1")
    file.map(_.toShort).copyToArray(cleanMemory, 512, 3584)
    file.close()

    copy(memory = cleanMemory)
  }

  def fetchDecode(): Instruction = {
    val instruction = (memory(pc), memory(pc + 1))
    val nibbles = ((memory(pc) & 0xF0) >> 4,
                   (memory(pc) & 0x0F),
                   (memory(pc + 1) & 0xF0) >> 4,
                   (memory(pc + 1) & 0x0F))
    // Generic Data
    lazy val _X__ = nibbles._2.toShort
    lazy val __Y_ = nibbles._3.toShort
    lazy val _NNN = (nibbles._2 << 8) + instruction._2
    lazy val __NN = instruction._2
    // Drawing
    lazy val x = variableRegisters(nibbles._2) % 64
    lazy val y = variableRegisters(nibbles._3) % 32
    lazy val height = nibbles._4

    nibbles match {
      // Drawing
      case (0x0, 0x0, 0xE, 0x0) => ClearScreen()
      case (0xD, _, _, _)       => Draw(x, y, height)
      // Jump + Subroutines
      case (0x0, 0x0, 0xE, 0xE) => Return()
      case (0x1, _, _, _)       => Jump(_NNN)
      case (0x2, _, _, _)       => CallSubroutine(_NNN)
      // Registers - Memory
      case (0x6, _, _, _)       => Set(_X__, __NN)
      case (0x7, _, _, _)       => Add(_X__, __NN)
      case (0x8, _, _, 0x0)     => Copy(_X__, __Y_)
      case (0xA, _, _, _)       => SetIndex(_NNN)
      case (0xF, _, 0x5, 0x5)   => StoreMemory(_X__, i)
      case (0xF, _, 0x6, 0x5)   => LoadMemory(_X__, i)
      case (0xF, _, 0x1, 0xE)   => AddToIndex(_X__)
      case (0xF, _, 0x3, 0x3)   => StoreBCD(_X__)
      // Skip
      case (0x3, _, _, _)       => SkipValEqual(_X__, __NN)
      case (0x4, _, _, _)       => SkipValNotEqual(_X__, __NN)
      case (0x5, _, _, 0x0)     => SkipRegisterEqual(_X__, __Y_)
      case (0x9, _, _, 0x0)     => SkipRegisterNotEqual(_X__, __Y_)
      // Operations
      case (0x8, _, _, 0x1)     => Or(_X__, __Y_)
      case (0x8, _, _, 0x2)     => And(_X__, __Y_)
      case (0x8, _, _, 0x3)     => Xor(_X__, __Y_)
      case (0x8, _, _, 0x4)     => AddRegister(_X__, __Y_)
      case (0x8, _, _, 0x5)     => SubtractRegister(_X__, __Y_)
      case (0x8, _, _, 0x6)     => ShiftRight(_X__, __Y_)
      case (0x8, _, _, 0x7)     => SubtractRegisterReverse(_X__, __Y_)
      case (0x8, _, _, 0xE)     => ShiftLeft(_X__, __Y_)
      case _                    => Nop()
    }
  }

  def execute(instruction: Instruction): ChipVMLogic = {
    val modifiedPC = copy(pc = pc + 2)
    instruction.execute(modifiedPC)
  }
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

  val VFIndex: Int = 15

  def byteToBool(input: Int): Seq[Boolean] = {
    (0 until 8).foldLeft(new Array[Boolean](8))((acc, el) => {
      acc(el) = ((input >> el) & 1) != 0
      acc
    }).toSeq.reverse
  }

  def fixSigned(byte: Int): Int = byte & 255

  def apply() = new ChipVMLogic(new Array[Short](4096), Display(), 512, 0, List[Int](), 0, 0, new Array[Short](16))
}