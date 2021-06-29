package com.electrit.protokol

import kotlin.random.Random
import kotlin.random.nextULong
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ULongsTest {

    class ULongsData(var list: List<ULong> = emptyList())

    object ULongsDataProtokolObject : ProtokolObject<ULongsData> {
        override val protokol: Protokol.(ULongsData) -> Unit = {
            with(it) {
                ULONGS(::list)
            }
        }

        override fun create() = ULongsData()
    }

    object StrictULongsDataProtokolObject : ProtokolObject<ULongsData> {
        override val protokol: Protokol.(ULongsData) -> Unit = {
            with(it) {
                ULONGS(::list, { size -> if (size == 0) throw IllegalArgumentException("size can't be 0") }) { value ->
                    if (value == ULong.MIN_VALUE) throw IllegalArgumentException("value can't be 0")
                }
            }
        }

        override fun create() = ULongsData()
    }

    @Test
    fun test() {
        fun assert(list: List<ULong>, po: ProtokolObject<ULongsData>) {
            val bytes = ByteArrayProtokolCodec.encode(ULongsData(list), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(list, data.list)
        }

        assert(emptyList(), ULongsDataProtokolObject)
        assert(List(127) { Random.nextULong() }, ULongsDataProtokolObject) // still one byte for list size
        assert(List(128) { Random.nextULong() }, ULongsDataProtokolObject) // two bytes for list size

        assertFailsWith<IllegalArgumentException> { assert(emptyList(), StrictULongsDataProtokolObject) }
        assert(listOf(1u, 2u, 3u), StrictULongsDataProtokolObject)
        assertFailsWith<IllegalArgumentException> { assert(listOf(1u, 0u, 3u), StrictULongsDataProtokolObject) }
    }

    @Test
    fun testParseError() {
        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(ULongsData(emptyList()), ULongsDataProtokolObject),
                StrictULongsDataProtokolObject
            )
        }

        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(ULongsData(listOf(1u, 0u, 3u)), ULongsDataProtokolObject),
                StrictULongsDataProtokolObject
            )
        }
    }

}