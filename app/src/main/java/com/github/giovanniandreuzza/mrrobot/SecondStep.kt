package com.github.giovanniandreuzza.mrrobot

import com.github.giovanniandreuzza.mrrobot.enums.Color
import kotlinx.coroutines.runBlocking
import timber.log.Timber


class SecondStep(
    private val field: Field,
    private val mrRobot: MrRobotCallback,
    private val ballList: List<Point<Int, Int>>
) {

    private var holdingBall = false
    private var ballsInSafe = 0

    private var state = State.GO_TO_BALL

    private var hasToMoveBack = false
    private var isColorCaught = false

    fun execute() {
        val isBallCatchable = runBlocking { mrRobot.isBallCatchable() }
        val color = runBlocking { mrRobot.getColor() }

        val isMoving = mrRobot.isAlreadyMoving()

        if (!isMoving) {
            when (state) {
                State.GO_TO_BALL -> moveToBall()
                State.GO_TO_SAFE -> moveToSafe()
                State.GO_TO_ORIGIN -> moveToOrigin(color)
            }
        }

        if ((ballsInSafe == Mission.NUMBER_OF_BALLS || field.isFieldFinished()) && field.isInOrigin()) {
            Timber.d("Finish Step 2")
        } else {
            execute()
        }
    }

    private fun moveToBall() {
        when {
            field.canMoveToBall(ballList[ballsInSafe]) -> mrRobot.coverDistance(field.getDistanceToMove())
            !field.isAtBall(ballList[ballsInSafe]) -> mrRobot.rotateByAngle(field.getAngleToRotateBall())
            else -> {
                mrRobot.closeClaws()
                state = State.GO_TO_ORIGIN
                field.ballGrabbed()
                holdingBall = true
                isColorCaught = false
            }
        }
    }

    private fun moveToSafe() {
        when {
            field.canMoveToSafe() && holdingBall -> mrRobot.coverDistance(field.getDistanceToMove())
            !field.isInSafe() && holdingBall -> {
                var angle = field.getAngleToRotateSafe()

                angle = when (angle) {
                    8 -> 9
                    -8 -> -9
                    else -> 19
                }

                mrRobot.rotateByAngle(angle)
            }
            field.isInSafe() && holdingBall -> {
                mrRobot.openClaws()
                field.ballReleased()
                ballsInSafe++
                holdingBall = false
                hasToMoveBack = true
            }
            !holdingBall && hasToMoveBack -> {
                hasToMoveBack = false
                mrRobot.coverDistance(field.moveBack())
            }
            ballsInSafe == Mission.NUMBER_OF_BALLS -> state = State.GO_TO_ORIGIN
            else -> state = State.GO_TO_BALL
        }
    }

    private fun moveToOrigin(color: Color) {
        if (!isColorCaught) {
            isColorCaught = true
            field.setColorOfHoldingBall(color)
        }

        when {
            field.canMoveToOrigin() -> mrRobot.coverDistance(field.getDistanceToMove())
            !field.isInOrigin() -> {
                var angle = field.getAngleToRotateOrigin()
                if (holdingBall) {
                    if (angle == 8) {
                        angle = 9
                    }

                    if (angle == -8) {
                        angle == -9
                    }

                    if (angle == 16) {
                        angle = 19
                    }
                }
                mrRobot.rotateByAngle(angle)
            }
            holdingBall -> state = State.GO_TO_SAFE
            else -> mrRobot.stopEngine()
        }
    }

    private enum class State {
        GO_TO_BALL,
        GO_TO_SAFE,
        GO_TO_ORIGIN
    }

}
