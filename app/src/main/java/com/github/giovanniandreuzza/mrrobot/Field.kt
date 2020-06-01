package com.github.giovanniandreuzza.mrrobot

import android.util.Log
import com.github.giovanniandreuzza.mrrobot.enums.Color
import timber.log.Timber

class Field(private val m: Int, n: Int, private val cellLength: Double) {

    private val ROWS = n + 2
    private val COLUMNS = m + 2

    private val TOP_LEFT = Point(1, 1)
    private val TOP_RIGHT = Point(COLUMNS - 2, 1)
    private val BOTTOM_LEFT = Point(1, ROWS - 2)
    private val BOTTOM_RIGHT = Point(COLUMNS - 2, ROWS - 2)

    private lateinit var SAFE_1: Point<Int, Int>
    private lateinit var SAFE_2: Point<Int, Int>
    private lateinit var SAFE_3: Point<Int, Int>

    private lateinit var originPosition: Point<Int, Int>
    private lateinit var currentPosition: Point<Int, Int>
    private var lastPoint: Point<Int, Int>? = null
    private lateinit var direction: Direction
    private lateinit var lastDirection: Direction

    private val ballMap = mutableMapOf<Point<Int, Int>, Color>()

    private var fieldMatrix = Array(ROWS) {
        Array(COLUMNS) {
            CellType.HIDDEN
        }
    }

    init {
        for (i in 0 until ROWS) {
            setVal(i, 0, CellType.OUT_OF_BOUNDS)
            setVal(i, COLUMNS - 1, CellType.OUT_OF_BOUNDS)
        }

        for (j in 1 until COLUMNS - 1) {
            setVal(0, j, CellType.OUT_OF_BOUNDS)
            setVal(ROWS - 1, j, CellType.OUT_OF_BOUNDS)
        }
    }

    fun startingPoint(origin: Point<Int, Int>) {
        originPosition = Point(origin.x, origin.y)
        currentPosition = origin
        setVal(origin.x, origin.y, CellType.CURRENT_POSITION)

        when {
            //LEFT-UPPER CORNER
            origin == TOP_LEFT -> {
                setVal(1, 0, CellType.SAFE_AVAILABLE)
                setVal(0, 0, CellType.SAFE_AVAILABLE)
                setVal(0, 1, CellType.SAFE_AVAILABLE)
            }
            //LEFT-BOTTOM CORNER
            origin == BOTTOM_LEFT -> {
                setVal(1, ROWS - 1, CellType.SAFE_AVAILABLE)
                setVal(0, ROWS - 1, CellType.SAFE_AVAILABLE)
                setVal(0, ROWS - 2, CellType.SAFE_AVAILABLE)
            }
            //RIGHT-UPPER CORNER
            origin == TOP_RIGHT -> {
                setVal(COLUMNS - 2, 0, CellType.SAFE_AVAILABLE)
                setVal(COLUMNS - 1, 0, CellType.SAFE_AVAILABLE)
                setVal(COLUMNS - 1, 1, CellType.SAFE_AVAILABLE)
            }
            //RIGHT-BOTTOM CORNER
            origin == BOTTOM_RIGHT -> {
                setVal(COLUMNS - 2, ROWS - 1, CellType.SAFE_AVAILABLE)
                setVal(COLUMNS - 1, ROWS - 1, CellType.SAFE_AVAILABLE)
                setVal(COLUMNS - 1, ROWS - 2, CellType.SAFE_AVAILABLE)
            }
            // LEFT SIDE
            origin.x == 1 -> {
                setVal(0, origin.y - 1, CellType.SAFE_AVAILABLE)
                setVal(0, origin.y, CellType.SAFE_AVAILABLE)
                setVal(0, origin.y + 1, CellType.SAFE_AVAILABLE)
            }
            // UPPER SIDE
            origin.y == 1 -> {
                setVal(origin.x - 1, 0, CellType.SAFE_AVAILABLE)
                setVal(origin.x, 0, CellType.SAFE_AVAILABLE)
                setVal(origin.x + 1, 0, CellType.SAFE_AVAILABLE)
            }
            // RIGHT SIDE
            origin.x == m -> {
                setVal(COLUMNS - 1, origin.y - 1, CellType.SAFE_AVAILABLE)
                setVal(COLUMNS - 1, origin.y, CellType.SAFE_AVAILABLE)
                setVal(COLUMNS - 1, origin.y + 1, CellType.SAFE_AVAILABLE)
            }
            // BOTTOM SIDE
            else -> {
                setVal(origin.x - 1, ROWS - 1, CellType.SAFE_AVAILABLE)
                setVal(origin.x, ROWS - 1, CellType.SAFE_AVAILABLE)
                setVal(origin.x + 1, ROWS - 1, CellType.SAFE_AVAILABLE)
            }
        }

        when {
            origin.y == 1 -> direction = Direction.TO_BOTTOM
            origin.y == ROWS - 1 -> direction = Direction.TO_UP
            origin.x == 1 -> direction = Direction.TO_RIGHT
            origin.x == COLUMNS - 1 -> direction = Direction.TO_LEFT
        }

        printMatrix()
    }

