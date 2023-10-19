package tetris

class RotateBackToStartTests extends TetrisTestSuiteBase {

  // tests where we rotate a couple of times to get
  // back to the original position


  test("testRotate360") {
    checkGame(    List(
      TestFrame(0,
        """....
          |IIII
          |....
          |...."""),
      TestFrame(0, List(RotateRight,RotateRight,RotateRight,RotateRight),
        """....
          |IIII
          |....
          |....""")
    ), hint = "Rotating clockwise 4 times  gives the initial orientation.")
  }

  test("testRotate360CC") {
    checkGame(       List(
      TestFrame(5,
        """.T.
          |TTT
          |..."""),
      TestFrame(0, List(RotateLeft,RotateLeft,RotateLeft,RotateLeft),
        """.T.
          |TTT
          |...""")
    ), hint = "Rotating counter-clockwise 4 times  gives the initial orientation.")
  }

  test("testRotateLeftRight") {
    checkGame(   List(
      TestFrame(6,
        """ZZ.
          |.ZZ
          |..."""),
      TestFrame(6, List(RotateRight),
        """..Z
          |.ZZ
          |.Z."""),
      TestFrame(6, List(RotateLeft),
        """ZZ.
          |.ZZ
          |...""")
    ), hint = "Rotating right and then left gives the original orientation.")
  }

  test("testRotateRightLeft") {
    checkGame( List(
      TestFrame(2,
        """..L
          |LLL
          |..."""),
      TestFrame(0, List(RotateLeft),
        """LL.
          |.L.
          |.L."""),
      TestFrame(0, List(RotateLeft),
        """...
          |LLL
          |L.."""),
      TestFrame(0, List(RotateRight),
        """LL.
          |.L.
          |.L."""),
      TestFrame(2, List(RotateRight),
        """..L
          |LLL
          |..."""),
    ), hint = "Rotating left and then right gives the original orientation.")
  }
}
