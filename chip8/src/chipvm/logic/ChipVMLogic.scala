package chipvm.logic

import engine.random.{RandomGenerator, ScalaRandomGen}
import chipvm.logic.ChipVMLogic._
import engine.graphics.Color
import chipvm.logic.instructions._

import scala.collection.immutable.ArraySeq
import java.awt.event.KeyEvent._
import java.util.Properties
import scala.io.Source

case class ChipVMLogic(memory: Vector[UByte], // 4 kilobytes (using Bytes) - using Short because of signedness of Byte
                  display: Display, // 64x32 display
                  pc: Int, // 12-bit (max 4096)
                  i: Int, // index register
                  stack: List[UShort], // stack of 16-bit addresses
                  delayTimer: UByte, // 8-bit timer, decreased at 60 times per second
                  soundTimer: UByte, // same, but BEEP as long as not 0
                  variableRegisters: Vector[UByte], // 16 8-bit registers (0-F / 0-15)
                  pressedKeys: Vector[Boolean], // these keys are currently pressed
                  waitForKeyIndex: Option[Int], // 0xFX0A - store the key we're waiting to be released
                  isDrawable: Boolean, // Drawing is only allowed 60 times per second
                  quirks: Map[String,Boolean]) {
  def timerTick(): ChipVMLogic = {
    val newDelayTimer = if (delayTimer == 0) UByte(0) else delayTimer - UByte(1)
    val newSoundTimer = if (soundTimer == 0) UByte(0) else soundTimer - UByte(1)

    copy(delayTimer = newDelayTimer,
         soundTimer = newSoundTimer,
         isDrawable = true)
  }

  def getIndexFromKeyCode(keyCode: Int): Option[Int] = {
    val value = keyCode match {
      case VK_1 => 0x1
      case VK_2 => 0x2
      case VK_3 => 0x3
      case VK_4 => 0xC
      case VK_Q => 0x4
      case VK_W => 0x5
      case VK_E => 0x6
      case VK_R => 0xD
      case VK_A => 0x7
      case VK_S => 0x8
      case VK_D => 0x9
      case VK_F => 0xE
      case VK_Z => 0xA
      case VK_X => 0x0
      case VK_C => 0xB
      case VK_V => 0xF
      case _ => 0xBAD
    }

    if (value == 0xBAD) None
    else Some(value)
  }

  def keyPressed(keyCode: Int): ChipVMLogic = {
    val index = getIndexFromKeyCode(keyCode)

    if(index.isDefined) {
      val updatedKeys = pressedKeys.updated(index.get, true)
      copy(pressedKeys = updatedKeys)
    } else this
  }

  def keyReleased(keyCode: Int): ChipVMLogic = {
    val index = getIndexFromKeyCode(keyCode)

    if (index.isDefined) {
      val updatedKeys = pressedKeys.updated(index.get, false)
      copy(pressedKeys = updatedKeys)
    } else this
  }

  def readROM(filePath: String): ChipVMLogic = {
    val cleanMemory = new Array[UByte](4096).map(_ => UByte(0))

    val file = Source.fromFile(filePath, "ISO8859-1")
    file.map(UByte(_)).copyToArray(cleanMemory, 512, 3584)
    file.close()

    font.flatten.map(UByte(_)).copyToArray(cleanMemory, 0, 512)

    val cleanedRegisters = variableRegisters.map(_ => UByte(0))

    copy(memory = cleanMemory.toVector,
      pc = 512,
      i = 0,
      variableRegisters = cleanedRegisters,
      waitForKeyIndex = None,
      isDrawable = true,
      display = Display(),
      stack = List[UShort](),
      soundTimer = UByte(0),
      delayTimer = UByte(0),
      quirks = parseProperties(readPropertiesFromDisk))
  }

  def parseProperties(properties: Properties): Map[String, Boolean] = {
    val javaKeys = properties.keySet()
    val keys = javaKeys.toArray.map(_.toString)
    val emptyMap = Map[String, Boolean]()

    keys.foldLeft(emptyMap)((acc, key) => {
      val value = properties.getProperty(key).toBoolean
      acc + (key -> value)
    })
  }

  def readPropertiesFromDisk: Properties = {
    try {
      val quirks = new Properties
      val quirksFile = new java.io.FileInputStream("quirks.properties")
      quirks.load(quirksFile)
      quirksFile.close()
      quirks
    } catch {
      case unableToLoadFile: Exception => getDefaultProperties
    }
  }

  def getDefaultProperties: Properties = {
    val quirks = new Properties()
    quirks.setProperty("memoryQuirk", "true")

    val quirksFile = new java.io.FileOutputStream("quirks.properties")
    quirks.store(quirksFile, "Quirks for ChipVM")
    quirksFile.close()

    quirks
  }

  def fetchDecode(): Instruction = {
    val instruction = (memory(pc), memory(pc + 1))
    val nibbles = (((memory(pc) & UByte(0xF0)) >> UByte(4)),
                   (memory(pc) & UByte(0x0F)),
                   ((memory(pc + 1) & UByte(0xF0)) >> UByte(4)),
                   (memory(pc + 1) & UByte(0x0F)))
    // Generic Data
    lazy val _X__ = nibbles._2
    lazy val __Y_ = nibbles._3
    lazy val _NNN = (UShort(nibbles._2) << UShort(8)) + UShort(instruction._2)
    lazy val __NN = instruction._2
    // Drawing
    lazy val x = variableRegisters(nibbles._2.toInt) % UByte(Width)
    lazy val y = variableRegisters(nibbles._3.toInt) % UByte(Height)
    lazy val height = nibbles._4

    val intNibbles = (nibbles._1.toInt, nibbles._2.toInt, nibbles._3.toInt, nibbles._4.toInt)

    // print current instruction in HEX
    // this is in DEC: println(s"Instruction: $intNibbles")
    val hexString = f"${instruction._1.toInt}%02X${instruction._2.toInt}%02X"
    println(s"Instruction: $hexString")


    intNibbles match {
      // Drawing
      case (0x0, 0x0, 0xE, 0x0) => ClearScreen()
      case (0xD, _, _, _)       => Draw(x, y, height)
      // Jump + Subroutines
      case (0x0, 0x0, 0xE, 0xE) => Return()
      case (0x1, _, _, _)       => Jump(_NNN)
      case (0xB, _, _, _)       => JumpOffset(_NNN)
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
      case (0xF, _, 0x2, 0x9)   => LoadFont(_X__)
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
      case (0xC, _, _, _)       => Random(_X__, __NN)
      // Keys
      case (0xE, _, 0x9, 0xE)   => SkipKeyPressed(_X__)
      case (0xE, _, 0xA, 0x1)   => SkipKeyNotPressed(_X__)
      case (0xF, _, 0x0, 0xA)   => WaitForKey(_X__)
      // Timers
      case (0xF, _, 0x0, 0x7)   => CopyDelayTimerToRegister(_X__)
      case (0xF, _, 0x1, 0x5)   => SetSoundTimer(_X__)
      case (0xF, _, 0x1, 0x8)   => SetDelayTimer(_X__)
      //
      case _                    => println(s"Unknown instruction: $intNibbles")
                                   Nop()
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
    ArraySeq[Byte](0xF0.toByte, 0x80.toByte, 0xF0.toByte, 0x80.toByte, 0x80.toByte)  // F
  )
  val FontWidth: Int = 5

  val DrawSizeFactor = 1.0 // increase this to make the game bigger

  val Width: Int = 64
  val Height: Int = 32
  val DefaultDims: Dimensions = Dimensions(width = Width, height = Height)

  val VFIndex: Int = 15

  def apply() = new ChipVMLogic(new Array[UByte](4096).toVector, Display(), 512, 0, List[UShort](), UByte(0), UByte(0), new Array[UByte](16).toVector, new Array[Boolean](16).toVector, None, true, Map[String, Boolean]())
}