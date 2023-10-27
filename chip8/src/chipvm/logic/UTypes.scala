package chipvm.logic

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

  def %(rhs: UByte): UByte =
    UByte((value % rhs.value).toByte)

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
    val shifted = toInt >>> rhs.value
    UByte(shifted.toByte)
  }

  def >(rhs: UByte): Boolean = toShort > rhs.toShort
  def <(rhs: UByte): Boolean = toShort < rhs.toShort

  // Quirk of JVM: if MSB is set, it will fill 1s on the left during Int casting (signed Two's complement)
  def toInt: Int = value & 0xFF
  def toShort: Short = toInt.toShort
  def toByte: Byte = value
  override def toString: String = toInt.toString
  def toBinary: String = {
    val binary = toInt.toBinaryString
    val padding = "0" * (8 - binary.length)
    padding + binary
  }
  def toBoolean: Seq[Boolean] = {
    (0 until 8).foldLeft(new Array[Boolean](8))((acc, el) => {
      acc(el) = ((value >> el) & 1) != 0
      acc
    }).toSeq.reverse
  }

  def copy(value: Byte = this.value): UByte =
    UByte(value)
}

object UByte {
  def apply(int: Int): UByte = UByte(int.toByte)
  def apply(short: Short): UByte = UByte(short.toByte)
  def apply(uShort: UShort): UByte = UByte(uShort.toByte)
  def apply(binary: String): UByte = UByte(Integer.parseInt(binary, 2).toByte)
}

case class UShort(value: Short) {
  def +(rhs: UShort): UShort =
    UShort((value + rhs.value).toShort)
  def -(rhs: UShort): UShort =
    UShort((value - rhs.value).toShort)
  def /(rhs: UShort): UShort =
    UShort((value / rhs.value).toShort)
  def *(rhs: UShort): UShort =
    UShort((value * rhs.value).toShort)

  def %(rhs: UShort): UShort =
    UShort((value % rhs.value).toShort)

  def &(rhs: UShort): UShort =
    UShort((value & rhs.value).toShort)
  def |(rhs: UShort): UShort =
    UShort((value | rhs.value).toShort)
  def ^(rhs: UShort): UShort =
    UShort((value ^ rhs.value).toShort)

  def <<(rhs: UShort): UShort =
    UShort((value << rhs.value).toShort)
  def >>(rhs: UShort): UShort = {
    // shifting to the RIGHT -> bits on the left matter
    val shifted = toInt >>> rhs.value
    UShort(shifted.toShort)
  }

  def toInt: Int = value & 0xFFFF
  def toByte: Byte = toInt.toByte
  override def toString: String = toInt.toString
  def toBinary: String = {
    val binary = toInt.toBinaryString
    val padding = "0" * (16 - binary.length)
    padding + binary
  }
  def this(value: UByte) = this(value.toShort)
}

object UShort {
  def apply(int: Int): UShort = UShort(int.toShort)
  def apply(byte: Byte): UShort = UShort(byte.toShort)
  def apply(uByte: UByte): UShort = UShort(uByte.toShort)
  def apply(binary: String): UShort = UShort(Integer.parseInt(binary, 2).toShort)
}

object Testing {
  // 32-bit int
  def intToBinary(value: Int): String = {
    val binary = value.toBinaryString
    val padding = "0" * (32 - binary.length)
    padding + binary
  }
}