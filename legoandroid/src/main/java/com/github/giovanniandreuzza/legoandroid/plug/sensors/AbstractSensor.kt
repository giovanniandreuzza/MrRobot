package com.github.giovanniandreuzza.legoandroid.plug.sensors

import com.github.giovanniandreuzza.legoandroid.ev3.EV3
import com.github.giovanniandreuzza.legoandroid.ev3.EV3InputPort
import com.github.giovanniandreuzza.legoandroid.plug.Plug

/**
 * Abstract class for sensors.
 *
 * Create an instance of this class given an object of type {@link EV3.Api}, and input port and the type constant.
 * This constructor is meant for subclasses specializing a specific sensor.
 *
 * @param api  an object of type [EV3].
 * @param port input port where the sensor is attached to.
 * @param type type constant, e.g. [com.github.giovanniandreuzza.legoandroid.comm.Const.EV3_COLOR].
 *
 */
abstract class AbstractSensor(api: EV3, port: EV3InputPort, private val type: Byte) :
    Plug<EV3InputPort>(api, port) {

    /**
     * Send a PCT command and converts the reply by applying the given function.
     *
     * @param mode   mode constant as defined in [com.github.giovanniandreuzza.legoandroid.comm.Const], e.g. [com.github.giovanniandreuzza.legoandroid.comm.Const.GYRO_ANGLE].
     * @param nvalue number of values for the reply.
     * @param f      function object for converting the array of shorts into an object of type [T].
     * @param <T>    generic type that is the generic argument of the resulting future.
     * @return a future hosting an object of type [T].
     */
    private suspend fun <T> getPercent(
        mode: Byte,
        nvalue: Byte,
        f: (x: ShortArray) -> T
    ): T = f(api.getPercentValue(port.toByte(), type, mode, nvalue))


    /**
     * Send a PCT command with 1 nvalue and converts the reply by applying the given function.
     * Calling this method is like calling {@link #getPercent(int, int, Function)} with parameter nvalue equal to 1.
     *
     * @param mode mode constant as defined in [com.github.giovanniandreuzza.legoandroid.comm.Const], e.g. [com.github.giovanniandreuzza.legoandroid.comm.Const.GYRO_ANGLE].
     * @param f    function object for converting the array of shorts into an object of type {@link T}.
     * @param <T>  generic type that is the generic argument of the resulting future.
     * @return a future hosting an object of type {@link T}.
     */
    protected suspend fun <T> getPercent1(mode: Byte, f: (x: Short) -> T): T =
        getPercent(mode = mode, nvalue = 1) {
            f(it[0])
        }


    /**
     * Send a PCT command with 1 nvalue and returns the resulting short.
     *
     * @param mode mode constant as defined in [com.github.giovanniandreuzza.legoandroid.comm.Const], e.g. [com.github.giovanniandreuzza.legoandroid.comm.Const.GYRO_ANGLE].
     * @return a future hosting a 16-bit short.
     */
    protected suspend fun getPercent1(mode: Byte): Short = getPercent1(mode) { it }

    /**
     * Send a SI command and converts the reply by applying the given function.
     *
     * @param mode   mode constant as defined in [com.github.giovanniandreuzza.legoandroid.comm.Const], e.g. [com.github.giovanniandreuzza.legoandroid.comm.Const.GYRO_ANGLE].
     * @param nvalue number of values for the reply.
     * @param f      function object for converting the array of floats into an object of type {@link T}.
     * @param <T>    generic type that is the generic argument of the resulting future.
     * @return a future hosting an object of type {@link T}.
     */
    protected suspend fun <T> getSi(mode: Byte, nvalue: Byte, f: (x: FloatArray) -> T): T =
        f(api.getSiValue(port.toByte(), type, mode, nvalue))


    /**
     * Send a SI command with 1 nvalue and converts the reply by applying the given function.
     * Calling this method is like calling {@link #getPercent(int, int, Function)} with parameter nvalue equal to 1.
     *
     * @param mode mode constant as defined in [com.github.giovanniandreuzza.legoandroid.comm.Const], e.g. [com.github.giovanniandreuzza.legoandroid.comm.Const.GYRO_ANGLE].
     * @param f    function object for converting the array of shorts into an object of type {@link T}.
     * @param <T>  generic type that is the generic argument of the resulting future.
     * @return a future hosting an object of type {@link T}.
     */
    protected suspend fun <T> getSi1(mode: Byte, f: (x: Float) -> T): T =
        getSi(mode = mode, nvalue = 1) {
            f(it[0])
        }


    /**
     * Send a PCT command with 1 nvalue and returns the resulting float.
     *
     * @param mode mode constant as defined in [com.github.giovanniandreuzza.legoandroid.comm.Const], e.g. [com.github.giovanniandreuzza.legoandroid.comm.Const.GYRO_ANGLE].
     * @return a future hosting a 32-bit float.
     */
    protected suspend fun getSi1(mode: Byte): Float = getSi1(mode) {
        it
    }

}