package com.github.giovanniandreuzza.mrrobot

import com.github.giovanniandreuzza.legoandroid.ev3.EV3
import com.github.giovanniandreuzza.legoandroid.ev3.EV3InputPort
import com.github.giovanniandreuzza.legoandroid.ev3.EV3OutputPort
import com.github.giovanniandreuzza.legoandroid.plug.motors.TachoMotor
import com.github.giovanniandreuzza.legoandroid.plug.sensors.LightSensor
import com.github.giovanniandreuzza.legoandroid.plug.sensors.UltrasonicSensor
import com.github.giovanniandreuzza.mrrobot.enums.Color
import kotlinx.coroutines.runBlocking
import kotlin.math.abs

class MrRobot(ev3: EV3) : MrRobotCallback {

    companion object {
        private const val deltaWheel = 2.75
        private const val velocity = 30
        private const val MAX_DISTANCE_TO_CATCH = 5.5f
        private const val CLAWS_DISTANCE = 50
    }

    private var engineAlreadyStarted = false

    private var leftWheel: TachoMotor = ev3.getTachoMotor(EV3OutputPort.A)
    private val rightWheel = ev3.getTachoMotor(EV3OutputPort.D)
    private val claw = ev3.getTachoMotor(EV3OutputPort.C)

    private val ultraSensor: UltrasonicSensor = ev3.getUltrasonicSensor(EV3InputPort._1)
    private val lightSensor: LightSensor = ev3.getLightSensor(EV3InputPort._2)

    private var isMoving = false

    override fun isAlreadyMoving(): Boolean {
        if (!isMoving) {
            return false
        }

        isMoving =
            runBlocking { leftWheel.getSpeed() != 0f || rightWheel.getSpeed() != 0f || claw.getSpeed() != 0f }
        return isMoving
    }

    override suspend fun isBallCatchable() = ultraSensor.getDistance() <= MAX_DISTANCE_TO_CATCH

    override suspend fun getColor() = when (lightSensor.getColor()) {
        LightSensor.Color.BLUE, LightSensor.Color.GREEN -> Color.BLUE
        LightSensor.Color.YELLOW, LightSensor.Color.BROWN -> Color.YELLOW
        else -> Color.RED
    }

    override fun coverDistance(distance: Int) {
        val time = calculateTimeToCoverDistance(distance, velocity)
        val vel = if (distance >= 0) velocity else -velocity
        isMoving = true
        leftWheel.setTimeSpeed(vel, 0, time, 0, true)
        rightWheel.setTimeSpeed(vel, 0, time, 0, true)
    }

    override fun rotateByAngle(angle: Int) {
        val vel = if (angle >= 0) velocity else -velocity
        var time = calculateTimeToCoverDistance(abs(angle), vel)
        isMoving = true

        if (angle == 8 || angle == -8) {
            time += 15
        }

        if (angle == 16) {
            time += 15
        }

        leftWheel.setTimeSpeed(vel, 0, time, 0, true)
        rightWheel.setTimeSpeed(-vel, 0, time, 0, true)
    }

    override fun closeClaws() {
        claw.setTimeSpeed(
            -velocity,
            0,
            calculateTimeToCoverDistance(CLAWS_DISTANCE, -velocity),
            0,
            true
        )
        isMoving = true
    }

    override fun openClaws() {
        claw.setTimeSpeed(
            velocity,
            0,
            calculateTimeToCoverDistance(CLAWS_DISTANCE, velocity),
            0,
            true
        )
        isMoving = true
    }

    override fun stopEngine() {
        if (engineAlreadyStarted) {
            engineAlreadyStarted = false
            leftWheel.stop()
            rightWheel.stop()
            claw.stop()
        }
    }

    private fun calculateTimeToCoverDistance(distance: Int, velocity: Int): Int {
        val velocityPerSecond = velocity * 10
        val radiant = (velocityPerSecond * Math.PI) / 180
        val time = (distance / (deltaWheel * radiant)) * 1000
        return time.toInt()
    }
}