package com.electrit.protokol

object ByteArrayProtokolCodec : ProtokolCodec<ByteArray>() {

    private class ByteArrayProtokolComposer(private val bytes: ByteArray) : StandardProtokolComposer() {
        private var offset = 0

        override fun composeBYTE(value: Byte) {
            ensureEnoughSpace(1)
            bytes[offset++] = value
        }

        override fun composeBYTEARRAY(value: ByteArray) {
            composeSize(value.size)
            ensureEnoughSpace(value.size)
            value.copyInto(bytes, offset)
            offset += value.size
        }

        private fun ensureEnoughSpace(size: Int) =
            require(offset + size <= bytes.size) { "Not enough space: free=${bytes.size - offset}, need=$size" }
    }

    private class ByteArrayProtokolParser(private val bytes: ByteArray) : StandardProtokolParser() {
        private var offset = 0

        override fun parseBYTE(): Byte = bytes[offset++]

        override fun parseBYTEARRAY(): ByteArray {
            val size = parseSize()
            val result = bytes.copyOfRange(offset, offset + size)
            offset += size
            return result
        }
    }

    override fun allocate(size: Int): ByteArray = ByteArray(size)

    override fun createComposer(data: ByteArray): StandardProtokolComposer = ByteArrayProtokolComposer(data)

    override fun createParser(data: ByteArray): StandardProtokolParser = ByteArrayProtokolParser(data)

}