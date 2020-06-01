package com.github.giovanniandreuzza.legoandroid.plug.sensors

import com.github.giovanniandreuzza.legoandroid.comm.Const
import com.github.giovanniandreuzza.legoandroid.ev3.EV3
import com.github.giovanniandreuzza.legoandroid.ev3.EV3InputPort
import io.reactivex.Single
import java.io.IOException
import java.util.concurrent.Future

/**
 * Instances of this class allow operations on the gyroscope sensor (accelerometer) connected to GenEV3 via an input port.
 */
class GyroSensor(api: EV3, port: EV3InputPort) : AbstractSensor(api, port, Const.EV3_GYRO) {

    /**
     * Get the angle from the sensor in degrees.
     *
     * @return a [Future] object hosting the 32-bit float within the range [ -18000 - 18000 ] returned by GenEV3.
     * @throws IOException thrown when communication errors occur.
     */
    suspend fun getAngle(): Float = getSi1(Const.GYRO_ANGLE)

    /**
     * Get the rate (or rotational speed) from the sensor in degrees per second.
     *
     * @return a [Future] object hosting the 32-bit float returned by GenEV3.
     * @throws IOException thrown when communication errors occur.
     */
    suspend fun getRate(): Float = getSi1(Const.GYRO_RATE)

}