package com.electrit.protokol

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UIntTest {

    class UIntData(var u: UInt = 0u)

    object UIntDataProtokolObject : ProtokolObject<UIntData> {
        override val protokol: Protokol.(UIntData) -> Unit = {
            with(it) {
                UINT(::u)
            }
        }

        override fun create() = UIntData()
    }

    object StrictUIntDataProtokolObject : ProtokolObject<UIntData> {
        override val protokol: Protokol.(UIntData) -> Unit = {
            with(it) {
                UINT(::u) { value -> if (value == 0u) throw IllegalArgumentException("value can't be 0") }
            }
        }

        override fun create() = UIntData()
    }

    @Test
    fun test() {
        fun assert(u: UInt, po: ProtokolObject<UIntData>) {
            val bytes = ByteArrayProtokolCodec.encode(UIntData(u), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(u, data.u)
        }

        assert(UInt.MIN_VALUE, UIntDataProtokolObject)
        assert(1234567890u, UIntDataProtokolObject)
        assert(UInt.MAX_VALUE, UIntDataProtokolObject)

        assertFailsWith<IllegalArgumentException> { assert(UInt.MIN_VALUE, StrictUIntDataProtokolObject) }
        assert(1234567890u, StrictUIntDataProtokolObject)
        assert(UInt.MAX_VALUE, StrictUIntDataProtokolObject)
    }

    @Test
    fun testParseError() {
        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(UIntData(0u), UIntDataProtokolObject),
                StrictUIntDataProtokolObject
            )
        }
    }

}