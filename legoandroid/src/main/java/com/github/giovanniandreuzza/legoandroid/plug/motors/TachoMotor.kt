package com.github.giovanniandreuzza.legoandroid.plug.motors

import android.annotation.SuppressLint
import android.util.Log
import com.github.giovanniandreuzza.legoandroid.comm.Bytecode
import com.github.giovanniandreuzza.legoandroid.comm.Const
import com.github.giovanniandreuzza.legoandroid.ev3.EV3
import com.github.giovanniandreuzza.legoandroid.ev3.EV3OutputPort
import com.github.giovanniandreuzza.legoandroid.plug.Plug
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class TachoMotor(api: EV3, port: EV3OutputPort) : Plug<EV3OutputPort>(api, port) {

    companion object {
        private const val TAG: String = "TachoMotor"
    }

    /**
     * Get the current position of the motor in tacho ticks.
     *
     * @return the current position of the motor in tacho ticks.
     */
    suspend fun getPosition(): Float = getTachoMotorValue(Const.L_MOTOR_DEGREE)

    /**
     * Get the current speed of the motor.
     * Returns the current motor speed in tacho counts per second.
     * Note, this is not necessarily degrees (although it is for LEGO motors).
     *
     * @return the current motor speed in tacho counts per second.
     */
    suspend fun getSpeed(): Float = getTachoMotorValue(Const.L_MOTOR_SPEED)


    /**
     * Clear the tacho counter of the motor.
     */
    fun clearCount() {
        Log.d(TAG, "motor clear count")
        api.sendNoReply(Bytecode().apply {
            addOpCode(Const.OUTPUT_CLR_COUNT)
            addParameter(Const.LAYER_MASTER)
            addParameter(port.toBitmask())
        })
    }


    /**
     * Tests whether the motor is busy or not.
     */
    fun isBusy() = runBlocking {
        Log.d(TAG, "motor is busy")
        api.send(1, Bytecode().apply {
            addOpCode(Const.OUTPUT_TEST)
            addParameter(Const.LAYER_MASTER)
            addParameter(port.toBitmask())
        }).data[0].toInt() != 0
    }

    /**
     * Wait until the motor is ready.
     * This method blocks the caller thread.
     */
    @SuppressLint("CheckResult")
    fun waitUntilReady() {
        if (isBusy()) {
            waitUntilReady()
        } else {
            waitCompletion()
        }
    }

    /**
     * Make the GenEV3 wait until the current command has been completed.
     * This method is NOT blocking the caller thread.
     */
    fun waitCompletion() {
        Log.d(TAG, "motor wait until ready")

        api.sendNoReply(Bytecode().apply {
            addOpCode(Const.OUTPUT_READY)
            addParameter(Const.LAYER_MASTER)
            addParameter(port.toBitmask())
        })
    }

    /**
     * Reset the position counter of the motor.
     */
    fun resetPosition() {
        Log.d(TAG, "motor reset position")

        api.sendNoReply(Bytecode().apply {
            addOpCode(Const.OUTPUT_RESET)
            addParameter(Const.LAYER_MASTER)
            addParameter(port.toBitmask())
        })
    }

    /**
     * Set the speed percentage of the motor.
     * This mode automatically enables speed control, which means the system will automatically adjust the power to keep the specified speed.
     *
     * @param speed the speed percentage in the range [ -100 - 100 ].
     */
    fun setSpeed(speed: Int) {
        Log.d(TAG, String.format("motor speed set: %d", speed))

        api.sendNoReply(Bytecode().apply {
            addOpCode(Const.OUTPUT_SPEED)
            addParameter(Const.LAYER_MASTER)
            addParameter(port.toBitmask())
            addParameter(speed.toByte())
        })
    }

    /**
     * Set the power percentage of the motor.
     *
     * @param power the power percentage in the range [ -100 - 100 ].
     */
    fun setPower(power: Int) {
        Log.d(TAG, String.format("motor power set: %d", power))

        api.sendNoReply(Bytecode().apply {
            addOpCode(Const.OUTPUT_POWER)
            addParameter(Const.LAYER_MASTER)
            addParameter(port.toBitmask())
            addParameter(power.toByte())
        })
    }

    /**
     * Start the motor.
     */
    fun start() {
        Log.d(TAG, "motor started")

        api.sendNoReply(Bytecode().apply {
            addOpCode(Const.OUTPUT_START)
            addParameter(Const.LAYER_MASTER)
            addParameter(port.toBitmask())
        })
    }

    /**
     * Brake the motor.
     */
    fun brake() {
        Log.d(TAG, "motor brake")

        api.sendNoReply(Bytecode().apply {
            addOpCode(Const.OUTPUT_STOP)
            addParameter(Const.LAYER_MASTER)
            addParameter(port.toBitmask())
            addParameter(Const.BRAKE)
        })
    }

    /**
     * Stop the motor.
     */
    fun stop() {
        Log.d(TAG, "motor stop")

        api.sendNoReply(Bytecode().apply {
            addOpCode(Const.OUTPUT_STOP)
            addParameter(Const.LAYER_MASTER)
            addParameter(port.toBitmask())
            addParameter(Const.COAST)
        })
    }

    /**
     * Type of motor enumeration type.
     */
    enum class Type {
        /**
         * Medium motor: the small ones, e.g. GenEV3 Medium Servo motor
         */
        MEDIUM,

        /**
         * Large motor: the standard ones, e.g. GenEV3 Large Servo Motor.
         */
        LARGE;

        /**
         * Convert to a byte for use with low level command creation.
         *
         * @return the type as a byte-sized constant.
         */
        fun toByte(): Byte = when (this) {
            MEDIUM -> Const.M_MOTOR
            LARGE -> Const.L_MOTOR
        }
    }

    /**
     * Set the motor type.
     * This is useful for switching mode between different motor types.
     *
     * @param mt the type of the motor.
     * @see Type
     */
    fun setType(mt: Type) {
        Log.d(TAG, String.format("motor type set: %s", mt))

        api.sendNoReply(Bytecode().apply {
            addOpCode(Const.OUTPUT_SET_TYPE)
            addParameter(Const.LAYER_MASTER)
            addParameter(port.toByte())
            addParameter(mt.toByte())
        })
    }

    /**
     * Polarity enumeration type.
     */
    enum class Polarity {
        /**
         * Motor will run backward.
         */
        BACKWARDS,

        /**
         * Motor will run opposite direction.
         */
        OPPOSITE,

        /**
         * Motor will run forward.
         */
        FORWARD;

        /**
         * Convert to a byte for use with low level command creation.
         *
         * @return the type as a byte-sized constant.
         */
        fun toByte(): Byte = when (this) {
            BACKWARDS -> -1
            OPPOSITE -> 0
            FORWARD -> 1
        }
    }

    /**
     * Set the polarity of the tacho motor.
     *
     * @param pol the polarity value.
     * @see Polarity
     */
    fun setPolarity(pol: Polarity) {
        Log.d(TAG, String.format("motor polarity set: %s", pol))

        api.sendNoReply(Bytecode().apply {
            addOpCode(Const.OUTPUT_POLARITY)
            addParameter(Const.LAYER_MASTER)
            addParameter(port.toBitmask())
            addParameter(pol.toByte())
        })
    }


    /**
     * This method enables specifying a full motor power cycle in tacho counts.
     * Step1 specifyes the power ramp up periode in tacho count, Step2 specifyes the constant power period in tacho counts, Step 3 specifyes the power down period in tacho counts.
     *
     * @param power the power level within range [ -100 - 100 ].
     * @param step1 tacho pulses during ramp up.
     * @param step2 tacho pulses during continues run.
     * @param step3 tacho pulses during ramp down.
     * @param brake break level [false: Float, true: Break].
     */
    fun setStepPower(power: Int, step1: Int, step2: Int, step3: Int, brake: Boolean) {
        Log.d(
            TAG,
            "motor step power: power=$power, step1=$step1, step2=$step2, step3=$step3, brake=$brake"
        )
        api.sendNoReply(Bytecode().apply {
            addOpCode(Const.OUTPUT_STEP_POWER)
            addParameter(Const.LAYER_MASTER)
            addParameter(port.toBitmask())
            addParameter(power.toByte())
            addParameter(step1)
            addParameter(step2)
            addParameter(step3)
            addParameter(if (brake) Const.BRAKE else Const.COAST)
        })
    }

    /**
     * This method enables specifying a full motor power cycle in time.
     * Step1 specifyes the power ramp up periode in milliseconds, Step2 specifyes the constant power period in milliseconds, Step 3 specifyes the power down period in milliseconds.
     *
     * @param power the power level within range [ -100 - 100 ].
     * @param step1 tacho pulses during ramp up.
     * @param step2 tacho pulses during continues run.
     * @param step3 tacho pulses during ramp down.
     * @param brake break level [false: Float, true: Break].
     */
    fun setTimePower(power: Int, step1: Int, step2: Int, step3: Int, brake: Boolean) {
        Log.d(
            TAG,
            "motor time power: power=$power, step1=$step1, step2=$step2, step3=$step3, brake=$brake"
        )
        api.sendNoReply(Bytecode().apply {
            addOpCode(Const.OUTPUT_TIME_POWER)
            addParameter(Const.LAYER_MASTER)
            addParameter(port.toBitmask())
            addParameter(power.toByte())
            addParameter(step1)
            addParameter(step2)
            addParameter(step3)
            addParameter(if (brake) Const.BRAKE else Const.COAST)
        })
    }


    private suspend fun getTachoMotorValue(motorValue: Byte) = api.getSiValue(
        port.toByteAsRead(),
        Const.L_MOTOR,
        motorValue,
        1
    )[0]


    /**
     * This method enables specifying a full motor power cycle in tacho counts.
     * The system will automatically adjust the power level to the motor to keep the specified output speed.
     * Step1 specifyes the power ramp up periode in tacho count, Step2 specifyes the constant power period in tacho counts, Step 3 specifyes the power down period in tacho counts.
     *
     * @param speed power level [-100 – 100].
     * @param step1 tacho pulses during ramp up.
     * @param step2 tacho pulses during continues run.
     * @param step3 tacho pulses during ramp down.
     * @param brake break level [false: Float, true: Break].
     */
    fun setStepSpeed(speed: Int, step1: Int, step2: Int, step3: Int, brake: Boolean) {
        Log.d(
            TAG,
            "motor step speed: speed=$speed, step1=$step1, step2=$step2, step3=$step3, brake=$brake"
        )
        api.sendNoReply(Bytecode().apply {
            addOpCode(Const.OUTPUT_STEP_SPEED)
            addParameter(Const.LAYER_MASTER)
            addParameter(port.toBitmask())
            addParameter(speed.toByte())
            addParameter(step1)
            addParameter(step2)
            addParameter(step3)
            addParameter(if (brake) Const.BRAKE else Const.COAST)
        })
    }

    /**
     * This method enables specifying a full motor power cycle in time.
     * The system will automatically adjust the power level to the motor to keep the specified output speed.
     * Step1 specifyes the power ramp up periode in milliseconds, Step2 specifyes the constant power period in milliseconds, Step 3 specifyes the power down period in milliseconds.
     *
     * @param speed power level [-100 – 100].
     * @param step1 tacho pulses during ramp up.
     * @param step2 tacho pulses during continues run.
     * @param step3 tacho pulses during ramp down.
     * @param brake break level [false: Float, true: Break].
     */
    fun setTimeSpeed(speed: Int, step1: Int, step2: Int, step3: Int, brake: Boolean) {
        Log.d(
            TAG,
            "motor step speed: speed=$speed, step1=$step1, step2=$step2, step3=$step3, brake=$brake"
        )
        api.sendNoReply(Bytecode().apply {
            addOpCode(Const.OUTPUT_TIME_SPEED)
            addParameter(Const.LAYER_MASTER)
            addParameter(port.toBitmask())
            addParameter(speed.toByte())
            addParameter(step1)
            addParameter(step2)
            addParameter(step3)
            addParameter(if (brake) Const.BRAKE else Const.COAST)
        })
    }


    /**
     * This method enables synchonizing two motors.
     * Synchonization should be used when motors should run as synchrone as possible, for example to archieve a model driving straight.
     * Duration is specified in tacho counts.
     * The turn ratio behaves as follows:
     * 0 : Motor will run with same power.
     * 100 : One motor will run with specified power while the other will be close to zero.
     * 200: One motor will run with specified power forward while the other will run in the opposite direction at the same power level.
     *
     * @param power     power level [ -100 - 100 ].
     * @param turnRatio turn ratio [ -200 - 200 ].
     * @param step      tacho pulses (0 = infinite).
     * @param brake     break level [false: Float, true: Break].
     */
    fun setStepSync(power: Int, turnRatio: Int, step: Int, brake: Boolean) {
        Log.d(
            TAG,
            "motor step sync: power=$power, turn=$turnRatio, step=$step, brake=$brake"
        )
        api.sendNoReply(Bytecode().apply {
            addOpCode(Const.OUTPUT_STEP_SYNC)
            addParameter(Const.LAYER_MASTER)
            addParameter(port.toBitmask())
            addParameter(power.toByte())
            addParameter(turnRatio.toShort())
            addParameter(step)
            addParameter(if (brake) Const.BRAKE else Const.COAST)
        })
    }

    /**
     * This method enables synchonizing two motors.
     * Synchonization should be used when motors should run as synchrone as possible, for example to archieve a model driving straight.
     * Duration is specified in time.
     * The turn ratio behaves as follows:
     * 0 : Motor will run with same power.
     * 100 : One motor will run with specified power while the other will be close to zero.
     * 200: One motor will run with specified power forward while the other will run in the opposite direction at the same power level.
     *
     * @param power     power level [ -100 - 100 ].
     * @param turnRatio turn ratio [ -200 - 200 ].
     * @param time      time in milliseconds (0 = infinite).
     * @param brake     break level [false: Float, true: Break].
     */
    fun setTimeSync(power: Int, turnRatio: Int, time: Int, brake: Boolean) {
        Log.d(
            TAG,
            "motor step sync: power=$power, turn=$turnRatio, time=$time, brake=$brake"
        )
        api.sendNoReply(Bytecode().apply {
            addOpCode(Const.OUTPUT_TIME_SYNC)
            addParameter(Const.LAYER_MASTER)
            addParameter(port.toBitmask())
            addParameter(power.toByte())
            addParameter(turnRatio.toShort())
            addParameter(time)
            addParameter(if (brake) Const.BRAKE else Const.COAST)
        })
    }

}