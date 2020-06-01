package com.github.giovanniandreuzza.legoandroid.comm

/**
 * Constants for low level encoding of commands and replies.
 *
 * @see <a href="http://google.com</a>https://le-www-live-s.legocdn.com/sc/media/files/ev3-developer-kit/lego%20mindstorms%20ev3%20firmware%20developer%20kit-7be073548547d99f7df59ddfd57c0088.pdf?la=en-us">GenEV3 Developer Kit Documentation</a>
 */
object Const {

    // Command Types
    const val DIRECT_COMMAND_REPLY = 0x00.toByte()
    const val DIRECT_COMMAND_NOREPLY = 0x80.toByte()

    const val DIRECT_COMMAND_SUCCESS = 0x02.toByte()
    const val DIRECT_COMMAND_FAIL = 0x04.toByte()

    // Direct Commands - SOUND
    const val SOUND_CONTROL = 0x94.toByte()

    // Sub codes for SOUND_CONTROL
    const val SOUND_BREAK = 0x00.toByte()
    const val SOUND_TONE = 0x01.toByte()
    const val SOUND_PLAY = 0x02.toByte()
    const val SOUND_REPEAT = 0x03.toByte()
    const val SOUND_SERVICE = 0x04.toByte()

    // Direct Commands - INPUT
    const val INPUT_SAMPLE = 0x97.toByte()
    const val INPUT_DEVICE_LIST = 0x98.toByte()
    const val INPUT_DEVICE = 0x99.toByte()
    const val INPUT_READ = 0x9A.toByte()
    const val INPUT_TEST = 0x9B.toByte()
    const val INPUT_READY = 0x9C.toByte()
    const val INPUT_READSI = 0x9D.toByte()
    const val INPUT_READEXT = 0x9E.toByte()
    const val INPUT_WRITE = 0x9F.toByte()

    // Direct Commands - OUTPUT
    const val OUTPUT_GET_TYPE = 0xA0.toByte()
    const val OUTPUT_SET_TYPE = 0xA1.toByte()
    const val OUTPUT_RESET = 0xA2.toByte()
    const val OUTPUT_STOP = 0xA3.toByte()
    const val OUTPUT_POWER = 0xA4.toByte()
    const val OUTPUT_SPEED = 0xA5.toByte()
    const val OUTPUT_START = 0xA6.toByte()
    const val OUTPUT_POLARITY = 0xA7.toByte()
    const val OUTPUT_READ = 0xA8.toByte()
    const val OUTPUT_TEST = 0xA9.toByte()
    const val OUTPUT_READY = 0xAA.toByte()
    const val OUTPUT_POSITION = 0xAB.toByte()
    const val OUTPUT_STEP_POWER = 0xAC.toByte()
    const val OUTPUT_TIME_POWER = 0xAD.toByte()
    const val OUTPUT_STEP_SPEED = 0xAE.toByte()
    const val OUTPUT_TIME_SPEED = 0xAF.toByte()
    const val OUTPUT_STEP_SYNC = 0xB0.toByte()
    const val OUTPUT_TIME_SYNC = 0xB1.toByte()
    const val OUTPUT_CLR_COUNT = 0xB2.toByte()
    const val OUTPUT_GET_COUNT = 0xB3.toByte()
    const val OUTPUT_PRG_ST = 0xB4.toByte()

    // Sub Commands for INPUT_DEVICE
    const val GET_FORMAT = 0x02.toByte()
    const val CAL_MINMAX = 0x03.toByte()
    const val CAL_DEFAULT = 0x04.toByte()
    const val GET_TYPEMODE = 0x05.toByte()
    const val GET_SYMBOL = 0x06.toByte()
    const val CAL_MIN = 0x07.toByte()
    const val CAL_MAX = 0x08.toByte()
    const val SETUP = 0x09.toByte()
    const val CLR_ALL = 0x0A.toByte()
    const val GET_RAW = 0x0B.toByte()
    const val GET_CONNECTION = 0x0C.toByte()
    const val STOP_ALL = 0x0D.toByte()
    const val GET_NAME = 0x15.toByte()
    const val GET_MODENAME = 0x16.toByte()
    const val SET_RAW = 0x17.toByte()
    const val GET_FIGURES = 0x18.toByte()
    const val GET_CHANGES = 0x19.toByte()
    const val CLR_CHANGES = 0x1A.toByte()
    const val READY_PCT = 0x1B.toByte()
    const val READY_RAW = 0x1C.toByte()
    const val READY_SI = 0x1D.toByte()
    const val GET_MINMAX = 0x1E.toByte()
    const val GET_BUMPS = 0x1F.toByte()

    // All motors
    const val ALL_MOTORS = 0x0f.toByte()

    // Layers
    const val LAYER_MASTER = 0x00.toByte()
    const val LAYER_SLAVE = 0x01.toByte()

    // Motor brakes
    const val COAST = 0x00.toByte()
    const val BRAKE = 0x01.toByte()

    // Input Device Modes
// Sensor Types
    const val TYPE_DEFAULT = 0x00.toByte()
    const val NXT_TOUCH = 0x01.toByte()
    const val NXT_LIGHT = 0x02.toByte()
    const val NXT_SOUND = 0x03.toByte()
    const val NXT_COLOR = 0x04.toByte()
    const val NXT_ULTRASONIC = 0x05.toByte()
    const val L_MOTOR = 0x07.toByte()
    const val M_MOTOR = 0x08.toByte()
    const val EV3_TOUCH = 0x10.toByte()
    const val EV3_COLOR = 0x1D.toByte()
    const val EV3_ULTRASONIC = 0x1E.toByte()
    const val EV3_GYRO = 0x20.toByte()
    const val EV3_IR = 0x21.toByte()

    // Sensor Modes
// constants for all the sensors
    const val NOT_INITIALIZED = 0xff.toByte()
    const val MODE_DEFAULT = 0x00.toByte()
    // constants for LightSensor
    const val LIGHT_REFLECT = 0x00.toByte()
    const val LIGHT_AMBIENT = 0x01.toByte()
    // constants for SoundSensor
    const val SOUND_DB = 0x00.toByte()
    const val SOUND_DBA = 0x01.toByte()
    // constants for TouchSensor
    const val TOUCH_TOUCH = 0x00.toByte()
    const val TOUCH_BUMPS = 0x01.toByte()
    // constants for ColorSensor
    const val COL_REFLECT = 0x00.toByte()
    const val COL_AMBIENT = 0x01.toByte()
    const val COL_COLOR = 0x02.toByte()
    const val COL_RGB = 0x04.toByte()
    // constants for UltrasonicSensor
    const val US_CM = 0x00.toByte()
    const val US_INCH = 0x01.toByte()
    const val US_LISTEN = 0x02.toByte()
    // constants for GyroSensor
    const val GYRO_ANGLE = 0x00.toByte()
    const val GYRO_RATE = 0x01.toByte()
    // constants for IRSensor (InfraRed)
    const val IR_PROX = 0x00.toByte()
    const val IR_SEEK = 0x01.toByte()
    const val IR_REMOTE = 0x02.toByte()

    // Motor Modes
// constants for L-Motor
    const val L_MOTOR_DEGREE = 0x00.toByte()
    const val L_MOTOR_ROTATE = 0x01.toByte()
    const val L_MOTOR_SPEED = 0x02.toByte()
    // constants for M-Motor
    const val M_MOTOR_DEGREE = 0x00.toByte()
    const val M_MOTOR_ROTATE = 0x01.toByte()
    const val M_MOTOR_SPEED = 0x02.toByte()

}