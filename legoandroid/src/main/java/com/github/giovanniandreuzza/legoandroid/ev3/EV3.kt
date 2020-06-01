package com.github.giovanniandreuzza.legoandroid.ev3

import com.github.giovanniandreuzza.legoandroid.comm.*
import com.github.giovanniandreuzza.legoandroid.plug.motors.TachoMotor
import com.github.giovanniandreuzza.legoandroid.plug.sensors.GyroSensor
import com.github.giovanniandreuzza.legoandroid.plug.sensors.LightSensor
import com.github.giovanniandreuzza.legoandroid.plug.sensors.TouchSensor
import com.github.giovanniandreuzza.legoandroid.plug.sensors.UltrasonicSensor
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Specialized EV3 class with default Api.
 * Use this commodity class when you do not need to extend the Api class.
 */
class EV3(private val legoChannel: LegoChannel) {

    constructor(channel: Channel) : this(LegoChannelService(channel))


    /**
     * Access the light sensor of GenEV3.
     *
     * @param port the input port where the light sensor is connected to on the brick.
     * @return an object of type LightSensor.
     */
    fun getLightSensor(port: EV3InputPort) = LightSensor(this, port)

    /**
     * Access the touch sensor of GenEV3.
     *
     * @param port the input port where the touch sensor is connected to on the brick.
     * @return an object of type TouchSensor.
     */
    fun getTouchSensor(port: EV3InputPort) = TouchSensor(this, port)

    /**
     * Access the ultrasonic sensor of GenEV3.
     *
     * @param port the input port where the ultrasonic sensor is connected to on the brick.
     * @return an object of type UltrasonicSensor.
     */
    fun getUltrasonicSensor(port: EV3InputPort) = UltrasonicSensor(this, port)

    /**
     * Access the gyroscope sensor of GenEV3.
     *
     * @param port the input port where the gyroscope sensor is connected to on the brick.
     * @return an object of type GyroSensor.
     */
    fun getGyroSensor(port: EV3InputPort) = GyroSensor(this, port)

    /**
     * Access the tacho motor of GenEV3.
     *
     * @param port the output port where the motor is connected to on the brick.
     * @return an object of type TachoMotor.
     */
    fun getTachoMotor(port: EV3OutputPort) = TachoMotor(this, port)

    /**
     * Play a sound tone on the GenEV3 brick.
     *
     * @param volume   volume within the range [0 - 100].
     * @param freq     frequency in the range [ 250 - 10000 ].
     * @param duration duration in milliseconds.
     */
    fun soundTone(volume: Int, freq: Int, duration: Int) {
        legoChannel.sendNoReply(Bytecode().apply {
            addOpCode(Const.SOUND_CONTROL)
            addOpCode(Const.SOUND_TONE)
            addParameter(volume.toByte())
            addParameter(freq.toShort())
            addParameter(duration.toShort())
        })
    }


    private fun prefaceGetValue(ready: Byte, port: Byte, type: Byte, mode: Byte, nvalue: Byte) =
        Bytecode().apply {
            addOpCode(Const.INPUT_DEVICE)
            addOpCode(ready)
            addParameter(Const.LAYER_MASTER)
            addParameter(port)
            addParameter(type)
            addParameter(mode)
            addParameter(nvalue)
            addGlobalIndex(0x00.toByte())
        }

    /**
     * Low level method for sending direct commands to the GenEV3 brick.
     * This method sends the opInput_Device command READY_SI according to the official GenEV3 Developer Kit Documentation.
     *
     * @param port   port number.
     * @param type   type constant as defined in [Const], e.g. [Const.EV3_TOUCH] or [Const.EV3_COLOR].
     * @param mode   mode constant as defined in [Const], e.g. [Const.COL_AMBIENT] or [Const.GYRO_ANGLE].
     * @param nvalue number of values the command expects to return in the result array.
     * @return a future object containing an array of 32-bit floats whose length is equal to parameter `nvalues`.
     * @see [LEGO Mindstorms GenEV3 Firmware Developer Kit](https://le-www-live-s.legocdn.com/sc/media/files/ev3-developer-kit/lego%20mindstorms%20ev3%20firmware%20developer%20kit-7be073548547d99f7df59ddfd57c0088.pdf?la=en-us)
     */
    suspend fun getSiValue(port: Byte, type: Byte, mode: Byte, nvalue: Byte): FloatArray {
        val bc: Bytecode = prefaceGetValue(Const.READY_SI, port, type, mode, nvalue)
        val reply = legoChannel.send(4 * nvalue, bc)

        val result = FloatArray(nvalue.toInt())
        for (i in 0 until nvalue.toInt()) {
            val bData: ByteArray = reply.data.copyOfRange(4 * i, 4 * i + 4)
            result[i] = ByteBuffer.wrap(bData).order(ByteOrder.LITTLE_ENDIAN).float
        }
        return result
    }


    /**
     * Low level method for sending direct commands to the GenEV3 brick.
     * This method sends the opInput_Device command READY_PCT according to the official GenEV3 Developer Kit Documentation.
     *
     * @param port   port number.
     * @param type   type constant as defined in [Const], e.g. [Const.EV3_TOUCH] or [Const.EV3_COLOR].
     * @param mode   mode constant as defined in [Const], e.g. [Const.COL_AMBIENT] or [Const.GYRO_ANGLE].
     * @param nvalue number of values the command expects to return in the result array.
     * @return a future object containing an array of 16-bit integers whose length is equal to parameter `nvalues`.
     * @see [LEGO Mindstorms GenEV3 Firmware Developer Kit](https://le-www-live-s.legocdn.com/sc/media/files/ev3-developer-kit/lego%20mindstorms%20ev3%20firmware%20developer%20kit-7be073548547d99f7df59ddfd57c0088.pdf?la=en-us)
     */
    suspend fun getPercentValue(port: Byte, type: Byte, mode: Byte, nvalue: Byte): ShortArray {
        val bc: Bytecode = prefaceGetValue(Const.READY_PCT, port, type, mode, nvalue)
        val reply: Reply = legoChannel.send(2 * nvalue, bc)

        val result = ShortArray(nvalue.toInt())
        for (i in 0 until nvalue) {
            val bData = reply.data.copyOfRange(2 * i, 2 * i + 2)
            result[i] = ByteBuffer.wrap(bData).order(ByteOrder.LITTLE_ENDIAN).short
        }
        return result
    }

    /**
     * Low level send command with reply.
     *
     * @param reservation global reservation for the result in bytes.
     * @param bc          object of type [Bytecode] representing the command to be sent.
     * @return a [Single] object hosting the [Reply] object wrapping the reply by GenEV3.
     * @see [LEGO Mindstorms GenEV3 Firmware Developer Kit](https://le-www-live-s.legocdn.com/sc/media/files/ev3-developer-kit/lego%20mindstorms%20ev3%20firmware%20developer%20kit-7be073548547d99f7df59ddfd57c0088.pdf?la=en-us)
     */
    suspend fun send(reservation: Int, bc: Bytecode): Reply = legoChannel.send(reservation, bc)

    /**
     * Low level send command with no reply.
     *
     * @param bc object of type [Bytecode] representing the command to be sent.
     * @see [LEGO Mindstorms GenEV3 Firmware Developer Kit](https://le-www-live-s.legocdn.com/sc/media/files/ev3-developer-kit/lego%20mindstorms%20ev3%20firmware%20developer%20kit-7be073548547d99f7df59ddfd57c0088.pdf?la=en-us)
     */
    fun sendNoReply(bc: Bytecode) {
        legoChannel.sendNoReply(bc)
    }

}