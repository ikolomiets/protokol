package com.electrit.protokol

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ShortTest {

    class ShortData(var s: Short = 0)

    object ShortDataProtokolObject : ProtokolObject<ShortData> {
        override val protokol: Protokol.(ShortData) -> Unit = {
            with(it) {
                SHORT(::s)
            }
        }

        override fun create() = ShortData()
    }

    object StrictShortDataProtokolObject : ProtokolObject<ShortData> {
        override val protokol: Protokol.(ShortData) -> Unit = {
            with(it) {
                SHORT(::s) { value -> if (value.toInt() == 0) throw IllegalArgumentException("value can't be 0") }
            }
        }

        override fun create() = ShortData()
    }

    @Test
    fun test() {
        fun assert(s: Short, po: ProtokolObject<ShortData>) {
            val bytes = ByteArrayProtokolCodec.encode(ShortData(s), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(s, data.s)
        }

        assert(Short.MIN_VALUE, ShortDataProtokolObject)
        assert(0, ShortDataProtokolObject)
        assert(Short.MAX_VALUE, ShortDataProtokolObject)

        assert(Short.MIN_VALUE, StrictShortDataProtokolObject)
        assertFailsWith<IllegalArgumentException> { assert(0, StrictShortDataProtokolObject) }
        assert(Short.MAX_VALUE, StrictShortDataProtokolObject)
    }

    @Test
    fun testParseError() {
        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(ShortData(0), ShortDataProtokolObject),
                StrictShortDataProtokolObject
            )
        }
    }

}