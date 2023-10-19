// DO NOT MODIFY FOR BASIC SUBMISSION
// scalastyle:off

package tetris

import engine.random.RandomGenerator
import generic.{CellTypeInterface, GameLogicInterface, GameTestSuite}
import tetris.logic.{CellType, Dimensions, Empty, ICell, JCell, LCell, OCell, Point, SCell, TCell, TetrisLogic, ZCell}

sealed abstract class TetrisAction
case object RotateLeft    extends TetrisAction
case object RotateRight   extends TetrisAction
case object Down          extends TetrisAction
case object Left          extends TetrisAction
case object Right         extends TetrisAction
case object Drop          extends TetrisAction

case class TetrisGridTypeWrapper(gridType : CellType)  extends CellTypeInterface[TetrisGridTypeWrapper]{
  override def conforms(rhs : TetrisGridTypeWrapper) : Boolean = gridType == rhs.gridType

  override def toChar: Char = gridType match {
    case ICell  => 'I'
    case JCell  => 'J'
    case LCell  => 'L'
    case OCell  => 'O'
    case SCell  => 'S'
    case TCell  => 'T'
    case ZCell  => 'Z'
    case Empty   => '.'
  }
}


case class TetrisLogicWrapper
(randomGentlw : RandomGenerator,
 gridDimstlw : Dimensions,
 initialBoardtlw : Seq[Seq[CellType]])
  extends TetrisLogic(randomGentlw,gridDimstlw,initialBoardtlw) with GameLogicInterface[TetrisAction, TetrisGridTypeWrapper]{
  override def performAction(action: TetrisAction): Unit = action match {
    case RotateLeft => rotateLeft()
    case RotateRight => rotateRight()
    case Down => moveDown()
    case Left => moveLeft()
    case Right => moveRight()
    case Drop => doHardDrop()
  }

  override def getCell(col: Int , row: Int): TetrisGridTypeWrapper = TetrisGridTypeWrapper(getCellType(Point(col,row)))

  override def nrRows: Int = gridDimstlw.height
  override def nrColumns: Int = gridDimstlw.width
}


class TetrisTestSuiteBase  extends GameTestSuite
  [TetrisAction, TetrisGridTypeWrapper, TetrisLogicWrapper, (Dimensions, Seq[Seq[CellType]])]() {
  def charToGridType(char: Char) : TetrisGridTypeWrapper = TetrisGridTypeWrapper(char match {
    case 'I' => ICell
    case 'J' => JCell
    case 'L' => LCell
    case 'O' => OCell
    case 'S' => SCell
    case 'T' => TCell
    case 'Z' => ZCell
    case '.' => Empty
  })


  def emptyBoard( dims : Dimensions) : Seq[Seq[CellType]] = {
    val emptyLine = Seq.fill(dims.width)(Empty)
    Seq.fill(dims.height)(emptyLine)
  }

  override def makeGame(random: RandomGenerator, initialInfo: (Dimensions, Seq[Seq[CellType]])): TetrisLogicWrapper =
    new TetrisLogicWrapper(random,initialInfo._1, initialInfo._2)

  override def gameLogicName: String = "TetrisLogic"


  private def toTestRecording(initialBoardString : Option[String], frames : Seq[TestFrame]) : TestRecording = {
    val dimensions: Dimensions = frames.head.display match {
      case grid: GridDisplay => Dimensions(width = grid.nrColumns, height = grid.nrRows)
      case _ => throw new Error("No grid display in test")
    }
    def parseInitialField(s : String) : Seq[Seq[CellType]] =
      stringToGridDisplay(s).grid.map(_.map(_.gridType))
    val board = initialBoardString match {
      case Some(s) => parseInitialField(s)
      case _ => emptyBoard(dimensions)
    }
    TestRecording((dimensions, board),frames)
  }

  private def toTestRecording(initialBoard : String, frames : Seq[TestFrame]) : TestRecording = {
    toTestRecording(Some(initialBoard),frames)
  }

  private def toTestRecording(frames : Seq[TestFrame]) : TestRecording = {
    toTestRecording(None,frames)
  }

  def checkGame(frames : Seq[TestFrame], hint : String = ""  ) : Unit =
    checkGame(toTestRecording(frames), hint )

  def checkGame(initialBoard : String, frames : Seq[TestFrame], hint : String  ) : Unit =
    checkGame(toTestRecording(initialBoard,frames), hint )

  def checkInterleave(framesA : Seq[TestFrame] , framesB : Seq[TestFrame]) : Unit = {
    checkInterleave(toTestRecording(framesA), toTestRecording(framesB))
  }


}
