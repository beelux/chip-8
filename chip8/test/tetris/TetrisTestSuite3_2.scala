package tetris

import infrastructure.ScoreCounter
import org.junit.runner.RunWith
import org.scalatest.{Args, Status, Suites}
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TetrisTestSuite3_2 extends TetrisTestSuitesBase(
  new PlacementTests,
  new RotationTests,
  new RotateBackToStartTests,
  new MovementTests,
  new DropTests,
  new BlockedTests,
  new SpawnTests,
  new ClearLinesTests,
  new GameOverTests,
  new FullGameTests )
  {

    val MaxPoints = 5.5

    override def run(testName: Option[String], args: Args): Status = {
      val (scoreCounter, res) = runWithScoreCounter(testName,args)
      printf("You got %d/%d points!\n", scoreCounter.points, scoreCounter.maxPoints)
      printf("Your base grade for the tetris exercise will be : %.2f\n",scoreCounter.fraction() * MaxPoints)
      res
    }

  }
