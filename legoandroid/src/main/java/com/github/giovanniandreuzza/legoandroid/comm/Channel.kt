package com.github.giovanniandreuzza.legoandroid.comm

import io.reactivex.Observable

interface Channel {

    /**
     * Send a [ByteArray].
     *
     * @param data the command to be sent.
     */
    fun send(data: ByteArray)

    /**
     * Receive a [Flowable<Byte>]
     *
     * @return the object of type [Flowable<Byte>].
     */
    suspend fun receive(): Byte

    /**
     * Disconnect from the device.
     */
    fun close()

}