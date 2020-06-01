package com.github.giovanniandreuzza.legoandroid.comm

class Reply(bytes: ByteArray) : Packet(
    bytes[1].toInt() shl 8 and 0xff00 or (bytes[0].toInt() and 0xff),
    ByteArray(bytes.size - 3)
) {

    companion object {
        private const val TAG = "Reply"
    }

    val error = bytes[2] != Const.DIRECT_COMMAND_SUCCESS

    init {
        System.arraycopy(bytes, 3, data, 0, data.size)
    }

}