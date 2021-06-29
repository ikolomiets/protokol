package com.electrit.protokol

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LongsTest {

    class LongsData(var list: List<Long> = emptyList())

    object LongsDataProtokolObject : ProtokolObject<LongsData> {
        override val protokol: Protokol.(LongsData) -> Unit = {
            with(it) {
                LONGS(::list)
            }
        }

        override fun create() = LongsData()
    }

    object StrictLongsDataProtokolObject : ProtokolObject<LongsData> {
        override val protokol: Protokol.(LongsData) -> Unit = {
            with(it) {
                LONGS(::list, { size -> if (size == 0) throw IllegalArgumentException("size can't be 0") }) { value ->
                    if (value == 0L) throw IllegalArgumentException("value can't be 0")
                }
            }
        }

        override fun create() = LongsData()
    }

    @Test
    fun test() {
        fun assert(list: List<Long>, po: ProtokolObject<LongsData>) {
            val bytes = ByteArrayProtokolCodec.encode(LongsData(list), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(list, data.list)
        }

        assert(emptyList(), LongsDataProtokolObject)
        assert(List(127) { Random.nextLong() }, LongsDataProtokolObject) // still one byte for list size
        assert(List(128) { Random.nextLong() }, LongsDataProtokolObject) // two bytes for list size

        assertFailsWith<IllegalArgumentException> { assert(emptyList(), StrictLongsDataProtokolObject) }
        assert(listOf(1, 2, 3), StrictLongsDataProtokolObject)
        assertFailsWith<IllegalArgumentException> { assert(listOf(1, 0, 3), StrictLongsDataProtokolObject) }
    }

    @Test
    fun testParseError() {
        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(LongsData(emptyList()), LongsDataProtokolObject),
                StrictLongsDataProtokolObject
            )
        }

        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(LongsData(listOf(1, 0, 3)), LongsDataProtokolObject),
                StrictLongsDataProtokolObject
            )
        }
    }

}