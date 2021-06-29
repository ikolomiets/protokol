package com.electrit.protokol

class ByteArrayProtokolComposer(private var size: Int) : ProtokolComposer() {

    val bytes = ByteArray(size)
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
        require(offset + size <= this.size) { "Not enough space: free=${this.size - offset}, need=$size" }

}