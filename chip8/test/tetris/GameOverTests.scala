package tetris

class GameOverTests extends TetrisTestSuiteBase {

  test("testGameOver") {
    checkGame(List(TestFrame(2,
      """..L.
        |LLL.
        |....
        |...."""),
      TestFrame(2, List(Down),
        """....
          |..L.
          |LLL.
          |...."""),
      TestFrame(2, List(Down),
        """....
          |....
          |..L.
          |LLL."""),
      TestFrame(3, List(Down),
        """.OO.
          |.OO.
          |..L.
          |LLL."""),
      TestFrame(4, List(Down), GameOverDisplay()))
      , "The game is over if the blocks of a newly spawned tetromino are already occupied")
  }

  test("testNoEscapeGameOver") {
    checkGame( List(TestFrame(1,
      """J...
        |JJJ.
        |...."""),
      TestFrame(1, List(Down),
        """....
          |J...
          |JJJ."""),
      TestFrame(1, List(Down),
        GameOverDisplay()),
      TestFrame(1, List(Right),
        GameOverDisplay()))
      , "You cannot escape from a game over state by moving or rotating blocks.")
  }

  test("testNoEscapeGameOver2") {
    checkGame( List(TestFrame(1,
      """J...
        |JJJ.
        |....
        |...."""),
      TestFrame(1, List(RotateRight),
        """.JJ.
          |.J..
          |.J..
          |...."""),
      TestFrame(1, List(Left),
        """JJ..
          |J...
          |J...
          |...."""),
      TestFrame(1, List(Down),
        """....
          |JJ..
          |J...
          |J..."""),
      TestFrame(0, List(Down), GameOverDisplay()),
      TestFrame(0, List(RotateRight), GameOverDisplay()))
      , "You cannot escape from a game over state by moving or rotating blocks.")
  }

  test("testNoGameOverTightFit") {
    checkGame(initialBoard =
      """I..I
        |I..I
        |I..I
        |I..I""",
      List(TestFrame(3,
        """IOOI
          |IOOI
          |I..I
          |I..I"""),
        TestFrame(3, List(Down),
          """I..I
            |IOOI
            |IOOI
            |I..I"""))
      , "GameOver should happen only when the new spawning block collides with any placed block.")
  }

}
