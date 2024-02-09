package chipvm.logic

abstract class CellType {
  val flipped: CellType
}
case object Fill  extends CellType {
  override val flipped: CellType = Empty
}
case object Empty   extends CellType {
  override val flipped: CellType = Fill
}
