package com.electrit.protokol

import kotlin.math.PI
import kotlin.random.Random
import kotlin.test.*

class FloatsTest {

    class FloatsData(var list: List<Float> = emptyList())

    object FloatsDataProtokolObject : ProtokolObject<FloatsData> {
        override val protokol: Protokol.(FloatsData) -> Unit = {
            with(it) {
                FLOATS(::list)
            }
        }

        override fun create() = FloatsData()
    }

    object StrictFloatsDataProtokolObject : ProtokolObject<FloatsData> {
        override val protokol: Protokol.(FloatsData) -> Unit = {
            with(it) {
                FLOATS(::list, { size -> if (size == 0) throw IllegalArgumentException("size can't be 0") }) { value ->
                    if (value < 0f) throw IllegalArgumentException("value can't be negative")
                }
            }
        }

        override fun create() = FloatsData()
    }

    @Test
    fun test() {
        fun assert(list: List<Float>, po: ProtokolObject<FloatsData>) {
            val bytes = ByteArrayProtokolCodec.encode(FloatsData(list), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)

            assertEquals(list.size, data.list.size)
            if (list.isNotEmpty()) {
                for (i in list.indices) {
                    assertEquals(list[i], data.list[i], 0f)
                }
            }
        }

        assert(emptyList(), FloatsDataProtokolObject)
        assert(List(127) { Random.nextFloat() }, FloatsDataProtokolObject) // still one byte for list size
        assert(List(128) { Random.nextFloat() }, FloatsDataProtokolObject) // two bytes for list size

        assertFailsWith<IllegalArgumentException> { assert(emptyList(), StrictFloatsDataProtokolObject) }
        assert(listOf(PI.toFloat(), 2 * PI.toFloat(), 3 * PI.toFloat()), StrictFloatsDataProtokolObject)
        assertFailsWith<IllegalArgumentException> { assert(listOf(1f, 0f, -1 * PI.toFloat()),
            StrictFloatsDataProtokolObject) }
    }

    @Test
    fun testParseError() {
        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(FloatsData(emptyList()), FloatsDataProtokolObject),
                StrictFloatsDataProtokolObject
            )
        }

        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(FloatsData(listOf(1f, 0f, -1 * PI.toFloat())), FloatsDataProtokolObject),
                StrictFloatsDataProtokolObject
            )
        }
    }

}