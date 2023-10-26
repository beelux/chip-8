package chipvm.logic.instructions

import chipvm.logic.ChipVMLogic

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

case class Copy(destination: Short, source: Short) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val registers = vm.variableRegisters

    val value = registers(source)
    val newRegisters = registers.updated(destination, value)
    vm.copy(variableRegisters = newRegisters)
  }
}

case class SetIndex(value: Int) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic =
    vm.copy(i = value)
}
