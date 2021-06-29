package com.electrit.protokol

import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UIntsTest {

    class UIntsData(var list: List<UInt> = emptyList())

    object UIntsDataProtokolObject : ProtokolObject<UIntsData> {
        override val protokol: Protokol.(UIntsData) -> Unit = {
            with(it) {
                UINTS(::list)
            }
        }

        override fun create() = UIntsData()
    }

    object StrictUIntsDataProtokolObject : ProtokolObject<UIntsData> {
        override val protokol: Protokol.(UIntsData) -> Unit = {
            with(it) {
                UINTS(::list, { size -> if (size == 0) throw IllegalArgumentException("size can't be 0") }) { value ->
                    if (value == 0u) throw IllegalArgumentException("value can't be 0")
                }
            }
        }

        override fun create() = UIntsData()
    }

    @Test
    fun test() {
        fun assert(list: List<UInt>, po: ProtokolObject<UIntsData>) {
            val bytes = ByteArrayProtokolCodec.encode(UIntsData(list), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(list, data.list)
        }

        assert(emptyList(), UIntsDataProtokolObject)
        assert(List(127) { Random.nextUInt() }, UIntsDataProtokolObject) // still one byte for list size
        assert(List(128) { Random.nextUInt() }, UIntsDataProtokolObject) // two bytes for list size

        assertFailsWith<IllegalArgumentException> { assert(emptyList(), StrictUIntsDataProtokolObject) }
        assert(listOf(1u, 2u, 3u), StrictUIntsDataProtokolObject)
        assertFailsWith<IllegalArgumentException> { assert(listOf(1u, 0u, 3u), StrictUIntsDataProtokolObject) }
    }

    @Test
    fun testParseError() {
        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(UIntsData(emptyList()), UIntsDataProtokolObject),
                StrictUIntsDataProtokolObject
            )
        }

        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(UIntsData(listOf(1u, 0u, 3u)), UIntsDataProtokolObject),
                StrictUIntsDataProtokolObject
            )
        }
    }

}