package tetris

class PlacementTests extends TetrisTestSuiteBase {

  test("testPlacementI") {
    checkGame(List(TestFrame(0,
      """.......
        |..IIII.
        |.......
        |.......""")), hint = "See Readme.md for how to place tetrominos")
  }

  test("testPlacementIEven") {
    checkGame( List(
      TestFrame(0,
        """......
          |.IIII.
          |......
          |......"""),
    ), hint = "See Readme.md for how to place tetrominos")
  }


  test("testPlacementJ") {
    checkGame( List(
      TestFrame(1,
        """..J....
          |..JJJ..
          |......."""),
    ), hint = "See Readme.md for how to place tetrominos")
  }

  test("testPlacementL") {
    checkGame( List(
      TestFrame(2,
        """...L..
          |.LLL..
          |......"""),
    ), hint = "See Readme.md for how to place tetrominos")
  }

  test("testPlacementO") {
    checkGame( List(
      TestFrame(3,
        """...OO..
          |...OO..
          |......."""),
    ), hint = "See Readme.md for how to place tetrominos")
  }

  test("testPlacementOEven") {
    checkGame( List(
      TestFrame(3,
        """..OO..
          |..OO..
          |......"""),
    ), hint = "See Readme.md for how to place tetrominos")
  }

  test("testPlacementS") {
    checkGame(  List(
      TestFrame(4,
        """..SS.
          |.SS..
          |....."""),
    ), hint = "See ReadMe.md for how to place tetrominos")
  }

  test("testPlacementT") {
    checkGame(   List(
      TestFrame(5,
        """...T....
          |..TTT...
          |........"""),
    ), hint = "See ReadMe.md for how to place tetrominos")
  }

  test("testPlacementZ") {
    checkGame(  List(
      TestFrame(6,
        """.ZZ...
          |..ZZ..
          |......"""),
    ), hint = "See ReadMe.md for how to place tetrominos")
  }
}
