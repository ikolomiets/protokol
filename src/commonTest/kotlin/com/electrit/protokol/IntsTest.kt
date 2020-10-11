package com.electrit.protokol

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class IntsTest {

    class IntsData(var list: List<Int> = emptyList())

    object IntsDataProtokolObject : ProtokolObject<IntsData> {
        override fun use(value: IntsData, p: Protokol) = with(p) {
            with(value) {
                INTS(::list)
            }
        }

        override fun create() = IntsData()
    }

    object StrictIntsDataProtokolObject : ProtokolObject<IntsData> {
        override fun use(value: IntsData, p: Protokol) = with(p) {
            with(value) {
                INTS(::list, { size -> if (size == 0) throw IllegalArgumentException("size can't be 0") }) {
                    if (it == 0) throw IllegalArgumentException("value can't be 0")
                }
            }
        }

        override fun create() = IntsData()
    }

    @Test
    fun test() {
        fun assert(list: List<Int>, po: ProtokolObject<IntsData>) {
            val bytes = ByteArrayProtokolCodec.encode(IntsData(list), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(list, data.list)
        }

        assert(emptyList(), IntsDataProtokolObject)
        assert(List(127) { Random.nextInt() }, IntsDataProtokolObject) // still one byte for list size
        assert(List(128) { Random.nextInt() }, IntsDataProtokolObject) // two bytes for list size

        assertFailsWith<IllegalArgumentException> { assert(emptyList(), StrictIntsDataProtokolObject) }
        assert(listOf(1, 2, 3), StrictIntsDataProtokolObject)
        assertFailsWith<IllegalArgumentException> { assert(listOf(1, 0, 3), StrictIntsDataProtokolObject) }
    }

    @Test
    fun testParseError() {
        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(IntsData(emptyList()), IntsDataProtokolObject),
                StrictIntsDataProtokolObject
            )
        }

        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(IntsData(listOf(1, 0, 3)), IntsDataProtokolObject),
                StrictIntsDataProtokolObject
            )
        }
    }

}