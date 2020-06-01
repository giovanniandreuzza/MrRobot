package com.github.giovanniandreuzza.legoandroid.comm

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

class Bytecode {

    companion object {
        private const val BYTE_SIZE = 129.toByte()
        private const val SHORT_SIZE = 0x82.toByte()
        private const val INT_SIZE = 0x83.toByte()
    }

    private val underlying: ByteArrayOutputStream = ByteArrayOutputStream()
    private val out: DataOutputStream = DataOutputStream(underlying)

    /**
     * Append the give op-code.
     *
     * @param opcode the op-code as a byte.
     * @see [LEGO Mindstorms GenEV3 Firmware Developer Kit](https://le-www-live-s.legocdn.com/sc/media/files/ev3-developer-kit/lego%20mindstorms%20ev3%20firmware%20developer%20kit-7be073548547d99f7df59ddfd57c0088.pdf?la=en-us)
     */
    fun addOpCode(opcode: Byte) {
        out.writeByte(opcode.toInt())
    }

    /**
     * Append the give parameter.
     *
     * @param param the parameter as a 8-bit byte.
     * @see [LEGO Mindstorms GenEV3 Firmware Developer Kit](https://le-www-live-s.legocdn.com/sc/media/files/ev3-developer-kit/lego%20mindstorms%20ev3%20firmware%20developer%20kit-7be073548547d99f7df59ddfd57c0088.pdf?la=en-us)
     */
    fun addParameter(param: Byte) {
        out.writeByte(BYTE_SIZE.toInt())
        out.writeByte(param.toInt())
    }

    /**
     * Append the give parameter.
     *
     * @param param the parameter as a 16-bit short integer.
     * @see [LEGO Mindstorms GenEV3 Firmware Developer Kit](https://le-www-live-s.legocdn.com/sc/media/files/ev3-developer-kit/lego%20mindstorms%20ev3%20firmware%20developer%20kit-7be073548547d99f7df59ddfd57c0088.pdf?la=en-us)
     */
    fun addParameter(param: Short) {
        out.writeByte(SHORT_SIZE.toInt())
        out.writeByte(param.toInt())
        out.writeByte(param.toInt() shr 8)
    }

    /**
     * Append the give parameter.
     *
     * @param param the parameter as a 32-bit integer.
     * @see [LEGO Mindstorms GenEV3 Firmware Developer Kit](https://le-www-live-s.legocdn.com/sc/media/files/ev3-developer-kit/lego%20mindstorms%20ev3%20firmware%20developer%20kit-7be073548547d99f7df59ddfd57c0088.pdf?la=en-us)
     */
    fun addParameter(param: Int) {
        out.writeByte(INT_SIZE.toInt())
        out.writeByte(param)
        out.writeByte(param shr 8)
        out.writeByte(param shr 16)
        out.writeByte(param shr 24)
    }

    /**
     * Append the global index part.
     *
     * @param index the index as a 8-bit byte.
     * @see [LEGO Mindstorms GenEV3 Firmware Developer Kit](https://le-www-live-s.legocdn.com/sc/media/files/ev3-developer-kit/lego%20mindstorms%20ev3%20firmware%20developer%20kit-7be073548547d99f7df59ddfd57c0088.pdf?la=en-us)
     */
    fun addGlobalIndex(index: Byte) {
        out.writeByte(index + 0x60)
    }

    /**
     * Append another object of type [Bytecode].
     *
     * @param bc the bytecode to be appended.
     */
    fun append(bc: Bytecode) {
        out.write(bc.getBytes())
    }

    /**
     * Get the bytecode as a byte array.
     *
     * @return the byte array representing this bytecode.
     */
    fun getBytes(): ByteArray {
        return underlying.toByteArray()
    }


}