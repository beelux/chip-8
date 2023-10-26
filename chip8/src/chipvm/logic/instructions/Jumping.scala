package chipvm.logic.instructions

import chipvm.logic._

case class Nop() extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic = vm.copy(pc = vm.pc-2)
}

case class Return() extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic =
    vm.copy(pc = vm.stack.head,
      stack = vm.stack.tail)
}

case class Jump(position: Int) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic =
    vm.copy(pc = position)
}

case class CallSubroutine(position: Int) extends Instruction {
  def execute(vm: ChipVMLogic): ChipVMLogic =
    vm.copy(pc = position,
      stack = vm.stack.prepended(vm.pc))
}
