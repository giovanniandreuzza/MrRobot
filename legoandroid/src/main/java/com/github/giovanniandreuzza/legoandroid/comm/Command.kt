package com.github.giovanniandreuzza.legoandroid.comm

class Command(
    private val hasReply: Boolean,
    localReservation: Int,
    globalReservation: Int,
    bytecode: ByteArray
) : Packet(sequenceCounter++, bytecode) {

    companion object {
        private var sequenceCounter = 0
    }

    private var reservationH = 0
    private var reservationL = 0

    init {
        require(globalReservation <= 1024) { "global buffer must be less than 1024 bytes" }
        require(localReservation <= 64) { "local buffer must be less than 64 bytes" }

        this.reservationH =
            localReservation shl 2 and 0x3.inv() or (globalReservation shr 8 and 0x03)
        this.reservationL = globalReservation and 0xFF
    }

    /**
     * Serialized the command into an array of bytes.
     *
     * @return the array of byte with the raw representation of the command.
     */
    fun marshal() = ByteArray(5 + data.size).apply {
        this[0] = (counter and 0xFF).toByte()
        this[1] = (counter shr 8 and 0xFF).toByte()
        this[2] = if (hasReply) Const.DIRECT_COMMAND_REPLY else Const.DIRECT_COMMAND_NOREPLY
        this[3] = (reservationL and 0xFF).toByte()
        this[4] = (reservationH and 0xFF).toByte()
        System.arraycopy(data, 0, this, 5, data.size)
    }

}