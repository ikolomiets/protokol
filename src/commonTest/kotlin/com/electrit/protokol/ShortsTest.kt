package com.electrit.protokol

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ShortsTest {

    class ShortsData(var list: List<Short> = emptyList())

    object ShortsDataProtokolObject : ProtokolObject<ShortsData> {
        override val protokol: Protokol.(ShortsData) -> Unit = {
            with(it) {
                SHORTS(::list)
            }
        }

        override fun create() = ShortsData()
    }

    object StrictShortsDataProtokolObject : ProtokolObject<ShortsData> {
        override val protokol: Protokol.(ShortsData) -> Unit = {
            with(it) {
                SHORTS(::list, { size -> if (size == 0) throw IllegalArgumentException("size can't be 0") }) { value ->
                    if (value.toInt() == 0) throw IllegalArgumentException("value can't be 0")
                }
            }
        }

        override fun create() = ShortsData()
    }

    @Test
    fun test() {
        fun assert(list: List<Short>, po: ProtokolObject<ShortsData>) {
            val bytes = ByteArrayProtokolCodec.encode(ShortsData(list), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(list, data.list)
        }

        assert(emptyList(), ShortsDataProtokolObject)
        assert(List(127) { Random.nextInt(Short.MAX_VALUE + 1).toShort() }, ShortsDataProtokolObject) // still one byte for list size
        assert(List(128) { Random.nextInt(Short.MAX_VALUE + 1).toShort() }, ShortsDataProtokolObject) // two bytes for list size

        assertFailsWith<IllegalArgumentException> { assert(emptyList(), StrictShortsDataProtokolObject) }
        assert(listOf(1, 2, 3), StrictShortsDataProtokolObject)
        assertFailsWith<IllegalArgumentException> { assert(listOf(1, 0, 3), StrictShortsDataProtokolObject) }
    }

    @Test
    fun testParseError() {
        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(ShortsData(emptyList()), ShortsDataProtokolObject),
                StrictShortsDataProtokolObject
            )
        }

        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(ShortsData(listOf(1, 0, 3)), ShortsDataProtokolObject),
                StrictShortsDataProtokolObject
            )
        }
    }

}