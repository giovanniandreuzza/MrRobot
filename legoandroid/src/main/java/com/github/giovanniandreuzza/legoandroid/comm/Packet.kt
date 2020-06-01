package com.github.giovanniandreuzza.legoandroid.comm

/**
 * Abstract class that serves as a superclass for {@link Command} and {@link Reply}.
 */
abstract class Packet(val counter: Int, val data: ByteArray)