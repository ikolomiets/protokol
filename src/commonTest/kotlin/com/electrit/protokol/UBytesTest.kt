package com.electrit.protokol

import kotlin.random.Random
import kotlin.random.nextUBytes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UBytesTest {

    class UBytesData(var list: List<UByte> = emptyList())

    object UBytesDataProtokolObject : ProtokolObject<UBytesData> {
        override val protokol: Protokol.(UBytesData) -> Unit = {
            with(it) {
                UBYTES(::list)
            }
        }

        override fun create() = UBytesData()
    }

    object StrictUBytesDataProtokolObject : ProtokolObject<UBytesData> {
        override val protokol: Protokol.(UBytesData) -> Unit = {
            with(it) {
                UBYTES(::list, { size -> if (size == 0) throw IllegalArgumentException("size can't be 0") }) { value ->
                    if (value.toInt() == 0) throw IllegalArgumentException("value can't be 0")
                }
            }
        }

        override fun create() = UBytesData()
    }

    @ExperimentalUnsignedTypes
    @Test
    fun test() {
        fun assert(list: List<UByte>, po: ProtokolObject<UBytesData>) {
            val bytes = ByteArrayProtokolCodec.encode(UBytesData(list), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(list, data.list)
        }

        assert(emptyList(), UBytesDataProtokolObject)
        assert(Random.nextUBytes(127).toList(), UBytesDataProtokolObject) // still one byte for list size
        assert(Random.nextUBytes(128).toList(), UBytesDataProtokolObject) // two bytes for list size

        assertFailsWith<IllegalArgumentException> { assert(emptyList(), StrictUBytesDataProtokolObject) }
        assert(listOf(1u, 2u, 3u, 4u), StrictUBytesDataProtokolObject)
        assertFailsWith<IllegalArgumentException> { assert(listOf(1u, 2u, 0u, 4u), StrictUBytesDataProtokolObject) }
    }

    @Test
    fun testParseError() {
        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(UBytesData(emptyList()), UBytesDataProtokolObject),
                StrictUBytesDataProtokolObject
            )
        }

        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(UBytesData(listOf(1u, 2u, 0u, 4u)), UBytesDataProtokolObject),
                StrictUBytesDataProtokolObject
            )
        }
    }

}