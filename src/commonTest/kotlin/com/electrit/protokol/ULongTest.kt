package com.electrit.protokol

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ULongTest {

    class ULongData(var u: ULong = 0u)

    object ULongDataProtokolObject : ProtokolObject<ULongData> {
        override val protokol: Protokol.(ULongData) -> Unit = {
            with(it) {
                ULONG(::u)
            }
        }

        override fun create() = ULongData()
    }

    object StrictULongDataProtokolObject : ProtokolObject<ULongData> {
        override val protokol: Protokol.(ULongData) -> Unit = {
            with(it) {
                ULONG(::u) { value -> if (value == ULong.MIN_VALUE) throw IllegalArgumentException("value can't be 0") }
            }
        }

        override fun create() = ULongData()
    }

    @Test
    fun test() {
        fun assert(u: ULong, po: ProtokolObject<ULongData>) {
            val bytes = ByteArrayProtokolCodec.encode(ULongData(u), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(u, data.u)
        }

        assert(ULong.MIN_VALUE, ULongDataProtokolObject)
        assert(12345678901234567890u, ULongDataProtokolObject)
        assert(ULong.MAX_VALUE, ULongDataProtokolObject)

        assertFailsWith<IllegalArgumentException> { assert(ULong.MIN_VALUE, StrictULongDataProtokolObject) }
        assert(12345678901234567890u, StrictULongDataProtokolObject)
        assert(ULong.MAX_VALUE, StrictULongDataProtokolObject)
    }

    @Test
    fun testParseError() {
        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(ULongData(0u), ULongDataProtokolObject),
                StrictULongDataProtokolObject
            )
        }
    }

}