    fun canMove() = when (direction) {
        Direction.TO_UP -> {
            val nextCellType = getVal(currentPosition.x, currentPosition.y - 1)
            nextCellType != CellType.OUT_OF_BOUNDS && nextCellType != CellType.SAFE_AVAILABLE && nextCellType != CellType.SAFE_UNAVAILABLE
        }
        Direction.TO_LEFT -> {
            val nextCellType = getVal(currentPosition.x - 1, currentPosition.y)
            nextCellType != CellType.OUT_OF_BOUNDS && nextCellType != CellType.SAFE_AVAILABLE && nextCellType != CellType.SAFE_UNAVAILABLE
        }
        Direction.TO_RIGHT -> {
            val nextCellType = getVal(currentPosition.x + 1, currentPosition.y)
            nextCellType != CellType.OUT_OF_BOUNDS && nextCellType != CellType.SAFE_AVAILABLE && nextCellType != CellType.SAFE_UNAVAILABLE
        }
        Direction.TO_BOTTOM -> {
            val nextCellType = getVal(currentPosition.x, currentPosition.y + 1)
            nextCellType != CellType.OUT_OF_BOUNDS && nextCellType != CellType.SAFE_AVAILABLE && nextCellType != CellType.SAFE_UNAVAILABLE
        }
    }

    fun canMoveToSafe(): Boolean {
        val safeToGo = getSafeToGo()
        val diffX = currentPosition.x - safeToGo.x
        val diffY = currentPosition.y - safeToGo.y

        return when (direction) {
            Direction.TO_UP -> {
                if (currentPosition.y - 1 < 0) {
                    false
                } else {
                    val nextCellType = getVal(currentPosition.x, currentPosition.y - 1)
                    diffY > 0 && nextCellType == CellType.SAFE_AVAILABLE
                }
            }
            Direction.TO_LEFT -> {
                if (currentPosition.x - 1 < 0) {
                    false
                } else {
                    val nextCellType = getVal(currentPosition.x - 1, currentPosition.y)
                    diffX > 0 && nextCellType == CellType.SAFE_AVAILABLE
                }
            }
            Direction.TO_RIGHT -> {
                if (currentPosition.x + 1 == COLUMNS) {
                    false
                } else {
                    val nextCellType = getVal(currentPosition.x + 1, currentPosition.y)
                    diffX < 0 && nextCellType == CellType.SAFE_AVAILABLE
                }
            }
            Direction.TO_BOTTOM -> {
                if (currentPosition.y + 1 == ROWS) {
                    false
                } else {
                    val nextCellType = getVal(currentPosition.x, currentPosition.y + 1)
                    diffY < 0 && nextCellType == CellType.SAFE_AVAILABLE
                }
            }
        }
    }

