package com.electrit.protokol

import java.nio.ByteBuffer

abstract class ByteBufferProtokolCodec : ProtokolCodec<ByteBuffer>() {

    private class ByteBufferProtokolComposer(private val buffer: ByteBuffer) : StandardProtokolComposer() {
        override fun composeBYTE(value: Byte) {
            buffer.put(value)
        }

        override fun composeBYTEARRAY(value: ByteArray) {
            buffer.put(value)
        }
    }

    private class ByteBufferProtokolParser(private val buffer: ByteBuffer) : StandardProtokolParser() {
        override fun parseBYTE(): Byte = buffer.get()

        override fun parseBYTEARRAY(): ByteArray {
            val size = parseSize()
            val result = ByteArray(size)
            buffer.get(result)
            return result
        }
    }

    override fun createComposer(data: ByteBuffer): StandardProtokolComposer = ByteBufferProtokolComposer(data)

    override fun createParser(data: ByteBuffer): StandardProtokolParser = ByteBufferProtokolParser(data)

}

object HeapByteBufferProtokolCodec : ByteBufferProtokolCodec() {
    override fun allocate(size: Int): ByteBuffer = ByteBuffer.allocate(size)
}

object DirectByteBufferProtokolCodec : ByteBufferProtokolCodec() {
    override fun allocate(size: Int): ByteBuffer = ByteBuffer.allocateDirect(size)
}
