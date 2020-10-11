package com.electrit.protokol

class ByteArrayProtokolParser(private val bytes: ByteArray) : ProtokolParser() {

    private var offset = 0

    override fun parseBYTE(): Byte = bytes[offset++]

    override fun parseBYTEARRAY(): ByteArray {
        val size = parseSize()
        val result = bytes.copyOfRange(offset, offset + size)
        offset += size
        return result
    }

}