package chipvm.logic.instructions

import chipvm.logic.{ChipVMLogic, UByte}
import chipvm.logic.ChipVMLogic._
import chipvm.logic.instructions.Instruction.modulo

abstract class LogicalOperation(index1: UByte, index2: UByte, operation: (UByte, UByte) => UByte) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val value1 = vm.variableRegisters(index1.toByte)
    val value2 = vm.variableRegisters(index2.toByte)

    val result = operation(value1, value2)
    val newRegisters = vm.variableRegisters.updated(index1.toByte, result)

    vm.copy(variableRegisters = newRegisters)
  }
}

abstract class MathOperation(index1: UByte, index2: UByte,
                             operation: (UByte, UByte) => (UByte, UByte)) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val value1 = vm.variableRegisters(index1.toByte)
    val value2 = vm.variableRegisters(index2.toByte)

    val (overflowedResult, overflow) = operation(value1, value2)

    val newRegisters = vm.variableRegisters.updated(index1.toByte, overflowedResult).updated(VFIndex, overflow)

    vm.copy(variableRegisters = newRegisters)
  }
}

case class Or(index1: UByte, index2: UByte) extends MathOperation(index1, index2, (x, y) => (x | y, UByte(0)))
case class And(index1: UByte, index2: UByte) extends MathOperation(index1, index2, (x, y) => (x & y, UByte(0)))
case class Xor(index1: UByte, index2: UByte) extends MathOperation(index1, index2, (x, y) => (x ^ y, UByte(0)))

case class AddRegister(index1: UByte, index2: UByte) extends MathOperation(index1, index2,
  (value1, value2) => {
    val result = value1 + value2
    val overflowableResult = value1.toShort + value2.toShort
    val overflow: UByte = if (overflowableResult > 255) UByte(1) else UByte(0)
    (result, overflow)
  })

abstract class SubtractOperation(index1: UByte, index2: UByte,
                                 resultFunction: (UByte, UByte) => UByte,
                                 overflowFunction: (UByte, UByte) => UByte) extends MathOperation(index1, index2,
  (value1, value2) => {
    val result = resultFunction(value1, value2)
    val overflow = overflowFunction(value1, value2)
    (result, overflow)
  }
)

case class SubtractRegister(minuendIndex: UByte, subtrahendIndex: UByte)
  extends SubtractOperation(minuendIndex, subtrahendIndex,
                            (minuend, subtrahend) => minuend - subtrahend,
                            (minuend, subtrahend) => if(minuend > subtrahend) UByte(1) else UByte(0))

case class SubtractRegisterReverse(subtrahendIndex: UByte, minuendIndex: UByte)
  extends SubtractOperation(subtrahendIndex, minuendIndex,
                           (subtrahend, minuend) => minuend - subtrahend,
                           (subtrahend, minuend) => if(minuend > subtrahend) UByte(1) else UByte(0))

case class ShiftRight(destination: UByte, source: UByte) extends MathOperation(destination, source,
  (_: UByte, value2: UByte) => {
    val result = value2 >> UByte(1)
    val overflowFlag = value2 & UByte(0x1)
    // set MSB as the overflow value's
    //val result = (intermediate | (value2 & 0x80)).toShort
    (result, overflowFlag)
  })

case class ShiftLeft(destination: UByte, source: UByte) extends MathOperation(destination, source,
  (_: UByte, value2: UByte) => {
    val result = value2 << UByte(1)
    val overflowFlag = (value2 & UByte(0x80)) >> UByte(7)
    // set LSB as the overflow value
    // val result = (intermediate | (value2 & 0x1)).toShort
    (result, overflowFlag)
  })

case class Random(index: UByte, mask: UByte) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val generator = scala.util.Random
    generator.setSeed(System.currentTimeMillis())

    val random = UByte(generator.nextInt(256))
    val result = random & mask

    vm.copy(variableRegisters = vm.variableRegisters.updated(index.toByte, result))
  }
}