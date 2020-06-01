package com.github.giovanniandreuzza.mrrobot

class Mission(
    private val mrRobot: MrRobot,
    val field: Field,
    private val step: Step,
    ballList: List<Point<Int, Int>>? = null
) {

    companion object {
        const val NUMBER_OF_BALLS = 3
    }

    private var firstStep = FirstStep(field, mrRobot)
    private var secondStep = SecondStep(field, mrRobot, ballList.orEmpty())

    fun startMission() {
        //mrRobot.rotateByAngle(8, 20)

        when (step) {
            Step.STEP_1 -> {
                firstStep.execute()
            }
            Step.STEP_2 -> {
                secondStep.execute()
            }
        }
    }

    fun stopMission() {
        mrRobot.stopEngine()
    }
}