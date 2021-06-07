package com.electrit.protokol

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BooleanTest {

    class BooleanData(var bool: Boolean = false)

    object BooleanDataProtokolObject : ProtokolObject<BooleanData> {
        override val protokol: Protokol.(BooleanData) -> Unit = {
            with(it) {
                BOOLEAN(::bool)
            }
        }

        override fun create() = BooleanData()
    }

    object StrictBooleanDataProtokolObject : ProtokolObject<BooleanData> {
        override val protokol: Protokol.(BooleanData) -> Unit = {
            with(it) {
                BOOLEAN(::bool) { value -> if (value) throw IllegalArgumentException("Only 'false' value is allowed") }
            }
        }

        override fun create() = BooleanData()
    }

    @Test
    fun test() {
        fun assert(b: Boolean, po: ProtokolObject<BooleanData>) {
            val bytes = ByteArrayProtokolCodec.encode(BooleanData(b), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(b, data.bool)
        }

        assert(false, BooleanDataProtokolObject)
        assert(true, BooleanDataProtokolObject)

        assert(false, StrictBooleanDataProtokolObject)
        assertFailsWith<IllegalArgumentException> { assert(true, StrictBooleanDataProtokolObject) }
    }

    @Test
    fun testParseError() {
        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(BooleanData(true), BooleanDataProtokolObject),
                StrictBooleanDataProtokolObject
            )
        }
    }

}