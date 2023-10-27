package chipvm.logic.instructions

import chipvm.logic.{ChipVMLogic, UByte, UShort}
import chipvm.logic.ChipVMLogic.fixSigned
import chipvm.logic.instructions.Instruction.modulo

case class Set(index: UByte, value: UByte) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val newRegisters = vm.variableRegisters.updated(index.toByte, value)
    vm.copy(variableRegisters = newRegisters)
  }
}

case class Add(index: UByte, value: UByte) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val registers = vm.variableRegisters

    val result = (registers(index.toByte) + value).toShort
    val overflowedResult = UByte(modulo(result, 256))
    val newRegisters = registers.updated(index.toByte, overflowedResult)
    vm.copy(variableRegisters = newRegisters)
  }
}

case class Copy(destination: UByte, source: UByte) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val registers = vm.variableRegisters

    val value = registers(source.toInt)
    val newRegisters = registers.updated(destination.toInt, value)
    vm.copy(variableRegisters = newRegisters)
  }
}

case class SetIndex(value: UShort) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic =
    vm.copy(i = value.toInt)
}

case class StoreMemory(index: UByte, address: Int) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val newMemory = (0 to index.toByte).foldLeft(vm.memory)(
      (acc, index: Int) => {
        acc.updated(vm.i + index, vm.variableRegisters(index))
      }
    )

    vm.copy(memory = newMemory)
  }
}

case class LoadMemory(index: UByte, address: Int) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val newRegisters = (0 to index.toByte).foldLeft(vm.variableRegisters)(
      (acc, index: Int) => {
        acc.updated(index, vm.memory(vm.i + index))
      }
    )

    vm.copy(variableRegisters = newRegisters)
  }
}

case class AddToIndex(index: UByte) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val newI = vm.i + vm.variableRegisters(index.toShort).toShort
    vm.copy(i = newI)
  }
}

case class StoreBCD(index: UByte) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val value = vm.variableRegisters(index.toByte).toShort
    val x__ = UByte(value / 100)
    val _x_ = UByte((value % 100) / 10)
    val __x = UByte(value % 10)

    val newMemory : Array[UByte] = vm.memory.updated(vm.i,     x__)
                                            .updated(vm.i + 1, _x_)
                                            .updated(vm.i + 2, __x)

    vm.copy(memory = newMemory)
  }
}