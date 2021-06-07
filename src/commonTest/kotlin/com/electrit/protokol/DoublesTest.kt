package com.electrit.protokol

import kotlin.math.PI
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DoublesTest {

    class DoublesData(var list: List<Double> = emptyList())

    object DoublesDataProtokolObject : ProtokolObject<DoublesData> {
        override val protokol: Protokol.(DoublesData) -> Unit = {
            with(it) {
                DOUBLES(::list)
            }
        }

        override fun create() = DoublesData()
    }

    object StrictDoublesDataProtokolObject : ProtokolObject<DoublesData> {
        override val protokol: Protokol.(DoublesData) -> Unit = {
            with(it) {
                DOUBLES(::list, { size -> if (size == 0) throw IllegalArgumentException("size can't be 0") }) { value ->
                    if (value < 0) throw IllegalArgumentException("value can't be negative")
                }
            }
        }

        override fun create() = DoublesData()
    }

    @Test
    fun test() {
        fun assert(list: List<Double>, po: ProtokolObject<DoublesData>) {
            val bytes = ByteArrayProtokolCodec.encode(DoublesData(list), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(list, data.list)
        }

        assert(emptyList(), DoublesDataProtokolObject)
        assert(List(127) { Random.nextDouble() }, DoublesDataProtokolObject) // still one byte for list size
        assert(List(128) { Random.nextDouble() }, DoublesDataProtokolObject) // two bytes for list size

        assertFailsWith<IllegalArgumentException> { assert(emptyList(), StrictDoublesDataProtokolObject) }
        assert(listOf(PI, 2 * PI, 3 * PI), StrictDoublesDataProtokolObject)
        assertFailsWith<IllegalArgumentException> { assert(listOf(1.0, 0.0, -1.0 * PI),
            StrictDoublesDataProtokolObject) }
    }

    @Test
    fun testParseError() {
        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(DoublesData(emptyList()), DoublesDataProtokolObject),
                StrictDoublesDataProtokolObject
            )
        }

        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(DoublesData(listOf(1.0, 0.0, -1 * PI)), DoublesDataProtokolObject),
                StrictDoublesDataProtokolObject
            )
        }
    }

}