package com.github.giovanniandreuzza.mrrobot

import com.github.giovanniandreuzza.mrrobot.Mission.Companion.NUMBER_OF_BALLS
import com.github.giovanniandreuzza.mrrobot.enums.Color
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class FirstStep(
    private val field: Field,
    private val mrRobot: MrRobotCallback
) {

    private var holdingBall = false
    private var ballsInSafe = 0

    private var state = State.SEARCH
    private var action = Action.MOVING_ON_LINE

    private var hasToMoveBack = false
    private var isColorCaught = false

    fun execute() {
        val isBallCatchable = runBlocking { mrRobot.isBallCatchable() }
        val color = runBlocking { mrRobot.getColor() }

        val isMoving = mrRobot.isAlreadyMoving()

        if (!isMoving) {
            when (state) {
                State.SEARCH -> move(isBallCatchable)
                State.GO_TO_SAFE -> moveToSafe()
                State.GO_TO_LAST_POINT -> moveToLastPoint()
                State.GO_TO_ORIGIN -> moveToOrigin(color)
            }
        }

        if ((ballsInSafe == NUMBER_OF_BALLS || field.isFieldFinished()) && field.isInOrigin()) {
            Timber.d("Finish Step 1")
        } else {
            execute()
        }
    }

    private fun move(isBallCatchable: Boolean) {
        when {
            field.canMove() && action == Action.MOVING_ON_LINE && !isBallCatchable -> mrRobot.coverDistance(
                field.getDistanceToMove()
            )
            field.isFieldFinished() -> state = State.GO_TO_ORIGIN
            isBallCatchable -> {
                mrRobot.closeClaws()
                state = State.GO_TO_ORIGIN
                field.ballGrabbed()
                holdingBall = true
                isColorCaught = false
            }
            else -> changeLine()
        }
    }

    private fun changeLine() {
        when (action) {
            Action.MOVING_ON_LINE -> action = Action.ROTATING_FIRST
            Action.ROTATING_FIRST -> {
                mrRobot.rotateByAngle(field.getAngleToRotate())
                action = Action.CHANGING_LINE
            }
            Action.CHANGING_LINE -> {
                if (field.hasReachedNewLine()) {
                    action = if (field.hasToRotate()) {
                        Action.ROTATING_SECOND
                    } else {
                        Action.MOVING_ON_LINE
                    }
                }
                mrRobot.coverDistance(field.getDistanceToMove())
            }
            Action.ROTATING_SECOND -> {
                mrRobot.rotateByAngle(field.getAngleToRotate())
                action = Action.MOVING_ON_LINE
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
            ballsInSafe == NUMBER_OF_BALLS -> state = State.GO_TO_ORIGIN
            else -> state = State.GO_TO_LAST_POINT
        }
    }

    private fun moveToLastPoint() {
        when {
            field.canMoveToLastPoint() -> mrRobot.coverDistance(field.getDistanceToMove())
            !field.isOnLastPoint() -> mrRobot.rotateByAngle(field.getAngleToRotateOrigin())
            !field.inRightPosition() -> mrRobot.rotateByAngle(field.getAngleToRotateLastDirection())
            else -> state = State.SEARCH
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
        SEARCH,
        GO_TO_SAFE,
        GO_TO_LAST_POINT,
        GO_TO_ORIGIN
    }

    private enum class Action {
        MOVING_ON_LINE,
        ROTATING_FIRST,
        CHANGING_LINE,
        ROTATING_SECOND
    }

}