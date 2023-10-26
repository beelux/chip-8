package chipvm.logic.instructions

import chipvm.logic._
import chipvm.logic.ChipVMLogic._

case class ClearScreen() extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic =
    vm.copy(display = vm.display.clear())
}

case class Draw(x: Int, y: Int, height: Int) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val display = vm.display
    val memory = vm.memory
    val i = vm.i
    val registers = vm.variableRegisters

    val data = memory.slice(i, i + height)

    val drawnDisplay: Display = {
      data.zipWithIndex.foldLeft(display)((acc, byte) => {
        val line = byteToBool(byte._1)

        val curY = y + byte._2

        if (curY < Height) {
          line.zipWithIndex.foldLeft(acc)((acc, bit) => {
            val curX = x + bit._2

            if (bit._1 && curX < Width) {
              acc.flip(curX, curY)
            } else acc
          })
        } else acc
      })
    }

    val newRegisters: Array[Short] = registers.updated(VFIndex,
      if (drawnDisplay.collision)
        1
      else 0)
    val newDisplay = drawnDisplay.clearCollision()

    vm.copy(display = newDisplay,
      variableRegisters = newRegisters)
  }
}
