package chipvm.logic.instructions

import chipvm.logic._

abstract class Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic
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
