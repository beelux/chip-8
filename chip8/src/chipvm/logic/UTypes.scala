package chipvm.logic

//trait UType[T] {
//  def value: T
//  // Basic Operators
//  def +(rhs: UType[T]): UType[T]
//  def -(rhs: UType[T]): UType[T]
//  def /(rhs: UType[T]): UType[T]
//  def *(rhs: UType[T]): UType[T]
//
//  // Logic Operators
//  def &(rhs: UType[T]): UType[T]
//  def |(rhs: UType[T]): UType[T]
//  def ^(rhs: UType[T]): UType[T]
//
//  // Shifting
//  def <<(rhs: UType[T]): UType[T]
//  def >>(rhs: UType[T]): UType[T]
//}

/**
 * Unsigned Byte
 *
 * Scala does not have unsigned types (excl. char), this is a way to get around that.
 * It wraps around it, and the operations behave as they usually would.
 *
 * Note that most operations work without much trouble, excluding shifting right.
 * This is due to Two's complement arithmetic.
 *
 * @param value the value of the unsigned byte
 */
case class UByte(value: Byte) {
  def +(rhs: UByte): UByte =
    UByte((value + rhs.value).toByte)
  def -(rhs: UByte): UByte =
    UByte((value - rhs.value).toByte)
  def /(rhs: UByte): UByte =
    UByte((value / rhs.value).toByte)
  def *(rhs: UByte): UByte =
    UByte((value * rhs.value).toByte)

  def &(rhs: UByte): UByte =
    UByte((value & rhs.value).toByte)
  def |(rhs: UByte): UByte =
    UByte((value | rhs.value).toByte)
  def ^(rhs: UByte): UByte =
    UByte((value ^ rhs.value).toByte)

  def <<(rhs: UByte): UByte =
    UByte((value << rhs.value).toByte)
  def >>(rhs: UByte): UByte = {
    // shifting to the RIGHT -> bits on the left matter
    val shifted = this.toInt >>> rhs.value
    UByte(shifted.toByte)
  }

  // Quirk of JVM: if MSB is set, it will fill 1s on the left during Int casting (signed Two's complement)
  def toInt: Int = value & 0xFF
  override def toString: String = toInt.toString
  def toBinary: String = {
    val binary = toInt.toBinaryString
    val padding = "0" * (8 - binary.length)
    padding + binary
  }

  def this(value: Short) = this(value.toByte)
  def this(binary: String) = {
    this(Integer.parseInt(binary, 2).toByte)
  }
}

case class UShort(value: Short) {
  def +(rhs: UShort): UShort = ???
  def -(rhs: UShort): UShort = ???
  def /(rhs: UShort): UShort = ???
  def *(rhs: UShort): UShort = ???

  def &(rhs: UShort): UShort = ???
  def |(rhs: UShort): UShort = ???
  def ^(rhs: UShort): UShort = ???

  def <<(rhs: UShort): UShort = ???
  def >>(rhs: UShort): UShort = ???
}

object Testing {
  // 32-bit int
  def intToBinary(value: Int): String = {
    val binary = value.toBinaryString
    val padding = "0" * (32 - binary.length)
    padding + binary
  }
}