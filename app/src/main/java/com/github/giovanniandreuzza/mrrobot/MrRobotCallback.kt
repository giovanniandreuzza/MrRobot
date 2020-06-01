package com.github.giovanniandreuzza.mrrobot

import com.github.giovanniandreuzza.mrrobot.enums.Color

interface MrRobotCallback {

    fun isAlreadyMoving(): Boolean

    suspend fun isBallCatchable(): Boolean

    suspend fun getColor(): Color

    fun coverDistance(distance: Int)

    fun rotateByAngle(angle: Int)

    fun closeClaws()

    fun openClaws()

    fun stopEngine()
}