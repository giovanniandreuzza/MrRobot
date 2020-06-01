package com.github.giovanniandreuzza.legoandroid.ev3

import kotlin.experimental.or

/**
 * This enum type represents the 4 physical output ports on the EV3 brick.
 */
enum class EV3OutputPort {

    /**
     * Output port A
     */
    A,
    /**
     * Output port B
     */
    B,
    /**
     * Output port C
     */
    C,
    /**
     * Output port D
     */
    D;

    /**
     * Encode the output port as a bit mask for certain GenEV3 direct commands that require the bitmask format as parameter.
     *
     * @return a byte with the bit mask.
     * @see <a href="http://google.com</a>https://le-www-live-s.legocdn.com/sc/media/files/ev3-developer-kit/lego%20mindstorms%20ev3%20firmware%20developer%20kit-7be073548547d99f7df59ddfd57c0088.pdf?la=en-us">GenEV3 Developer Kit Documentation</a>
     */
    fun toBitmask(): Byte = (1 shl toByte().toInt()).toByte()

    /**
     * Encode the output port into a byte for use with [Api.getPercentValue] and [Api.getSiValue].
     * Using output ports for receive operations is possible, though a special encoding is needed according to the GenEV3 Developer Kit Documentation - this is provided by this method.
     *
     * @return a byte according to the encoding defined by the GenEV3 Developer Kit Documentation.
     * @see [GenEV3 Developer Kit Documentation](http://google.com</a>https://le-www-live-s.legocdn.com/sc/media/files/ev3-developer-kit/lego%20mindstorms%20ev3%20firmware%20developer%20kit-7be073548547d99f7df59ddfd57c0088.pdf?la=en-us)
     */
    fun toByteAsRead(): Byte = toByte() or 0x10

    /**
     * Encode the output port into a byte for use with [Api.getPercentValue] and [Api.getSiValue].
     *
     * @return a byte according to the encoding defined by the GenEV3 Developer Kit Documentation.
     * @see [GenEV3 Developer Kit Documentation](http://google.com</a>https://le-www-live-s.legocdn.com/sc/media/files/ev3-developer-kit/lego%20mindstorms%20ev3%20firmware%20developer%20kit-7be073548547d99f7df59ddfd57c0088.pdf?la=en-us)
     */
    fun toByte(): Byte = when (this) {
        A -> 0
        B -> 1
        C -> 2
        D -> 3
    }

    override fun toString(): String = String.format("Out/%s", super.toString())

}