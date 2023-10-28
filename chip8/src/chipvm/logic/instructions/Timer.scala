package chipvm.logic.instructions

import chipvm.logic.{ChipVMLogic, UByte, UShort}

// case (0xF, _, 0x0, 0x7)   => CopyDelayTimerToRegister(_X__)
//      case (0xF, _, 0x1, 0x5)   => SetSoundTimer(_X__)
//      case (0xF, _, 0x1, 0x8)   => SetDelayTimer(_X__)

case class CopyDelayTimerToRegister(index: UByte) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val registers = vm.variableRegisters.updated(index.toInt, vm.delayTimer)
    vm.copy(variableRegisters = registers)
  }
}

abstract class SetTimer(index: UByte, setTimer: (UByte, ChipVMLogic) => ChipVMLogic) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = {
    val timerValue = vm.variableRegisters(index.toInt)
    setTimer(timerValue, vm)
  }
}

case class SetSoundTimer(index: UByte) extends SetTimer(index,
  (timerValue, vm) =>
    vm.copy(soundTimer = timerValue)
)

case class SetDelayTimer(index: UByte) extends SetTimer(index,
  (timerValue, vm) =>
    vm.copy(delayTimer = timerValue)
)
