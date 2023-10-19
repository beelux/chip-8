package infrastructure

import org.scalactic.source.Position
import org.scalatest.concurrent.{Signaler, TimeLimitedTests}
import org.scalatest.exceptions.TestFailedException
import org.scalatest.time.{Days, Seconds, Span}
import org.scalatest.{Args, BeforeAndAfterAll, FunSuite, Status, Tag}

abstract class TestBase extends FunSuite with TimeLimitedTests {

  // check if the program was launced from the debugger, so that we can disable the timeout in that case
  val isDebug : Boolean = java.lang.management.ManagementFactory.getRuntimeMXBean.getInputArguments.toString.indexOf("jdwp") >= 0
  override def timeLimit: Span = if (isDebug) Span(365,Days) else Span(1,Seconds)

  // this is need to actually stop when the buggy code contains an infinite loop...
  override val defaultTestSignaler: Signaler = ReallyStopSignaler

  var scoreCounter : Option[ScoreCounter] = None

  override def run(testName: Option[String], args: Args): Status = {
    if(args.configMap.contains("scoreCounter")) {
      args.configMap("scoreCounter") match {
        case sc: ScoreCounter => this.scoreCounter = Some(sc)
      }
    }
    super.run(testName, args)
  }

  override def test(testName: String, testTags: Tag*)(testFun: => Any)(implicit pos: Position): Unit =
    test(testName,1,testTags:_*){testFun}


  def test(testName : String, weight : Int, testTags : Tag*)(testFun : => Any)(implicit pos: Position): Unit = {
    super.test(testName,testTags:_*){
      try {
        testFun
        scoreCounter.foreach(_.addScore(weight,weight))
      } catch {
        case e : TestFailedException => {
          scoreCounter.foreach(_.addScore(weight,0))
          throw e
        }
      }
    }
  }
}

object ReallyStopSignaler extends Signaler {
  override def apply(testThread: Thread): Unit = {
    StopRunningNow.stopRunningNowUnsafe(testThread)
  }
}

