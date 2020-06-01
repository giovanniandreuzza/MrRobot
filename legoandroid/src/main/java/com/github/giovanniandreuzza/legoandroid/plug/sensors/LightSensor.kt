package com.github.giovanniandreuzza.legoandroid.plug.sensors

import com.github.giovanniandreuzza.legoandroid.comm.Const
import com.github.giovanniandreuzza.legoandroid.ev3.EV3
import com.github.giovanniandreuzza.legoandroid.ev3.EV3InputPort
import io.reactivex.Single

/**
 * Instances of this class allow operations on the light sensor connected to GenEV3 via an input port.
 */
class LightSensor(api: EV3, port: EV3InputPort) : AbstractSensor(api, port, Const.EV3_COLOR) {

    /**
     * Get the reflected light from the sensor (device mode GenEV3-Color-Reflected).
     * Sets the sensor LED color to red.
     *
     * @return a [Single] object hosting the 16-bit integer within the range [ 0 - 100 ] returned by GenEV3.
     */
    suspend fun getReflected(): Short = getPercent1(Const.COL_REFLECT)

    /**
     * Get the ambient light from the sensor (device mode GenEV3-Color-Ambient).
     * Sets the sensor LED color to blue (dimly lit).
     *
     * @return a {@link Future} object hosting the 16-bit integer within the range [ 0 - 100 ] returned by GenEV3.
     */
    suspend fun getAmbient(): Short = getPercent1(Const.COL_AMBIENT)

    /**
     * Get the color value from the sensor (device mode GenEV3-Color-Color).
     * Sets the sensor LED color to white (all LEDs rapidly cycling).
     *
     * @return a {@link Future} object hosting the value of type {@link Color} returned by GenEV3.
     */
    suspend fun getColor(): Color = getSi1(Const.COL_COLOR) { Color.values()[it.toInt()] }


    /**
     * Get the raw RGB values from the sensor (device mode GenEV3-Color-RGB-Raw).
     * Sets the sensor LED color to white (all LEDs rapidly cycling).
     *
     * @return a {@link Future} object hosting the object of type {@link Rgb} returned by GenEV3.
     * @deprecated On current GenEV3 firmwares this command seems to return wrong or imprecise values. Use at own risk.
     */
    suspend fun getRgb(): Rgb = getSi(Const.COL_RGB, 3) { rgb ->
        Rgb(rgb[0].toInt(), rgb[1].toInt(), rgb[2].toInt())
    }

    /**
     * This class represents a raw RGB color value via a triple of integers.
     *
     * Create an object given the 3 integer values for each color component.
     * @param R 8-bit red component in range [ 0 - 255 ]
     * @param G 8-bit red component in range [ 0 - 255 ]
     * @param B 8-bit red component in range [ 0 - 255 ]
     */
    class Rgb(private val R: Int, private val G: Int, private val B: Int) {
        /**
         * Calculate the RGB 24-bit color value (8 bits for each component).
         *
         * @return the RGB24 value as an integer.
         */
        fun toRGB24(): Int {
            return R shl 16 or (G shl 8) or B
        }

        /**
         * Calculate the ARGB 32-bit color value (four 8-bit components, including alpha channel).
         * Alpha channel defaults to 255 for maximum opaqueness.
         *
         * @return the ARGB32 value as an integer.
         */
        fun toARGB32(): Int {
            return -0x1000000 or toRGB24()
        }

    }

    /**
     * This enum type represents the possible colors returned by the sensor in device mode GenEV3-Color-RGB-Raw.
     */
    enum class Color {
        TRANSPARENT, BLACK, BLUE, GREEN, YELLOW, RED, WHITE, BROWN;

        /**
         * Calculate the ARGB 32-bit color value (8 bits for each component, including alpha channel).
         *
         * @return the ARGB32 value as an integer.
         */
        fun toARGB32(): Int {
            return when (this) {
                TRANSPARENT -> 0x00000000
                BLACK -> -0x1000000
                BLUE -> -0xffff01
                GREEN -> -0xff0100
                YELLOW -> -0x100
                RED -> -0x10000
                WHITE -> -0x1
                else -> -0x1000000 or (180 shl 16) or (142 shl 8) or 92
            }
        }
    }

}