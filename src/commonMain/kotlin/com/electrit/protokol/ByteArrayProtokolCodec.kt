package com.electrit.protokol

object ByteArrayProtokolCodec {

    fun <T> encode(value: T, po: ProtokolObject<T>): ByteArray {
        val sizer = Sizer()
        po.use(value, sizer)
        val composer = ByteArrayProtokolComposer(sizer.size)
        po.use(value, composer)
        return composer.bytes
    }

    fun <T> decode(bytes: ByteArray, po: ProtokolObject<T>): T {
        val value = po.create()
        po.use(value, ByteArrayProtokolParser(bytes))
        return value
    }

    private class Sizer : ProtokolComposer() {
        var size: Int = 0

        override fun composeBYTE(value: Byte, validator: (Byte) -> Unit) {
            size++
        }

        override fun composeBYTEARRAY(value: ByteArray, validator: (ByteArray) -> Unit) {
            composeSize(value.size)
            size += value.size
        }
    }

}

