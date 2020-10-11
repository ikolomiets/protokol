package com.electrit.protokol

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LongTest {

    class LongData(var l: Long = 0)

    object LongDataProtokolObject : ProtokolObject<LongData> {
        override fun use(value: LongData, p: Protokol) = with(p) {
            with(value) {
                LONG(::l)
            }
        }

        override fun create(): LongData = LongData()
    }

    object StrictLongDataProtokolObject : ProtokolObject<LongData> {
        override fun use(value: LongData, p: Protokol) = with(p) {
            with(value) {
                LONG(::l) { if (it == 0.toLong()) throw IllegalArgumentException("value can't be 0") }
            }
        }

        override fun create(): LongData = LongData()
    }

    @Test
    fun test() {
        fun assert(l: Long, po: ProtokolObject<LongData>) {
            val bytes = ByteArrayProtokolCodec.encode(LongData(l), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(l, data.l)
        }

        assert(Long.MIN_VALUE, LongDataProtokolObject)
        assert(0, LongDataProtokolObject)
        assert(Long.MAX_VALUE, LongDataProtokolObject)

        assert(Long.MIN_VALUE, StrictLongDataProtokolObject)
        assertFailsWith<IllegalArgumentException> { assert(0, StrictLongDataProtokolObject) }
        assert(Long.MAX_VALUE, StrictLongDataProtokolObject)
    }

    @Test
    fun testParseError() {
        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(LongData(0), LongDataProtokolObject),
                StrictLongDataProtokolObject
            )
        }
    }

}