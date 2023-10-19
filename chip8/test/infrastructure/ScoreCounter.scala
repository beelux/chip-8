package infrastructure

class ScoreCounter {
  var maxPoints : Int = 0
  var points : Int  = 0

  def reset() : Unit = { maxPoints = 0 ; points = 0}

  def addScore(max : Int, actual : Int) : Unit  = {
    maxPoints += max
    points += actual
  }

  // fraction of total points attained
  def fraction() : Double = points.toDouble / maxPoints

}