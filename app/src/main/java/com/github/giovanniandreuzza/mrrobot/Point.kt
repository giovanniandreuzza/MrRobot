package com.github.giovanniandreuzza.mrrobot

data class Point<A, B>(var x: A, var y: B) : java.io.Serializable {

    /**
     * Returns string representation of the [Pair] including its [first] and [second] values.
     */
    override fun toString(): String = "($x, $y)"

    override fun equals(other: Any?): Boolean {
        return if (other is Point<*, *>) {
            x == other.x && y == other.y
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = x?.hashCode() ?: 0
        result = 31 * result + (y?.hashCode() ?: 0)
        return result
    }
}