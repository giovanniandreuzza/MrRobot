package com.github.giovanniandreuzza.legoandroid.comm

import io.reactivex.Single

interface LegoChannel {

    /**
     * Send a [Command] asynchronously and returns a future object hosting the [Reply] object.
     *
     * @param cmd the command to be sent.
     * @return the future object hosting the reply.
     */
    fun send(cmd: Command): Reply

    /**
     * Send a [Command] asynchronously and returns a future object hosting the [Reply] object.
     * Can specify the global reservation for the command on the GenEV3 side.
     *
     * @param reservation number of bytes for the global reservation on the GenEV3.
     * @param bc          object of type Bytecode with the command.
     * @return the future object hossting the reply.
     */
    suspend fun send(reservation: Int, bc: Bytecode): Reply

    /**
     * Lower-level method for sending a custom-built Bytecode objects as commands.
     *
     * @param bc the object of type Bytecode.
     */
    fun sendNoReply(bc: Bytecode)

    /**
     * Schedule disconnection from the device.
     * Calling this method explicitly invalidates the object, therefore it is recommended to trigger the [AutoCloseable] behaviour by losing the reference to the object instead, when you need to disconnect.
     */
    fun close()

}