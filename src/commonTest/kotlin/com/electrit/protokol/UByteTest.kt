package com.electrit.protokol

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UByteTest {

    class UByteData(var ubyte: UByte = 0u)

    object UByteDataProtokolObject : ProtokolObject<UByteData> {
        override val protokol: Protokol.(UByteData) -> Unit = {
            with(it) {
                UBYTE(::ubyte)
            }
        }

        override fun create() = UByteData()
    }

    object StrictUByteDataProtokolObject : ProtokolObject<UByteData> {
        override val protokol: Protokol.(UByteData) -> Unit = {
            with(it) {
                UBYTE(::ubyte) { value -> if (value.toInt() == 0) throw IllegalArgumentException("zero is not allowed") }
            }
        }

        override fun create() = UByteData()
    }

    @Test
    fun test() {
        fun assert(ub: UByte, po: ProtokolObject<UByteData>) {
            val bytes = ByteArrayProtokolCodec.encode(UByteData(ub), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(ub, data.ubyte)
        }

        assert(UByte.MIN_VALUE, UByteDataProtokolObject)
        assert(0u, UByteDataProtokolObject)
        assert(UByte.MAX_VALUE, UByteDataProtokolObject)
        assert(200u, UByteDataProtokolObject)

        assertFailsWith<IllegalArgumentException> { assert(UByte.MIN_VALUE, StrictUByteDataProtokolObject) }
        assertFailsWith<IllegalArgumentException> { assert(0u, StrictUByteDataProtokolObject) }
        assert(UByte.MAX_VALUE, StrictUByteDataProtokolObject)
        assert(1u, StrictUByteDataProtokolObject)
        assert(200u, StrictUByteDataProtokolObject)
    }

    @Test
    fun testParseError() {
        val bytes = ByteArrayProtokolCodec.encode(UByteData(0u), UByteDataProtokolObject)
        assertFailsWith<IllegalArgumentException> { ByteArrayProtokolCodec.decode(bytes, StrictUByteDataProtokolObject) }
    }

}