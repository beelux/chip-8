package chipvm.logic

import chipvm.logic.ChipVMLogic._

abstract class Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic
}

case class Nop() extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = vm
}

case class ClearScreen() extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic =
    vm.copy(display = vm.display.clear())
}

case class Return() extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic =
    vm.copy(pc = vm.stack.head,
            stack = vm.stack.tail)
}

case class Jump(position: Int) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic =
    vm.copy(pc = position)
}

case class CallSubroutine(position: Int) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic =
    vm.copy(pc = position,
            stack = vm.stack.prepended(vm.pc))
}

case class Set(index: Short, value: Short) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val newRegisters = vm.variableRegisters.updated(index, value)
    vm.copy(variableRegisters = newRegisters)
  }
}

case class Add(index: Short, value: Short) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val registers = vm.variableRegisters
    val result = (registers(index) + value).toShort
    val newRegisters = registers.updated(index, result)
    vm.copy(variableRegisters = newRegisters)
  }
}

case class SetIndex(value: Int) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic =
    vm.copy(i = value)
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

object Instruction {
  def modifiesDisplay(instruction: Instruction): Boolean = {
    instruction match {
      case _: Draw => true
      case _: ClearScreen => true
      case _ => false
    }
  }
}