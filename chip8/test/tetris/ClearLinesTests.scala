package tetris

class ClearLinesTests extends TetrisTestSuiteBase {

  test("testClear1Line") {
    checkGame(initialBoard =
      """...
        |...
        |...
        |...
        |ZZ.""",
      List(TestFrame(4,
        """.SS
          |SS.
          |...
          |...
          |ZZ."""),
        TestFrame(4, List(RotateRight),
          """.S.
            |.SS
            |..S
            |...
            |ZZ."""),
        TestFrame(4, List(Down),
          """...
            |.S.
            |.SS
            |..S
            |ZZ."""),
        TestFrame(4, List(Down),
          """...
            |...
            |.S.
            |.SS
            |ZZS"""),
        TestFrame(4, List(Down),
          """.SS
            |SS.
            |...
            |.S.
            |.SS""")
      ), hint = "When a new tetromino is placed, any full lines are removed from the board. If a lines is removed, the lines above it are moved down to such that the line is replaced by the line above it. Empty lines are added to the top to ensure the game has the correct height."
    )
  }

  test("testClear1LineMiddle") {
    checkGame(initialBoard =
      """....
        |....
        |....
        |....
        |.Z..
        |.Z..
        |ZZZ.
        |ZZ..
        |Z.Z.""",
      List(TestFrame(0,
        """....
          |IIII
          |....
          |....
          |.Z..
          |.Z..
          |ZZZ.
          |ZZ..
          |Z.Z."""),
        TestFrame(0, List(RotateRight),
          """..I.
            |..I.
            |..I.
            |..I.
            |.Z..
            |.Z..
            |ZZZ.
            |ZZ..
            |Z.Z."""),
        TestFrame(0, List(Right),
          """...I
            |...I
            |...I
            |...I
            |.Z..
            |.Z..
            |ZZZ.
            |ZZ..
            |Z.Z."""),
        TestFrame(0, List(Down),
          """....
            |...I
            |...I
            |...I
            |.Z.I
            |.Z..
            |ZZZ.
            |ZZ..
            |Z.Z."""),
        TestFrame(0, List(Down),
          """....
            |....
            |...I
            |...I
            |.Z.I
            |.Z.I
            |ZZZ.
            |ZZ..
            |Z.Z."""),
        TestFrame(0, List(Down),
          """....
            |....
            |....
            |...I
            |.Z.I
            |.Z.I
            |ZZZI
            |ZZ..
            |Z.Z."""),
        TestFrame(0, List(Down),
          """....
            |....
            |....
            |....
            |.Z.I
            |.Z.I
            |ZZZI
            |ZZ.I
            |Z.Z."""),
        TestFrame(0, List(Down),
          """....
            |....
            |....
            |....
            |.Z..
            |.Z.I
            |ZZZI
            |ZZ.I
            |Z.ZI"""),
        TestFrame(0, List(Down),
          """....
            |IIII
            |....
            |....
            |....
            |.Z..
            |.Z.I
            |ZZ.I
            |Z.ZI""")
      ), hint = "When a new tetromino is placed, any full lines are removed from the board. If a lines is removed, the lines above it are moved down to such that the line is replaced by the line above it. Empty lines are added to the top to ensure the game has the correct height."
    )
  }

  test("testClear2Lines") {
    checkGame(initialBoard =
      """....
        |....
        |....
        |....
        |ZZZ.
        |.Z..
        |ZZZ.
        |Z.Z.""",
      List(TestFrame(0,
        """....
          |IIII
          |....
          |....
          |ZZZ.
          |.Z..
          |ZZZ.
          |Z.Z."""),
        TestFrame(0, List(RotateRight),
          """..I.
            |..I.
            |..I.
            |..I.
            |ZZZ.
            |.Z..
            |ZZZ.
            |Z.Z."""),
        TestFrame(0, List(Right),
          """...I
            |...I
            |...I
            |...I
            |ZZZ.
            |.Z..
            |ZZZ.
            |Z.Z."""),
        TestFrame(0, List(Down),
          """....
            |...I
            |...I
            |...I
            |ZZZI
            |.Z..
            |ZZZ.
            |Z.Z."""),
        TestFrame(0, List(Down),
          """....
            |....
            |...I
            |...I
            |ZZZI
            |.Z.I
            |ZZZ.
            |Z.Z."""),
        TestFrame(0, List(Down),
          """....
            |....
            |....
            |...I
            |ZZZI
            |.Z.I
            |ZZZI
            |Z.Z."""),
        TestFrame(0, List(Down),
          """....
            |....
            |....
            |....
            |ZZZI
            |.Z.I
            |ZZZI
            |Z.ZI"""),
        TestFrame(0, List(Down),
          """....
            |IIII
            |....
            |....
            |....
            |....
            |.Z.I
            |Z.ZI""")
      ), hint = "When a new tetromino is placed, any full lines are removed from the board. If a lines is removed, the lines above it are moved down to such that the line is replaced by the line above it. Empty lines are added to the top to ensure the game has the correct height."
    )
  }

