package net.revive.framework.nms.util

object ByteUtil {
    fun setBit(input: Byte, bitField: Int, set: Boolean): Byte {
        var value = input
        value = if (set) {
            (value.toInt() or bitField).toByte()
        } else {
            (value.toInt() and bitField.inv()).toByte()
        }
        return value
    }
}