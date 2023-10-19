// DO NOT MODIFY FOR BASIC SUBMISSION
// scalastyle:off

package generic


import engine.random.RandomGenerator
import generic.GameTestSuite._

import scala.language.postfixOps
import generic.StringUtils._
import infrastructure.TestBase
import org.scalatest.exceptions.TestFailedException

/**   * Generic test infrastructure for Snake and Tetris.
 *
 * This supports a game with a grid, filled with some CellType
 * on which some actions can be done.
 *
 * An initial game can be constructed out of some InitialInfo
 * For testing we perform the actions and check is the grid
 * is the same as specified in the test.
 */
trait CellTypeInterface[T] {
  def conforms(rhs: T): Boolean

  def toChar: Char
}

trait GameLogicInterface[GameAction, CellType] {
  def nrRows: Int

  def nrColumns: Int

  def performAction(action: GameAction): Unit

  def getCell(col: Int, row: Int): CellType

  def isGameOver: Boolean
}

abstract class GameTestSuite[
  GameAction,
  CellType <: CellTypeInterface[CellType],
  GameLogic <: GameLogicInterface[GameAction, CellType],
  InitialInfo]()  extends TestBase {

  def charToGridType(ch: Char): CellType

  def makeGame(r: RandomGenerator, info: InitialInfo): GameLogic

  def gameLogicName: String

  sealed abstract class GameDisplay {
    def conforms(other: GameDisplay): Boolean
    def isError : Boolean
  }

  case class GridDisplay(grid: Seq[Seq[CellType]])
    extends GameDisplay {

    val nrRows: Int = grid.length
    val nrColumns: Int = grid.head.length

    override def toString: String =
      grid.map(_.map(_.toChar).mkString).mkString("\n")

    def gridConforms(otherGrid: Seq[Seq[CellType]]): Boolean = {

      val zippedCells: Seq[(CellType, CellType)] =
        for ((rowL, rowR) <- grid zip otherGrid; p <- rowL zip rowR) yield p

      val sameContent: Boolean =
        zippedCells.forall(pair => pair._1.conforms(pair._2))

      sameContent
    }

    def sameDimensions(rhs: GridDisplay): Boolean =
      nrRows == rhs.nrRows && nrColumns == rhs.nrColumns

    override def conforms(other: GameDisplay): Boolean =
      other match {
        case g: GridDisplay => sameDimensions(g) && gridConforms(g.grid)
        case _ => false
      }

    override def isError: Boolean = false
  }

  case class GameOverDisplay() extends GameDisplay {
    override def conforms(other: GameDisplay): Boolean = other match {
      case GameOverDisplay() => true
      case _ => false
    }
    override def isError: Boolean = false
  }

  case class LogicFailed(cause: Throwable) extends GameDisplay {
    override def conforms(other: GameDisplay): Boolean = other match {
      case LogicFailed(_) => true
      case _ => false
    }

    override def isError: Boolean = true

    override def toString: String =
      "LogicFailed(" + stackTraceAsString(cause) + ")"
  }

  case class FrameInput(randomNumber: Int, actions: Seq[GameAction])

  case class TestFrame(input: FrameInput, display: GameDisplay) {

    override def toString: String = {
      val actionsString =
        if (input.actions.isEmpty) ""
        else ", " + input.actions.toString
      "TestFrame(" + input.randomNumber + actionsString + ",\n" +
        asIndentendMultilineString(display.toString, 6)
    }

  }

  def stringToGridDisplay(s: String): GridDisplay = {
    GridDisplay(
      for (row <- s.stripMargin.linesIterator.toList)
        yield for (char <- row)
          yield charToGridType(char)
    )
  }

  def getDisplay(logic: GameLogic): GameDisplay = {
    def getGridDisplay(logic: GameLogic): GridDisplay = {
      GridDisplay(
        for (row <- 0 until logic.nrRows)
          yield for (col <- 0 until logic.nrColumns)
            yield logic.getCell(col, row)
      )
    }

    if (logic.isGameOver) GameOverDisplay()
    else getGridDisplay(logic)
  }

  def performActionsAndGetDisplay(random: TestRandomGen,
                                  logic: GameLogic,
                                  frameInput: FrameInput): GameDisplay = {
    random.curNumber = frameInput.randomNumber
    frameInput.actions.foreach(logic.performAction)
    getDisplay(logic)
  }

  def canInterleave(testA: TestRecording, testB: TestRecording): Boolean = {
    val randomA = new TestRandomGen(testA.frames.head.input.randomNumber)
    val randomB = new TestRandomGen(testB.frames.head.input.randomNumber)

    val logicA = makeGame(randomA, testA.initialInfo)
    val logicB = makeGame(randomB, testB.initialInfo)
    val dispA = getDisplay(logicA)
    val dispB = getDisplay(logicB)
    if(!dispA.conforms(testA.frames.head.display) ||
      !dispB.conforms(testB.frames.head.display))
      return false

    for ((a, b) <- testA.frames.tail.zip(testB.frames.tail)) {
      val (da, db) = (performActionsAndGetDisplay(randomA, logicA, a.input),
        performActionsAndGetDisplay(randomB, logicB, b.input))
      val (successA, successB) = (da.conforms(a.display), db.conforms(b.display))
      if (!successA || !successB) return false
    }
    return true
  }

  case class TestRecording(initialInfo: InitialInfo, frames: Seq[TestFrame]) {
    lazy val referenceDisplays: Seq[GameDisplay] = frames.map(_.display)

    lazy val implementationDisplays: Seq[GameDisplay] = {
      val random = new TestRandomGen(frames.head.input.randomNumber)
      def catchLogicError(compute : => GameDisplay) : GameDisplay = {
        try {
          return compute
        } catch {
          case e : Throwable => LogicFailed(e)
        }
      }
      try {
        val logic = makeGame(random, initialInfo)
        val displays = Seq.newBuilder[GameDisplay]
        val firstDisplay = catchLogicError{ getDisplay(logic) }
        displays.addOne(firstDisplay)
        var error = firstDisplay.isError
        val inputIterator = frames.tail.iterator
        while(!error && inputIterator.hasNext ) {
          val testFrame = inputIterator.next()

          val newDisplay = catchLogicError {performActionsAndGetDisplay(random, logic, testFrame.input) }
          displays.addOne(newDisplay)
          error = newDisplay.isError
        }
        displays.result()
      } catch {
        case e: Throwable => Seq(LogicFailed(e))
      }
    }

    lazy val passes: Boolean =
      checkSameRecording(referenceDisplays, implementationDisplays)
  }

  object TestFrame {

    def apply(rand: Int,
              actions: Seq[GameAction],
              grid: String): TestFrame = {
      TestFrame(FrameInput(rand, actions), stringToGridDisplay(grid))
    }

    def apply(rand: Int, grid: String): TestFrame = {
      apply(rand, List(), grid)
    }

    def apply(rand: Int,
              actions: Seq[GameAction],
              display: GameDisplay): TestFrame = {
      TestFrame(FrameInput(rand, actions), display)
    }

    def apply(rand: Int, display: GameDisplay): TestFrame = {
      TestFrame(FrameInput(rand, List()), display)
    }

  }

  def checkSameRecording(testRecord: Seq[GameDisplay],
                         actualRecord: Seq[GameDisplay]): Boolean = {
    testRecord.zip(actualRecord).forall(p => p._1.conforms(p._2))
  }

  val InterleaveFailMsg =
    s"""
       |Hint: It should be possible to “run” multiple games simultaneously, i.e., it should not
       |be a problem to have multiple instances of $gameLogicName which act independently. To test
       |this, we perform “interleave tests”: we instantiate two $gameLogicName objects with different
       |board sizes and alternate between performing a step on one and a step on the other. If
       |all is correct, the two games should progress exactly as they would if they were the only
       |snake games being run. If this is not true, then there is likely some global state through
       |which one game influences the other.
       |
       |
       |Hence if you fail this test but you passed the other full game test, then you have some global state.
       |Running two instances of $gameLogicName/and alternately doing steps between them results in some interference.
  """.stripMargin.replaceAll("\n", " ")

  def wordWrap(s : String, lineLen : Int) : String = {
    val res = new StringBuilder()
    var line : List[String] = List()
    var len = 0
    for(word <- s.split("[ ]")) {
      if(len + word.length > lineLen) {
        res++=line.reverse.mkString(" ") + "\n"
        line = List(word)
        len = word.length
      } else {
        len+= word.length
        line = word :: line
      }
    }
    if(line.nonEmpty) res++=line.reverse.mkString(" ")
    res.toString()
  }


  def checkGame(theTest : TestRecording, hint : String): Unit = {
    def actionsString(actions: Seq[GameAction]): String =
      "<" ++ actions.map(_.toString).mkString(", ") ++ ">"
    val sbuild = new StringBuilder()
    if(!hint.isEmpty) sbuild.append("\n" + wordWrap("Hint: "+hint,80) )
    sbuild.append("\n" + horizontalLineOfWidth(80) + "\n")
    def printTraceFrame(frame: TestFrame, actual: GameDisplay, index: Int): Unit = {
      sbuild.append(s"step=$index, rand=${frame.input.randomNumber}, actions=${actionsString(frame.input.actions)}\n")

      val frameIsCorrect = frame.display.conforms(actual)
      val headerB = if(frameIsCorrect) "Got ✓" else "Got ✗"
      val frameString = twoColumnTable("Want", headerB, frame.display.toString, actual.toString)

      sbuild.append(frameString + "\n")
      sbuild.append("\n")
    }
    val didPass = theTest.passes

    if (!didPass) sbuild.append("This is what went wrong:\n\n")
    else sbuild.append("This is what we got & expected:\n\n")

    theTest.frames
      .lazyZip(theTest.implementationDisplays)
      .lazyZip(theTest.frames.indices).foreach(printTraceFrame)
    assert(didPass,sbuild.toString())
  }
  def checkInterleave( testA : TestRecording, testB : TestRecording): Unit = {
    val didPass = canInterleave(testA, testB)

    assert(didPass, "\n" + InterleaveFailMsg)
  }



}

object GameTestSuite {
  val PassStr = "[√] Pass"
  val FailStr = "[X] Fail"

  val DisplayPadWidth = 3
  val DisplayPadString: String = " " * DisplayPadWidth

}