    fun canMoveToOrigin(): Boolean {
        val diffX = currentPosition.x - originPosition.x
        val diffY = currentPosition.y - originPosition.y

        return when (direction) {
            Direction.TO_UP -> {
                val nextCellType = getVal(currentPosition.x, currentPosition.y - 1)
                diffY > 0 && nextCellType == CellType.EMPTY
            }
            Direction.TO_LEFT -> {
                val nextCellType = getVal(currentPosition.x - 1, currentPosition.y)
                diffX > 0 && nextCellType == CellType.EMPTY
            }
            Direction.TO_RIGHT -> {
                val nextCellType = getVal(currentPosition.x + 1, currentPosition.y)
                diffX < 0 && nextCellType == CellType.EMPTY
            }
            Direction.TO_BOTTOM -> {
                val nextCellType = getVal(currentPosition.x, currentPosition.y + 1)
                diffY < 0 && nextCellType == CellType.EMPTY
            }
        }
    }

    fun canMoveToBall(ball: Point<Int, Int>): Boolean {
        val diffX = currentPosition.x - ball.x
        val diffY = currentPosition.y - ball.y

        return when (direction) {
            Direction.TO_UP -> {
                val nextCellType = getVal(currentPosition.x, currentPosition.y - 1)
                diffY > 0 && nextCellType != CellType.BALL && nextCellType != CellType.OUT_OF_BOUNDS
            }
            Direction.TO_LEFT -> {
                val nextCellType = getVal(currentPosition.x - 1, currentPosition.y)
                diffX > 0 && nextCellType != CellType.BALL && nextCellType != CellType.OUT_OF_BOUNDS
            }
            Direction.TO_RIGHT -> {
                val nextCellType = getVal(currentPosition.x + 1, currentPosition.y)
                diffX < 0 && nextCellType != CellType.BALL && nextCellType != CellType.OUT_OF_BOUNDS
            }
            Direction.TO_BOTTOM -> {
                val nextCellType = getVal(currentPosition.x, currentPosition.y + 1)
                diffY < 0 && nextCellType != CellType.BALL && nextCellType != CellType.OUT_OF_BOUNDS
            }
        }
    }


    fun canMoveToLastPoint(): Boolean {
        val diffX = currentPosition.x - lastPoint!!.x
        val diffY = currentPosition.y - lastPoint!!.y

        return when (direction) {
            Direction.TO_UP -> {
                val nextCellType = getVal(currentPosition.x, currentPosition.y - 1)
                diffY > 0 && nextCellType == CellType.EMPTY
            }
            Direction.TO_LEFT -> {
                val nextCellType = getVal(currentPosition.x - 1, currentPosition.y)
                diffX > 0 && nextCellType == CellType.EMPTY
            }
            Direction.TO_RIGHT -> {
                val nextCellType = getVal(currentPosition.x + 1, currentPosition.y)
                diffX < 0 && nextCellType == CellType.EMPTY
            }
            Direction.TO_BOTTOM -> {
                val nextCellType = getVal(currentPosition.x, currentPosition.y + 1)
                diffY < 0 && nextCellType == CellType.EMPTY
            }
        }
    }

    fun getAngleToRotate() = when (direction) {
        Direction.TO_UP -> {
            val leftCellType = getVal(currentPosition.x - 1, currentPosition.y)
            val rightCellType = getVal(currentPosition.x + 1, currentPosition.y)
            val bottomCellType = getVal(currentPosition.x, currentPosition.y + 1)

            getAngle(leftCellType, rightCellType, bottomCellType)
        }
        Direction.TO_LEFT -> {
            val leftCellType = getVal(currentPosition.x, currentPosition.y + 1)
            val rightCellType = getVal(currentPosition.x, currentPosition.y - 1)
            val bottomCellType = getVal(currentPosition.x + 1, currentPosition.y)

            getAngle(leftCellType, rightCellType, bottomCellType)
        }
        Direction.TO_RIGHT -> {
            val leftCellType = getVal(currentPosition.x, currentPosition.y - 1)
            val rightCellType = getVal(currentPosition.x, currentPosition.y + 1)
            val bottomCellType = getVal(currentPosition.x - 1, currentPosition.y)

            getAngle(leftCellType, rightCellType, bottomCellType)
        }
        Direction.TO_BOTTOM -> {
            val leftCellType = getVal(currentPosition.x + 1, currentPosition.y)
            val rightCellType = getVal(currentPosition.x - 1, currentPosition.y)
            val bottomCellType = getVal(currentPosition.x, currentPosition.y - 1)

            getAngle(leftCellType, rightCellType, bottomCellType)
        }
    }

