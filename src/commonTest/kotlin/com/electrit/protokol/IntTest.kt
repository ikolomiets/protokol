package com.electrit.protokol

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class IntTest {

    class IntData(var i: Int = 0)

    object IntDataProtokolObject : ProtokolObject<IntData> {
        override fun use(value: IntData, p: Protokol) = with(p) {
            with(value) {
                INT(::i)
            }
        }

        override fun create() = IntData()
    }

    object StrictIntDataProtokolObject : ProtokolObject<IntData> {
        override fun use(value: IntData, p: Protokol) = with(p) {
            with(value) {
                INT(::i) { if (it == 0) throw IllegalArgumentException("value can't be 0") }
            }
        }

        override fun create() = IntData()
    }

    @Test
    fun test() {
        fun assert(i: Int, po: ProtokolObject<IntData>) {
            val bytes = ByteArrayProtokolCodec.encode(IntData(i), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(i, data.i)
        }

        assert(Int.MIN_VALUE, IntDataProtokolObject)
        assert(0, IntDataProtokolObject)
        assert(Int.MAX_VALUE, IntDataProtokolObject)

        assert(Int.MIN_VALUE, StrictIntDataProtokolObject)
        assertFailsWith<IllegalArgumentException> { assert(0, StrictIntDataProtokolObject) }
        assert(Int.MAX_VALUE, StrictIntDataProtokolObject)
    }

    @Test
    fun testParseError() {
        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(IntData(0), IntDataProtokolObject),
                StrictIntDataProtokolObject
            )
        }
    }

}