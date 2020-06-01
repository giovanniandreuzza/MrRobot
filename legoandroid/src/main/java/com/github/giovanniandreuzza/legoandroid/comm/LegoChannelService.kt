package com.github.giovanniandreuzza.legoandroid.comm

import kotlinx.coroutines.runBlocking

/**
 * This class implements the lego channel that sends commands and receives replies.
 *
 * @see LegoChannel
 */
class LegoChannelService(private val channel: Channel) : LegoChannel {

    override fun send(cmd: Command): Reply = runBlocking {
        val a: ByteArray = cmd.marshal()
        val l = byteArrayOf((a.size and 0xFF).toByte(), (a.size shr 8 and 0xFF).toByte())

        val result = l.copyOf(l.size + a.size)
        System.arraycopy(a, 0, result, l.size, a.size)


        var contSize = 0
        var contPayload = 0

        val sizeArr = mutableListOf<Byte>()
        val payloadArr = mutableListOf<Byte>()
        var payloadSize: Int


        channel.send(result)


        while (contSize < 2) {
            sizeArr.add(channel.receive())
            contSize++
        }

        payloadSize = sizeArr[1].toInt() and 0xff shl 8 or (sizeArr[0].toInt() and 0xff)

        while (contPayload < payloadSize) {
            payloadArr.add(channel.receive())
            contPayload++
        }

        Reply(payloadArr.toByteArray())
    }

    override suspend fun send(reservation: Int, bc: Bytecode): Reply =
        send(Command(true, 0, reservation, bc.getBytes()))

    override fun sendNoReply(bc: Bytecode) {
        val a: ByteArray = Command(false, 0, 0, bc.getBytes()).marshal()
        val l = byteArrayOf((a.size and 0xFF).toByte(), (a.size shr 8 and 0xFF).toByte())

        val result = l.copyOf(l.size + a.size)
        System.arraycopy(a, 0, result, l.size, a.size)
        channel.send(result)
    }

    override fun close() {
        // Nothing
    }

}