package net.mystoria.framework.nms.util

object ByteUtil {
    fun setBit(value: Byte, bitField: Int, set: Boolean): Byte {
        var value = value
        value = if (set) {
            (value.toInt() or bitField).toByte()
        } else {
            (value.toInt() and bitField.inv()).toByte()
        }
        return value
    }
}