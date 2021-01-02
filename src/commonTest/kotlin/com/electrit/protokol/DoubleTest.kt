package com.electrit.protokol

import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DoubleTest {

    class DoubleData(var d: Double = 0.0)

    object DoubleDataProtokolObject : ProtokolObject<DoubleData> {
        override fun use(value: DoubleData, p: Protokol) = with(p) {
            with(value) {
                DOUBLE(::d)
            }
        }

        override fun create() = DoubleData()
    }

    object StrictDoubleDataProtokolObject : ProtokolObject<DoubleData> {
        override fun use(value: DoubleData, p: Protokol) = with(p) {
            with(value) {
                DOUBLE(::d) { if (it < 0) throw IllegalArgumentException("value can't be negative") }
            }
        }

        override fun create() = DoubleData()
    }

    @Test
    fun test() {
        fun assert(d: Double, po: ProtokolObject<DoubleData>) {
            val bytes = ByteArrayProtokolCodec.encode(DoubleData(d), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(d, data.d)
        }

        assert(Double.MIN_VALUE, DoubleDataProtokolObject)
        assert(0.0, DoubleDataProtokolObject)
        assert(PI, DoubleDataProtokolObject)
        assert(Double.NEGATIVE_INFINITY, DoubleDataProtokolObject)
        assert(Double.POSITIVE_INFINITY, DoubleDataProtokolObject)
        assert(Double.NaN, DoubleDataProtokolObject)
        assert(Double.MAX_VALUE, DoubleDataProtokolObject)

        assertFailsWith<IllegalArgumentException> { assert(-1 * PI, StrictDoubleDataProtokolObject) }
        assert(PI, StrictDoubleDataProtokolObject)
    }

    @Test
    fun testParseError() {
        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(DoubleData(-1.0), DoubleDataProtokolObject),
                StrictDoubleDataProtokolObject
            )
        }
    }

}