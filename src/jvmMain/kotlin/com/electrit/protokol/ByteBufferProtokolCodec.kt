package com.electrit.protokol

import java.nio.ByteBuffer

object ByteBufferProtokolCodec : ProtokolCodec() {

    private class ByteBufferProtokolComposer(private val buffer: ByteBuffer) : ProtokolComposer() {
        override fun composeByte(value: Byte) {
            buffer.put(value)
        }

        override fun composeByteArray(value: ByteArray) {
            buffer.put(value)
        }
    }

    private class ByteBufferProtokolParser(private val buffer: ByteBuffer) : ProtokolParser() {
        override fun parseBYTE(): Byte = buffer.get()

        override fun parseBYTEARRAY(): ByteArray {
            val size = parseSize()
            val result = ByteArray(size)
            buffer.get(result)
            return result
        }
    }

    override fun <T> encode(value: T, po: ProtokolObject<T>): ByteArray {
        val protokol = po.protokol
        val sizer = Sizer()
        sizer.protokol(value)
        val buffer = ByteBuffer.allocate(sizer.size)
        val composer = ByteBufferProtokolComposer(buffer)
        composer.protokol(value)
        return buffer.array()
    }

    override fun <T> decode(bytes: ByteArray, po: ProtokolObject<T>): T {
        val value = po.create()
        val protokol = po.protokol
        val buffer = ByteBuffer.wrap(bytes)
        val parser = ByteBufferProtokolParser(buffer)
        parser.protokol(value)
        return value
    }

}

