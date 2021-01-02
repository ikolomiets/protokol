package com.electrit.protokol

import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@IgnoreJs
class FloatTest {

    class FloatData(var f: Float = 0f)

    object FloatDataProtokolObject : ProtokolObject<FloatData> {
        override fun use(value: FloatData, p: Protokol) = with(p) {
            with(value) {
                FLOAT(::f)
            }
        }

        override fun create() = FloatData()
    }

    object StrictFloatDataProtokolObject : ProtokolObject<FloatData> {
        override fun use(value: FloatData, p: Protokol) = with(p) {
            with(value) {
                FLOAT(::f) { if (it < 0) throw IllegalArgumentException("value can't be negative") }
            }
        }

        override fun create() = FloatData()
    }

    @Test
    fun test() {
        fun assert(f: Float, po: ProtokolObject<FloatData>) {
            val bytes = ByteArrayProtokolCodec.encode(FloatData(f), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(f, data.f)
        }

        assert(Float.MIN_VALUE, FloatDataProtokolObject)
        assert(0f, FloatDataProtokolObject)
        assert(PI.toFloat(), FloatDataProtokolObject)
        assert(Float.NEGATIVE_INFINITY, FloatDataProtokolObject)
        assert(Float.POSITIVE_INFINITY, FloatDataProtokolObject)
        assert(Float.NaN, FloatDataProtokolObject)
        assert(Float.MAX_VALUE, FloatDataProtokolObject)

        assertFailsWith<IllegalArgumentException> { assert(-1 * PI.toFloat(), StrictFloatDataProtokolObject) }
        assert(PI.toFloat(), StrictFloatDataProtokolObject)
    }

    @Test
    fun testParseError() {
        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(FloatData(-1 * PI.toFloat()), FloatDataProtokolObject),
                StrictFloatDataProtokolObject
            )
        }
    }

}