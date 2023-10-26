package chipvm.logic.instructions

import chipvm.logic.ChipVMLogic
import chipvm.logic.ChipVMLogic._
import chipvm.logic.instructions.Instruction.modulo

abstract class LogicalOperation(index1: Short, index2: Short, operation: (Short, Short) => Int) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val value1 = vm.variableRegisters(index1)
    val value2 = vm.variableRegisters(index2)

    val result = operation(value1, value2).toShort
    val newRegisters = vm.variableRegisters.updated(index1, result)

    vm.copy(variableRegisters = newRegisters)
  }
}

case class Or(index1: Short, index2: Short) extends LogicalOperation(index1, index2, (x, y) => x | y)
case class And(index1: Short, index2: Short) extends LogicalOperation(index1, index2, (x, y) => x & y)
case class Xor(index1: Short, index2: Short) extends LogicalOperation(index1, index2, (x, y) => x ^ y)

abstract class MathOperation(index1: Short, index2: Short,
                             operation: (Short, Short) => (Short, Short)) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val value1 = vm.variableRegisters(index1)
    val value2 = vm.variableRegisters(index2)

    val (overflowedResult, overflow) = operation(value1, value2)

    val newRegisters = vm.variableRegisters.updated(index1, overflowedResult).updated(VFIndex, overflow)

    vm.copy(variableRegisters = newRegisters)
  }
}

case class AddRegister(index1: Short, index2: Short) extends MathOperation(index1, index2,
  (value1, value2) => {
    val result = (value1 + value2).toShort
    val overflow: Short = if (result > 255) 1 else 0
    val overflowedResult: Short = (result % 256).toShort
    (overflowedResult, overflow)
  })

abstract class SubtractOperation(index1: Short, index2: Short,
                                 resultFunction: (Short, Short) => Int,
                                 overflowFunction: (Short, Short) => Short) extends MathOperation(index1, index2,
  (value1, value2) => {
    val result = resultFunction(value1, value2).toShort
    val overflow: Short = overflowFunction(value1, value2)
    val overflowedResult: Short = modulo(result, 256).toShort
    (overflowedResult, overflow)
  }
)

case class SubtractRegister(minuendIndex: Short, subtrahendIndex: Short)
  extends SubtractOperation(minuendIndex, subtrahendIndex,
                            (minuend, subtrahend) => minuend - subtrahend,
                            (minuend, subtrahend) => if(minuend > subtrahend) 1 else 0)

case class SubtractRegisterReverse(subtrahendIndex: Short, minuendIndex: Short)
  extends SubtractOperation(subtrahendIndex, minuendIndex,
                           (subtrahend, minuend) => minuend - subtrahend,
                           (subtrahend, minuend) => if(minuend > subtrahend) 1 else 0)

case class ShiftRight(destination: Short, source: Short) extends MathOperation(destination, source,
  (_: Short, value2: Short) => {
    val result = fixSigned(value2.toByte >> 1).toShort
    val overflowFlag = (value2 & 0x1).toShort
    (result, overflowFlag)
  })

case class ShiftLeft(destination: Short, source: Short) extends MathOperation(destination, source,
  (_: Short, value2: Short) => {
    val result = fixSigned(value2.toByte << 1).toShort
    val overflowFlag = (value2 & 0x80).toShort
    (result, overflowFlag)
  })