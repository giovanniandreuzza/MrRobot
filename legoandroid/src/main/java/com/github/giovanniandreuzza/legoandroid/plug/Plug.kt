package com.github.giovanniandreuzza.legoandroid.plug

import com.github.giovanniandreuzza.legoandroid.ev3.EV3

/**
 * Constructor.
 *
 * @param api  the [EV3] object.
 * @param port the port of type [Port].
 */
abstract class Plug<Port>(val api: EV3, val port: Port) {

    override fun toString(): String {
        return String.format("%s@%s", this.javaClass.simpleName, port)
    }

}