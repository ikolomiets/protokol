package com.electrit.protokol

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UShortTest {

    class UShortData(var u: UShort = 0u)

    object UIntDataProtokolObject : ProtokolObject<UShortData> {
        override val protokol: Protokol.(UShortData) -> Unit = {
            with(it) {
                USHORT(::u)
            }
        }

        override fun create() = UShortData()
    }

    object StrictUIntDataProtokolObject : ProtokolObject<UShortData> {
        override val protokol: Protokol.(UShortData) -> Unit = {
            with(it) {
                USHORT(::u) { value -> if (value.toInt() == 0) throw IllegalArgumentException("value can't be 0") }
            }
        }

        override fun create() = UShortData()
    }

    @Test
    fun test() {
        fun assert(u: UShort, po: ProtokolObject<UShortData>) {
            val bytes = ByteArrayProtokolCodec.encode(UShortData(u), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(u, data.u)
        }

        assert(UShort.MIN_VALUE, UIntDataProtokolObject)
        assert(12345u, UIntDataProtokolObject)
        assert(UShort.MAX_VALUE, UIntDataProtokolObject)

        assertFailsWith<IllegalArgumentException> { assert(UShort.MIN_VALUE, StrictUIntDataProtokolObject) }
        assert(12345u, StrictUIntDataProtokolObject)
        assert(UShort.MAX_VALUE, StrictUIntDataProtokolObject)
    }

    @Test
    fun testParseError() {
        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(UShortData(0u), UIntDataProtokolObject),
                StrictUIntDataProtokolObject
            )
        }
    }

}