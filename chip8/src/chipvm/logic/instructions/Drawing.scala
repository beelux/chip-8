package chipvm.logic.instructions

import chipvm.logic._
import chipvm.logic.ChipVMLogic._

case class ClearScreen() extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    if (vm.isDrawable)
      vm.copy(display = vm.display.clear(), isDrawable = false)
    else
      vm.copy(pc = vm.pc - 2)
  }
}

case class Draw(x: UByte, y: UByte, height: UByte) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    if (!vm.isDrawable)
      vm.copy(pc = vm.pc - 2)
    else {
      val display = vm.display
      val memory = vm.memory
      val i = vm.i
      val registers = vm.variableRegisters

      val data = memory.slice(i, i + height.toInt)

      val drawnDisplay: Display = {
        data.zipWithIndex.foldLeft(display)((acc, byte) => {
          val line = byte._1.toBoolean

          val curY = y.toInt + byte._2

          if (curY < Height) {
            line.zipWithIndex.foldLeft(acc)((acc, bit) => {
              val curX = x.toInt + bit._2

              if (bit._1 && curX < Width) {
                acc.flip(curX, curY)
              } else acc
            })
          } else acc
        })
      }

      val newRegisters: Array[UByte] = registers.updated(VFIndex,
        if (drawnDisplay.collision) UByte(1)
        else UByte(0))
      val newDisplay = drawnDisplay.clearCollision()

      vm.copy(display = newDisplay,
        variableRegisters = newRegisters,
        isDrawable = false)
    }
  }
}
