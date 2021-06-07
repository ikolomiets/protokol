package com.electrit.protokol

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BytesTest {

    class BytesData(var list: List<Byte> = emptyList())

    object BytesDataProtokolObject : ProtokolObject<BytesData> {
        override val protokol: Protokol.(BytesData) -> Unit = {
            with(it) {
                BYTES(::list)
            }
        }

        override fun create() = BytesData()
    }

    object StrictBytesDataProtokolObject : ProtokolObject<BytesData> {
        override val protokol: Protokol.(BytesData) -> Unit = {
            with(it) {
                BYTES(::list, { size -> if (size == 0) throw IllegalArgumentException("size can't be 0") }) { value ->
                    if (value.toInt() == 0) throw IllegalArgumentException("value can't be 0")
                }
            }
        }

        override fun create() = BytesData()
    }

    @Test
    fun test() {
        fun assert(list: List<Byte>, po: ProtokolObject<BytesData>) {
            val bytes = ByteArrayProtokolCodec.encode(BytesData(list), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(list, data.list)
        }

        assert(emptyList(), BytesDataProtokolObject)
        assert(Random.nextBytes(127).toList(), BytesDataProtokolObject) // still one byte for list size
        assert(Random.nextBytes(128).toList(), BytesDataProtokolObject) // two bytes for list size

        assertFailsWith<IllegalArgumentException> { assert(emptyList(), StrictBytesDataProtokolObject) }
        assert(listOf(1, 2, 3, 4), StrictBytesDataProtokolObject)
        assertFailsWith<IllegalArgumentException> { assert(listOf(1, 2, 0, 4), StrictBytesDataProtokolObject) }
    }

    @Test
    fun testParseError() {
        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(BytesData(emptyList()), BytesDataProtokolObject),
                StrictBytesDataProtokolObject
            )
        }

        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(BytesData(listOf(1,2, 0, 4)), BytesDataProtokolObject),
                StrictBytesDataProtokolObject
            )
        }
    }

}