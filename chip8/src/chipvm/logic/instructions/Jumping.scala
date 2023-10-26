package chipvm.logic.instructions

import chipvm.logic._

// Simple jumping instructions

/**
 * Does nothing.
 * Used to halt execution in case of an unknown instruction.
 */
case class Nop() extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = vm.copy(pc = vm.pc-2)
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

// Conditional jumping instructions

case class SkipValEqual(index: Short, value: Short) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    vm.variableRegisters(index) match {
      case `value` => vm.copy(pc = vm.pc + 2)
      case _ => vm
    }
  }
}

case class SkipValNotEqual(index: Short, value: Short) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    vm.variableRegisters(index) match {
      case `value` => vm
      case _ => vm.copy(pc = vm.pc + 2)
    }
  }
}

case class SkipRegisterEqual(index1: Short, index2: Short) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val value1 = vm.variableRegisters(index1)
    val value2 = vm.variableRegisters(index2)

    if (value1 == value2)
      vm.copy(pc = vm.pc + 2)
    else
      vm
  }
}

case class SkipRegisterNotEqual(index1: Short, index2: Short) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val value1 = vm.variableRegisters(index1)
    val value2 = vm.variableRegisters(index2)

    if (value1 != value2)
      vm.copy(pc = vm.pc + 2)
    else
      vm
  }
}