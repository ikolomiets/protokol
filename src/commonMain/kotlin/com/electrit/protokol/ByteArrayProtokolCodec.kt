package com.electrit.protokol

object ByteArrayProtokolCodec : ProtokolCodec() {

    private class ByteArrayProtokolComposer(private val bytes: ByteArray) : ProtokolComposer() {
        private var offset = 0

        override fun composeByte(value: Byte) {
            ensureEnoughSpace(1)
            bytes[offset++] = value
        }

        override fun composeByteArray(value: ByteArray) {
            composeSize(value.size)
            ensureEnoughSpace(value.size)
            value.copyInto(bytes, offset)
            offset += value.size
        }

        private fun ensureEnoughSpace(size: Int) =
            require(offset + size <= bytes.size) { "Not enough space: free=${bytes.size - offset}, need=$size" }
    }

    private class ByteArrayProtokolParser(private val bytes: ByteArray) : ProtokolParser() {
        private var offset = 0

        override fun parseBYTE(): Byte = bytes[offset++]

        override fun parseBYTEARRAY(): ByteArray {
            val size = parseSize()
            val result = bytes.copyOfRange(offset, offset + size)
            offset += size
            return result
        }
    }

    override fun <T> encode(value: T, po: ProtokolObject<T>): ByteArray {
        val protokol = po.protokol

        val sizer = Sizer().apply {
            protokol(value)
        }

        return ByteArray(sizer.size).apply {
            with(ByteArrayProtokolComposer(this)) {
                protokol(value)
            }
        }
    }

    override fun <T> decode(bytes: ByteArray, po: ProtokolObject<T>): T {
        val protokol = po.protokol
        return po.create().apply {
            with(ByteArrayProtokolParser(bytes)) {
                protokol(this@apply)
            }
        }
    }

}