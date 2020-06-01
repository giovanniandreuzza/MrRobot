package com.github.giovanniandreuzza.legoandroid.plug.sensors

import com.github.giovanniandreuzza.legoandroid.comm.Const
import com.github.giovanniandreuzza.legoandroid.ev3.EV3
import com.github.giovanniandreuzza.legoandroid.ev3.EV3InputPort
import io.reactivex.Single

/**
 * Instances of this class allow operations on the touch (or pressure) sensor connected to GenEV3 via an input port.
 */
class TouchSensor(api: EV3, port: EV3InputPort) : AbstractSensor(api, port, Const.EV3_TOUCH) {

    /**
     * Get the pressed status of the touch sensor.
     *
     * @return true when the sensor is pressed; false otherwise.
     */
    suspend fun getPressed(): Boolean = getPercent1(Const.TOUCH_TOUCH) { it > 0 }

}