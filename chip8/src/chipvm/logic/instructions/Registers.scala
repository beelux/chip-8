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

case class AddToIndex(index: Short) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val newI = vm.i + vm.variableRegisters(index)
    vm.copy(i = newI)
  }
}

case class StoreBCD(index: Short) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val value = vm.variableRegisters(index)
    val x__ : Short = (value / 100).toShort
    val _x_ : Short = ((value % 100) / 10).toShort
    val __x : Short = (value % 10).toShort

    val newMemory : Array[Short] = vm.memory.updated(vm.i,     x__)
                                            .updated(vm.i + 1, _x_)
                                            .updated(vm.i + 2, __x)

    vm.copy(memory = newMemory)
  }
}