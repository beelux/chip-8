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
    vm.copy(pc = vm.stack.head.toInt,
      stack = vm.stack.tail)
}

case class Jump(position: UShort) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic =
    vm.copy(pc = position.toInt)
}

case class CallSubroutine(position: UShort) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic =
    vm.copy(pc = position.toInt,
      stack = vm.stack.prepended(UShort(vm.pc)))
}

// Conditional jumping instructions

case class SkipValEqual(index: UByte, value: UByte) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    vm.variableRegisters(index.toByte) match {
      case `value` => vm.copy(pc = vm.pc + 2)
      case _ => vm
    }
  }
}

case class SkipValNotEqual(index: UByte, value: UByte) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    vm.variableRegisters(index.toByte) match {
      case `value` => vm
      case _ => vm.copy(pc = vm.pc + 2)
    }
  }
}

case class SkipRegisterEqual(index1: UByte, index2: UByte) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val value1 = vm.variableRegisters(index1.toByte)
    val value2 = vm.variableRegisters(index2.toByte)

    if (value1 == value2)
      vm.copy(pc = vm.pc + 2)
    else
      vm
  }
}

case class SkipRegisterNotEqual(index1: UByte, index2: UByte) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val value1 = vm.variableRegisters(index1.toByte)
    val value2 = vm.variableRegisters(index2.toByte)

    if (value1 != value2)
      vm.copy(pc = vm.pc + 2)
    else
      vm
  }
}