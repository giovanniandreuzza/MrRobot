package com.github.giovanniandreuzza.legoandroid.plug.sensors

import com.github.giovanniandreuzza.legoandroid.comm.Const
import com.github.giovanniandreuzza.legoandroid.ev3.EV3
import com.github.giovanniandreuzza.legoandroid.ev3.EV3InputPort

/**
 * Instances of this class allow operations on the ultrasonic sensor connected to GenEV3 via an input port.
 */
class UltrasonicSensor(api: EV3, port: EV3InputPort) :
    AbstractSensor(api, port, Const.EV3_ULTRASONIC) {

    /**
     * Get the distance status of the touch sensor.
     *
     * @return true when the sensor is pressed; false otherwise.
     */
    suspend fun getDistance(): Float = getSi1(Const.US_CM)

}