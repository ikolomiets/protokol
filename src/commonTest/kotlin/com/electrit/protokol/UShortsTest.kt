package com.electrit.protokol

import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UShortsTest {

    class UShortsData(var list: List<UShort> = emptyList())

    object UShortsDataProtokolObject : ProtokolObject<UShortsData> {
        override val protokol: Protokol.(UShortsData) -> Unit = {
            with(it) {
                USHORTS(::list)
            }
        }

        override fun create() = UShortsData()
    }

    object StrictUShortsDataProtokolObject : ProtokolObject<UShortsData> {
        override val protokol: Protokol.(UShortsData) -> Unit = {
            with(it) {
                USHORTS(::list, { size -> if (size == 0) throw IllegalArgumentException("size can't be 0") }) { value ->
                    if (value.toInt() == 0) throw IllegalArgumentException("value can't be 0")
                }
            }
        }

        override fun create() = UShortsData()
    }

    @Test
    fun test() {
        fun assert(list: List<UShort>, po: ProtokolObject<UShortsData>) {
            val bytes = ByteArrayProtokolCodec.encode(UShortsData(list), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(list, data.list)
        }

        assert(emptyList(), UShortsDataProtokolObject)
        assert(List(127) { Random.nextUInt().toUShort() }, UShortsDataProtokolObject) // still one byte for list size
        assert(List(128) { Random.nextUInt().toUShort() }, UShortsDataProtokolObject) // two bytes for list size

        assertFailsWith<IllegalArgumentException> { assert(emptyList(), StrictUShortsDataProtokolObject) }
        assert(listOf(1u, 2u, 3u), StrictUShortsDataProtokolObject)
        assertFailsWith<IllegalArgumentException> { assert(listOf(1u, 0u, 3u), StrictUShortsDataProtokolObject) }
    }

    @Test
    fun testParseError() {
        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(UShortsData(emptyList()), UShortsDataProtokolObject),
                StrictUShortsDataProtokolObject
            )
        }

        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(UShortsData(listOf(1u, 0u, 3u)), UShortsDataProtokolObject),
                StrictUShortsDataProtokolObject
            )
        }
    }

}