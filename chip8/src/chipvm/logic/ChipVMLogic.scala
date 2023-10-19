package chipvm.logic

import engine.random.{RandomGenerator, ScalaRandomGen}
import chipvm.logic.ChipVMLogic._

class ChipVMLogic(val randomGen: RandomGenerator,
                  val board: Array[Array[CellType]]) {

  def this(random: RandomGenerator) = this(random, makeEmptyBoard)
  def this() = this(new ScalaRandomGen(), makeEmptyBoard)

  def moveDown(): Unit = ()

  def getCellType(p : Point): CellType = board(p.x)(p.y)
}

object ChipVMLogic {

  val FramesPerSecond: Int = 60

  val DrawSizeFactor = 1.0 // increase this to make the game bigger

  def makeEmptyBoard: Array[Array[CellType]] = {
    val emptyColumn : Array[CellType] = Array.fill(Height)(Empty)
    Array.fill(Width)(emptyColumn.clone())
  }

  val Width: Int = 64
  val Height: Int = 32
  val DefaultDims : Dimensions = Dimensions(width = Width, height = Height)

  def apply() = new ChipVMLogic(new ScalaRandomGen(), makeEmptyBoard)
}