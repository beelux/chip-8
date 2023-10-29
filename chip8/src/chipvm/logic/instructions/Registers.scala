package chipvm.logic.instructions

import chipvm.logic.ChipVMLogic.FontWidth
import chipvm.logic.{ChipVMLogic, UByte, UShort}
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
    val overflowedResult = UByte(modulo(result, UByte.max.toInt+1))
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
    vm.copy(i = value)
}

case class StoreMemory(index: UByte, address: UShort) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val newMemory = (0 to index.toByte).foldLeft(vm.memory)(
      (acc, index: Int) => {
        acc.updated(vm.i.toShort + index, vm.variableRegisters(index))
      }
    )

    val newI = Memory.getIafterMemoryOperation(vm, index)

    vm.copy(memory = newMemory, i = newI)
  }
}

case class LoadMemory(index: UByte, address: UShort) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val newRegisters = (0 to index.toByte).foldLeft(vm.variableRegisters)(
      (acc, index: Int) => {
        acc.updated(index, vm.memory(vm.i.toShort + index))
      }
    )

    val newI = Memory.getIafterMemoryOperation(vm, index)

    vm.copy(variableRegisters = newRegisters, i = newI)
  }
}

case class AddToIndex(index: UByte) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val newI = vm.i + vm.variableRegisters(index.toShort).toUShort
    vm.copy(i = newI)
  }
}

case class StoreBCD(index: UByte) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val value = vm.variableRegisters(index.toByte).toShort
    val x__ = UByte(value / 100)
    val _x_ = UByte((value % 100) / 10)
    val __x = UByte(value % 10)

    val newMemory : Vector[UByte] = vm.memory.updated(vm.i.toShort,     x__)
                                             .updated(vm.i.toShort + 1, _x_)
                                             .updated(vm.i.toShort + 2, __x)

    vm.copy(memory = newMemory)
  }
}

case class LoadFont(index: UByte) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val fontAddress = vm.variableRegisters(index.toShort).toInt * FontWidth

    vm.copy(i = UShort(fontAddress))
  }
}

object Memory {
  def getIafterMemoryOperation(vm: ChipVMLogic, index: UByte): UShort = {
      vm.quirks.getOrElse("memoryQuirk", false) match {
        case true => vm.i + index.toUShort + UShort(1)
        case false => vm.i
      }
  }
}