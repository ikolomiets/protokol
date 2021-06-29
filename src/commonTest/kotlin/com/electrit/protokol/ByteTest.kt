package com.electrit.protokol

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ByteTest {

    class ByteData(var byte: Byte = 0)

    object ByteDataProtokolObject : ProtokolObject<ByteData> {
        override val protokol: Protokol.(ByteData) -> Unit = {
            with(it) {
                BYTE(::byte)
            }
        }

        override fun create() = ByteData()
    }

    object StrictByteDataProtokolObject : ProtokolObject<ByteData> {
        override val protokol: Protokol.(ByteData) -> Unit = {
            with(it) {
                BYTE(::byte) { value -> if (value.toInt() == 0) throw IllegalArgumentException("zero is not allowed") }
            }
        }

        override fun create() = ByteData()
    }

    @Test
    fun test() {
        fun assert(b: Byte, po: ProtokolObject<ByteData>) {
            val bytes = ByteArrayProtokolCodec.encode(ByteData(b), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(b, data.byte)
        }

        assert(Byte.MIN_VALUE, ByteDataProtokolObject)
        assert(Byte.MAX_VALUE, ByteDataProtokolObject)
        assert(100, ByteDataProtokolObject)

        assert(Byte.MIN_VALUE, StrictByteDataProtokolObject)
        assertFailsWith<IllegalArgumentException> { assert(0, StrictByteDataProtokolObject) }
        assert(Byte.MAX_VALUE, StrictByteDataProtokolObject)
        assert(100, StrictByteDataProtokolObject)
    }

    @Test
    fun testParseError() {
        val bytes = ByteArrayProtokolCodec.encode(ByteData(0), ByteDataProtokolObject)
        assertFailsWith<IllegalArgumentException> { ByteArrayProtokolCodec.decode(bytes, StrictByteDataProtokolObject) }
    }

}