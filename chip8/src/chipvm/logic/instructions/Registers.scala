package chipvm.logic.instructions

import chipvm.logic.ChipVMLogic
import chipvm.logic.instructions.Instruction.modulo

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
    val overflowedResult = modulo(result, 256).toShort
    val newRegisters = registers.updated(index, overflowedResult)
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

case class StoreMemory(index: Short, address: Int) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val newMemory = (0 to index).foldLeft(vm.memory)(
      (acc, index: Int) => {
        acc.updated(vm.i + index, vm.variableRegisters(index))
      }
    )

    vm.copy(memory = newMemory)
  }
}

case class LoadMemory(index: Short, address: Int) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val newRegisters = (0 to index).foldLeft(vm.variableRegisters)(
      (acc, index: Int) => {
        acc.updated(index, vm.memory(vm.i + index))
      }
    )

    vm.copy(variableRegisters = newRegisters)
  }
}