    fun getAngleToRotateOrigin() = when (direction) {
        Direction.TO_UP -> {
            val leftCellType = getVal(currentPosition.x - 1, currentPosition.y)
            val rightCellType = getVal(currentPosition.x + 1, currentPosition.y)

            getAngleOrigin(leftCellType, rightCellType)
        }
        Direction.TO_LEFT -> {
            val leftCellType = getVal(currentPosition.x, currentPosition.y + 1)
            val rightCellType = getVal(currentPosition.x, currentPosition.y - 1)

            getAngleOrigin(leftCellType, rightCellType)
        }
        Direction.TO_RIGHT -> {
            val leftCellType = getVal(currentPosition.x, currentPosition.y - 1)
            val rightCellType = getVal(currentPosition.x, currentPosition.y + 1)

            getAngleOrigin(leftCellType, rightCellType)
        }
        Direction.TO_BOTTOM -> {
            val leftCellType = getVal(currentPosition.x + 1, currentPosition.y)
            val rightCellType = getVal(currentPosition.x - 1, currentPosition.y)

            getAngleOrigin(leftCellType, rightCellType)
        }
    }

    fun getAngleToRotateSafe(): Int {
        val safeToGo = getSafeToGo()
        return when (direction) {
            Direction.TO_UP -> if (safeToGo.x < currentPosition.x) {
                direction = direction.toLeft()
                -8
            } else {
                direction = direction.toRight()
                8
            }
            Direction.TO_LEFT -> if (safeToGo.y > currentPosition.y) {
                direction = direction.toLeft()
                -8
            } else {
                direction = direction.toRight()
                8
            }
            Direction.TO_RIGHT -> if (safeToGo.y < currentPosition.y) {
                direction = direction.toLeft()
                -8
            } else {
                direction = direction.toRight()
                8
            }
            Direction.TO_BOTTOM -> if (safeToGo.x > currentPosition.x) {
                direction = direction.toLeft()
                -8
            } else {
                direction = direction.toRight()
                8
            }
        }
    }

    fun getAngleToRotateLastDirection() = when (direction) {
        Direction.TO_UP -> when (lastDirection) {
            Direction.TO_UP -> 0
            Direction.TO_LEFT -> {
                direction = direction.toLeft()
                -8
            }
            Direction.TO_RIGHT -> {
                direction = direction.toRight()
                8
            }
            Direction.TO_BOTTOM -> {
                direction = direction.toBottom()
                16
            }
        }
        Direction.TO_LEFT -> when (lastDirection) {
            Direction.TO_UP -> {
                direction = direction.toRight()
                8
            }
            Direction.TO_LEFT -> 0
            Direction.TO_RIGHT -> {
                direction = direction.toBottom()
                16
            }
            Direction.TO_BOTTOM -> {
                direction = direction.toLeft()
                -8
            }
        }
        Direction.TO_RIGHT -> when (lastDirection) {
            Direction.TO_UP -> {
                direction = direction.toLeft()
                -8
            }
            Direction.TO_LEFT -> {
                direction = direction.toBottom()
                16
            }
            Direction.TO_RIGHT -> 0
            Direction.TO_BOTTOM -> {
                direction = direction.toRight()
                8
            }
        }
        Direction.TO_BOTTOM -> when (lastDirection) {
            Direction.TO_UP -> {
                direction = direction.toBottom()
                16
            }
            Direction.TO_LEFT -> {
                direction = direction.toRight()
                8
            }
            Direction.TO_RIGHT -> {
                direction = direction.toLeft()
                -8
            }
            Direction.TO_BOTTOM -> 0
        }
    }

    fun getAngleToRotateBall() = when (direction) {
        Direction.TO_UP -> {
            val leftCellType = getVal(currentPosition.x - 1, currentPosition.y)
            val rightCellType = getVal(currentPosition.x + 1, currentPosition.y)

            getAngleBall(leftCellType, rightCellType)
        }
        Direction.TO_LEFT -> {
            val leftCellType = getVal(currentPosition.x, currentPosition.y + 1)
            val rightCellType = getVal(currentPosition.x, currentPosition.y - 1)

            getAngleBall(leftCellType, rightCellType)
        }
        Direction.TO_RIGHT -> {
            val leftCellType = getVal(currentPosition.x, currentPosition.y - 1)
            val rightCellType = getVal(currentPosition.x, currentPosition.y + 1)

            getAngleBall(leftCellType, rightCellType)
        }
        Direction.TO_BOTTOM -> {
            val leftCellType = getVal(currentPosition.x + 1, currentPosition.y)
            val rightCellType = getVal(currentPosition.x - 1, currentPosition.y)

            getAngleBall(leftCellType, rightCellType)
        }
    }

