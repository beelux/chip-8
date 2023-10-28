package chipvm.logic.instructions

import chipvm.logic.{ChipVMLogic, UByte, UShort}

abstract class SkipKey(index: UByte, skipValue: Boolean) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val keyIndex = vm.variableRegisters(index.toInt).toInt

    val isKeyPressed = vm.pressedKeys(keyIndex)
    val clearedKeys = vm.pressedKeys.updated(keyIndex, false)

    val keepValue = !skipValue
    val pc = isKeyPressed match {
      case `skipValue` => vm.pc + 2
      case `keepValue` => vm.pc
    }

    vm.copy(pc = pc, pressedKeys = clearedKeys)
  }
}

case class SkipKeyPressed(index: UByte) extends SkipKey(index, skipValue = true)
case class SkipKeyNotPressed(index: UByte) extends SkipKey(index, skipValue = false)

case class WaitForKey(index: UByte) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val firstKeyPressed = vm.pressedKeys.indexOf(true)

    firstKeyPressed match {
      case -1 => vm.copy(pc = vm.pc - 2)
      case key =>
        val registers = vm.variableRegisters.updated(index.toInt, UByte(key))
        vm.copy(variableRegisters = registers)
    }
  }
}