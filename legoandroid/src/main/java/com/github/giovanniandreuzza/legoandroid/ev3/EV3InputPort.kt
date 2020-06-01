package com.github.giovanniandreuzza.legoandroid.ev3

/**
 * This enum type represents the 4 physical input ports on the EV3 brick.
 */
enum class EV3InputPort {
    /**
     * Input port 1
     */
    _1,
    /**
     * Input port 2
     */
    _2,
    /**
     * Input port 3
     */
    _3,
    /**
     * Input port 4
     */
    _4;

    /**
     * Encode the input port into a byte for use with {@link Api#getPercentValue(byte, int, int, int)} and {@link Api#getSiValue(byte, int, int, int)}.
     *
     * @return a byte according to the encoding defined by the GenEV3 Developer Kit Documentation.
     * @see <a href="https://le-www-live-s.legocdn.com/sc/media/files/ev3-developer-kit/lego%20mindstorms%20ev3%20firmware%20developer%20kit-7be073548547d99f7df59ddfd57c0088.pdf?la=en-us">LEGO Mindstorms GenEV3 Firmware Developer Kit</a>
     */
    fun toByte(): Byte = when (this) {
        _1 -> 0
        _2 -> 1
        _3 -> 2
        _4 -> 3
    }

    override fun toString(): String = String.format("In/%d", toByte())

}