    fun moveBack(): Int {
        when (direction) {
            Direction.TO_UP -> currentPosition.y += 1
            Direction.TO_LEFT -> currentPosition.x += 1
            Direction.TO_RIGHT -> currentPosition.x -= 1
            Direction.TO_BOTTOM -> currentPosition.y -= 1
        }
        updateCurrentPosition()
        return -(cellLength.toInt())
    }

    fun hasReachedNewLine() = when (direction) {
        Direction.TO_UP -> getVal(currentPosition.x, currentPosition.y - 1) == CellType.HIDDEN
        Direction.TO_LEFT -> getVal(currentPosition.x - 1, currentPosition.y) == CellType.HIDDEN
        Direction.TO_RIGHT -> getVal(currentPosition.x + 1, currentPosition.y) == CellType.HIDDEN
        Direction.TO_BOTTOM -> getVal(currentPosition.x, currentPosition.y + 1) == CellType.HIDDEN
    }

    fun hasToRotate() = when (direction) {
        Direction.TO_UP -> {
            val leftCellType = getVal(currentPosition.x - 1, currentPosition.y - 1)
            val rightCellType = getVal(currentPosition.x + 1, currentPosition.y - 1)

            leftCellType == CellType.HIDDEN || rightCellType == CellType.HIDDEN
        }
        Direction.TO_LEFT -> {
            val leftCellType = getVal(currentPosition.x - 1, currentPosition.y + 1)
            val rightCellType = getVal(currentPosition.x - 1, currentPosition.y - 1)

            leftCellType == CellType.HIDDEN || rightCellType == CellType.HIDDEN
        }
        Direction.TO_RIGHT -> {
            val leftCellType = getVal(currentPosition.x + 1, currentPosition.y - 1)
            val rightCellType = getVal(currentPosition.x + 1, currentPosition.y + 1)

            leftCellType == CellType.HIDDEN || rightCellType == CellType.HIDDEN
        }
        Direction.TO_BOTTOM -> {
            val leftCellType = getVal(currentPosition.x + 1, currentPosition.y + 1)
            val rightCellType = getVal(currentPosition.x - 1, currentPosition.y + 1)

            leftCellType == CellType.HIDDEN || rightCellType == CellType.HIDDEN
        }
    }

    fun isInOrigin() = originPosition == currentPosition

    fun isInSafe() = getSafeToGo() == currentPosition

    fun isOnLastPoint() = lastPoint == currentPosition

    fun getDistanceToMove(): Int {
        Timber.d("Current Position: $currentPosition")
        when (direction) {
            Direction.TO_UP -> currentPosition.y -= 1
            Direction.TO_LEFT -> currentPosition.x -= 1
            Direction.TO_RIGHT -> currentPosition.x += 1
            Direction.TO_BOTTOM -> currentPosition.y += 1
        }
        updateCurrentPosition()
        return cellLength.toInt()
    }

    fun isFieldFinished(): Boolean {
        var isFinished = true
        for (i in 0 until COLUMNS) {
            for (j in 0 until ROWS) {
                if (getVal(i, j) == CellType.HIDDEN) {
                    isFinished = false
                }
            }
        }
        return isFinished
    }

    fun inRightPosition() = direction == lastDirection

    fun isAtBall(ball: Point<Int, Int>) = ball == currentPosition

    fun ballGrabbed() {
        lastPoint = Point(currentPosition.x, currentPosition.y)
        lastDirection = direction
    }

    fun setColorOfHoldingBall(color: Color) {
        lastPoint?.let {
            ballMap[it] = color
        }
    }

