package tetris

class RotationTests extends TetrisTestSuiteBase {

  test("testIRotations") {
    checkGame( List(
      TestFrame(0,
        """....
          |IIII
          |....
          |...."""),
      TestFrame(0, List(RotateRight),
        """..I.
          |..I.
          |..I.
          |..I."""),
      TestFrame(0, List(RotateRight),
        """....
          |....
          |IIII
          |...."""),
      TestFrame(0, List(RotateRight),
        """.I..
          |.I..
          |.I..
          |.I..""")), hint = "Check Readme.md for how pieces should rotate")
  }

  test("testJRotations") {
    checkGame( List(
      TestFrame(1,
        """J..
          |JJJ
          |..."""),
      TestFrame(1, List(RotateRight),
        """.JJ
          |.J.
          |.J."""),
      TestFrame(0, List(RotateRight),
        """...
          |JJJ
          |..J"""),
      TestFrame(0, List(RotateRight),
        """.J.
          |.J.
          |JJ.""")
    ), hint = "Check Readme.md for how pieces should rotate")
  }

  test("testLRotations") {
    checkGame( List(
      TestFrame(2,
        """..L
          |LLL
          |..."""),
      TestFrame(1, List(RotateRight),
        """.L.
          |.L.
          |.LL"""),
      TestFrame(0, List(RotateRight),
        """...
          |LLL
          |L.."""),
      TestFrame(0, List(RotateRight),
        """LL.
          |.L.
          |.L.""")
    ), hint = "Check Readme.md for how pieces should rotate")
  }

  test("testORotations") {
    checkGame(     List(
      TestFrame(3,
        """...OO..
          |...OO..
          |......."""),
      TestFrame(3, List(RotateRight),
        """...OO..
          |...OO..
          |......."""),
      TestFrame(3, List(RotateRight),
        """...OO..
          |...OO..
          |......."""),
      TestFrame(3, List(RotateRight),
        """...OO..
          |...OO..
          |.......""")
    ), hint = "Check Readme.md for how pieces should rotate")
  }

  test("testSRotations") {
    checkGame(     List(
      TestFrame(4,
        """.SS
          |SS.
          |..."""),
      TestFrame(1, List(RotateRight),
        """.S.
          |.SS
          |..S"""),
      TestFrame(0, List(RotateRight),
        """...
          |.SS
          |SS."""),
      TestFrame(0, List(RotateRight),
        """S..
          |SS.
          |.S.""")
    ), hint = "Check Readme.md for how pieces should rotate")
  }

  test("testTRotations") {
    checkGame(     List(
      TestFrame(5,
        """.T.
          |TTT
          |..."""),
      TestFrame(1, List(RotateRight),
        """.T.
          |.TT
          |.T."""),
      TestFrame(0, List(RotateRight),
        """...
          |TTT
          |.T."""),
      TestFrame(0, List(RotateRight),
        """.T.
          |TT.
          |.T.""")
    ), hint = "Check Readme.md for how pieces should rotate")
  }

  test("testZRotations") {
    checkGame(   List(
      TestFrame(6,
        """ZZ.
          |.ZZ
          |..."""),
      TestFrame(1, List(RotateRight),
        """..Z
          |.ZZ
          |.Z."""),
      TestFrame(0, List(RotateRight),
        """...
          |ZZ.
          |.ZZ"""),
      TestFrame(0, List(RotateRight),
        """.Z.
          |ZZ.
          |Z..""")
    ), hint = "Check Readme.md for how pieces should rotate")
  }




}