  test("testClear3Lines") {
    checkGame(initialBoard =
      """....
        |....
        |....
        |....
        |ZZZ.
        |.Z..
        |ZZZ.
        |ZZZ.""",
      List(TestFrame(0,
        """....
          |IIII
          |....
          |....
          |ZZZ.
          |.Z..
          |ZZZ.
          |ZZZ."""),
        TestFrame(0, List(RotateRight),
          """..I.
            |..I.
            |..I.
            |..I.
            |ZZZ.
            |.Z..
            |ZZZ.
            |ZZZ."""),
        TestFrame(0, List(Right),
          """...I
            |...I
            |...I
            |...I
            |ZZZ.
            |.Z..
            |ZZZ.
            |ZZZ."""),
        TestFrame(0, List(Down),
          """....
            |...I
            |...I
            |...I
            |ZZZI
            |.Z..
            |ZZZ.
            |ZZZ."""),
        TestFrame(0, List(Down),
          """....
            |....
            |...I
            |...I
            |ZZZI
            |.Z.I
            |ZZZ.
            |ZZZ."""),
        TestFrame(0, List(Down),
          """....
            |....
            |....
            |...I
            |ZZZI
            |.Z.I
            |ZZZI
            |ZZZ."""),
        TestFrame(0, List(Down),
          """....
            |....
            |....
            |....
            |ZZZI
            |.Z.I
            |ZZZI
            |ZZZI"""),
        TestFrame(0, List(Down),
          """....
            |IIII
            |....
            |....
            |....
            |....
            |....
            |.Z.I""")
      ), hint = "When a new tetromino is placed, any full lines are removed from the board. If a lines is removed, the lines above it are moved down to such that the line is replaced by the line above it. Empty lines are added to the top to ensure the game has the correct height."
    )
  }


  test("testClear4Lines") {
    checkGame(initialBoard =
      """....
        |....
        |....
        |...O
        |Z.ZZ
        |Z.ZZ
        |Z.ZZ
        |Z.ZZ
        |.ZZZ""",
      List(TestFrame(0,
        """....
          |IIII
          |....
          |...O
          |Z.ZZ
          |Z.ZZ
          |Z.ZZ
          |Z.ZZ
          |.ZZZ"""),
        TestFrame(0, List(RotateRight),
          """..I.
            |..I.
            |..I.
            |..IO
            |Z.ZZ
            |Z.ZZ
            |Z.ZZ
            |Z.ZZ
            |.ZZZ"""),
        TestFrame(0, List(Left),
          """.I..
            |.I..
            |.I..
            |.I.O
            |Z.ZZ
            |Z.ZZ
            |Z.ZZ
            |Z.ZZ
            |.ZZZ"""),
        TestFrame(0, List(Down),
          """....
            |.I..
            |.I..
            |.I.O
            |ZIZZ
            |Z.ZZ
            |Z.ZZ
            |Z.ZZ
            |.ZZZ"""),
        TestFrame(0, List(Down),
          """....
            |....
            |.I..
            |.I.O
            |ZIZZ
            |ZIZZ
            |Z.ZZ
            |Z.ZZ
            |.ZZZ"""),
        TestFrame(0, List(Down),
          """....
            |....
            |....
            |.I.O
            |ZIZZ
            |ZIZZ
            |ZIZZ
            |Z.ZZ
            |.ZZZ"""),
        TestFrame(0, List(Down),
          """....
            |....
            |....
            |...O
            |ZIZZ
            |ZIZZ
            |ZIZZ
            |ZIZZ
            |.ZZZ"""),
        TestFrame(0, List(Down),
          """....
            |IIII
            |....
            |....
            |....
            |....
            |....
            |...O
            |.ZZZ""")
      ), hint = "When a new tetromino is placed, any full lines are removed from the board. If a lines is removed, the lines above it are moved down to such that the line is replaced by the line above it. Empty lines are added to the top to ensure the game has the correct height."
    )
  }

}