    fun ballReleased() {
        setVal(currentPosition.x, currentPosition.y, CellType.SAFE_UNAVAILABLE)
    }

    private fun updateCurrentPosition() {
        for (i in 0 until COLUMNS) {
            for (j in 0 until ROWS) {
                if (getVal(i, j) == CellType.CURRENT_POSITION) {
                    setVal(i, j, CellType.EMPTY)
                }
            }
        }

        val cellValue = getVal(currentPosition.x, currentPosition.y)
        if (cellValue == CellType.HIDDEN || cellValue == CellType.EMPTY) {
            setVal(currentPosition.x, currentPosition.y, CellType.CURRENT_POSITION)
        }
        printMatrix()
    }

    private fun getVal(x: Int, y: Int): CellType {
        return if (COLUMNS > x && ROWS > y && x >= 0 && y >= 0) {
            fieldMatrix[y][x]
        } else {
            Log.d("ERROR", "X: $x - Y: $y")
            CellType.OUT_OF_BOUNDS
        }
    }

    private fun setVal(x: Int, y: Int, value: CellType) {
        if (COLUMNS > x && ROWS > y && x >= 0 && y >= 0) {
            fieldMatrix[y][x] = value
        } else {
            Log.d("ERROR", "X: $x - Y: $y")
            throw IndexOutOfBoundsException()
        }
    }

    private fun printMatrix() {
        var matrix = ""
        fieldMatrix.forEach {
            var rowString = ""
            it.forEach { cel ->
                rowString += " ${cel.ordinal}"
            }
            matrix += "\n$rowString"
        }

        Log.d("MATRIX", "Matrix:\n\n$matrix\n\n")
    }

    private fun getAngle(
        leftCellType: CellType,
        rightCellType: CellType,
        bottomCellType: CellType
    ) =
        if (leftCellType == CellType.OUT_OF_BOUNDS) {
            if (rightCellType == CellType.HIDDEN || bottomCellType == CellType.EMPTY) {
                direction = direction.toRight()
                8
            } else {
                direction = direction.toBottom()
                16
            }
        } else if (rightCellType == CellType.HIDDEN) {
            direction = direction.toRight()
            8
        } else {
            direction = direction.toLeft()
            -8
        }

    private fun getAngleOrigin(
        leftCellType: CellType,
        rightCellType: CellType
    ) = when {
        leftCellType == CellType.EMPTY -> {
            direction = direction.toLeft()
            -8
        }
        rightCellType == CellType.EMPTY -> {
            direction = direction.toRight()
            8
        }
        else -> {
            direction = direction.toBottom()
            16
        }
    }

    private fun getAngleBall(
        leftCellType: CellType,
        rightCellType: CellType
    ) = when {
        leftCellType != CellType.BALL && leftCellType != CellType.OUT_OF_BOUNDS -> {
            direction = direction.toLeft()
            -8
        }
        rightCellType != CellType.BALL && rightCellType != CellType.OUT_OF_BOUNDS -> {
            direction = direction.toRight()
            8
        }
        else -> {
            direction = direction.toBottom()
            16
        }
    }

    private fun isOriginInCorner() = originPosition == TOP_LEFT ||
            originPosition == TOP_RIGHT ||
            originPosition == BOTTOM_LEFT ||
            originPosition == BOTTOM_RIGHT

    private fun getSafeToGo() = when {
        isOriginInCorner() -> {
            when (originPosition) {
                TOP_LEFT -> {
                    when (CellType.SAFE_AVAILABLE) {
                        getVal(0, 0) -> {
                            Point(0, 0)
                        }
                        getVal(1, 0) -> {
                            Point(1, 0)
                        }
                        else -> {
                            Point(0, 1)
                        }
                    }
                }
                TOP_RIGHT -> {
                    when (CellType.SAFE_AVAILABLE) {
                        getVal(COLUMNS - 1, 0) -> {
                            Point(COLUMNS - 1, 0)
                        }
                        getVal(COLUMNS - 2, 0) -> {
                            Point(COLUMNS - 2, 0)
                        }
                        else -> {
                            Point(COLUMNS - 1, 1)
                        }
                    }
                }
                BOTTOM_LEFT -> {
                    when (CellType.SAFE_AVAILABLE) {
                        getVal(0, ROWS - 1) -> {
                            Point(0, ROWS - 1)
                        }
                        getVal(1, ROWS - 1) -> {
                            Point(1, ROWS - 1)
                        }
                        else -> {
                            Point(0, ROWS - 2)
                        }
                    }
                }
                else -> {
                    when (CellType.SAFE_AVAILABLE) {
                        getVal(COLUMNS - 1, ROWS - 1) -> {
                            Point(COLUMNS - 1, ROWS - 1)
                        }
                        getVal(COLUMNS - 2, ROWS - 1) -> {
                            Point(COLUMNS - 2, ROWS - 1)
                        }
                        else -> {
                            Point(COLUMNS - 1, ROWS - 2)
                        }
                    }
                }
            }
        }
        else -> when {
            originPosition.x == 1 -> {
                when (CellType.SAFE_AVAILABLE) {
                    getVal(0, originPosition.y - 1) -> {
                        Point(0, originPosition.y - 1)
                    }
                    getVal(0, originPosition.y + 1) -> {
                        Point(0, originPosition.y + 1)
                    }
                    else -> {
                        Point(0, originPosition.y)
                    }
                }
            }
            originPosition.y == 1 -> when (CellType.SAFE_AVAILABLE) {
                getVal(originPosition.x - 1, 0) -> {
                    Point(originPosition.x - 1, 0)
                }
                getVal(originPosition.x + 1, 0) -> {
                    Point(originPosition.x + 1, 0)
                }
                else -> {
                    Point(originPosition.x, 0)
                }
            }
            originPosition.x == COLUMNS - 2 -> when (CellType.SAFE_AVAILABLE) {
                getVal(COLUMNS - 1, originPosition.y - 1) -> {
                    Point(COLUMNS - 1, originPosition.y - 1)
                }
                getVal(COLUMNS - 1, originPosition.y + 1) -> {
                    Point(COLUMNS - 1, originPosition.y + 1)
                }
                else -> {
                    Point(COLUMNS - 1, originPosition.y)
                }
            }
            else -> when (CellType.SAFE_AVAILABLE) {
                getVal(originPosition.x - 1, ROWS - 1) -> {
                    Point(originPosition.x - 1, ROWS - 1)
                }
                getVal(originPosition.x + 1, ROWS - 1) -> {
                    Point(originPosition.x + 1, ROWS - 1)
                }
                else -> {
                    Point(originPosition.x, ROWS - 1)
                }
            }
        }
    }

    fun getFinalMatrix(): String {
        var matrix = ""
        fieldMatrix.forEachIndexed { i, value ->
            var rowString = ""
            value.forEachIndexed { j, cell ->
                rowString += if (ballMap.containsKey(Point(j, i))) {
                    val color = when (ballMap[Point(j, i)]) {
                        Color.BLUE -> "B"
                        Color.YELLOW -> "Y"
                        else -> "R"
                    }
                    " $color"
                } else {
                    " ${cell.ordinal}"
                }
            }
            matrix += "\n$rowString"
        }
        return matrix
    }

    private enum class CellType {
        OUT_OF_BOUNDS,
        BALL,
        HIDDEN,
        EMPTY,
        CURRENT_POSITION,
        SAFE_AVAILABLE,
        SAFE_UNAVAILABLE
    }

    private enum class Direction {
        TO_UP,
        TO_LEFT,
        TO_RIGHT,
        TO_BOTTOM;

        fun toRight() = when (this@Direction) {
            TO_UP -> TO_RIGHT
            TO_LEFT -> TO_UP
            TO_RIGHT -> TO_BOTTOM
            TO_BOTTOM -> TO_LEFT
        }

        fun toLeft() = when (this@Direction) {
            TO_UP -> TO_LEFT
            TO_LEFT -> TO_BOTTOM
            TO_RIGHT -> TO_UP
            TO_BOTTOM -> TO_RIGHT
        }

        fun toBottom() = when (this@Direction) {
            TO_UP -> TO_BOTTOM
            TO_LEFT -> TO_RIGHT
            TO_RIGHT -> TO_LEFT
            TO_BOTTOM -> TO_UP
        }
    }
}