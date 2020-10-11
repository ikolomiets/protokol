package com.electrit.protokol

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ByteArraysTest {

    class ByteArraysData(var list: List<ByteArray> = emptyList())

    object ByteArraysDataProtokolObject : ProtokolObject<ByteArraysData> {
        override fun use(value: ByteArraysData, p: Protokol) = with(p) {
            with(value) {
                BYTEARRAYS(::list)
            }
        }

        override fun create() = ByteArraysData()
    }

    object StrictByteArraysDataProtokolObject : ProtokolObject<ByteArraysData> {
        override fun use(value: ByteArraysData, p: Protokol) = with(p) {
            with(value) {
                BYTEARRAYS(::list, { size -> if (size == 0) throw IllegalArgumentException("size can't be 0") }) {
                    if (it.isEmpty()) throw IllegalArgumentException("value can't be empty")
                }
            }
        }

        override fun create() = ByteArraysData()
    }

    @Test
    fun test() {
        fun assert(list: List<ByteArray>, po: ProtokolObject<ByteArraysData>) {
            val bytes = ByteArrayProtokolCodec.encode(ByteArraysData(list), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertTrue(list.toTypedArray().contentDeepEquals(data.list.toTypedArray()))
        }

        assert(emptyList(), ByteArraysDataProtokolObject)
        assert(
            // still one byte for list size
            List(127) { Random.nextBytes(Random.nextInt(1, 10)) },
            ByteArraysDataProtokolObject
        )
        assert(
            // two bytes for list size
            List(128) { Random.nextBytes(Random.nextInt(1, 10)) },
            ByteArraysDataProtokolObject
        )

        assertFailsWith<IllegalArgumentException> { assert(emptyList(), StrictByteArraysDataProtokolObject) }
        assert(listOf(byteArrayOf(1), byteArrayOf(1, 2), byteArrayOf(1, 2, 3)), StrictByteArraysDataProtokolObject)
        assertFailsWith<IllegalArgumentException> {
            assert(listOf(byteArrayOf(1), ByteArray(0), byteArrayOf(1, 2)), StrictByteArraysDataProtokolObject)
        }
    }

    @Test
    fun testParseError() {
        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(ByteArraysData(emptyList()), ByteArraysDataProtokolObject),
                StrictByteArraysDataProtokolObject
            )
        }

        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(
                    ByteArraysData(listOf(byteArrayOf(1), ByteArray(0), byteArrayOf(1, 2))),
                    ByteArraysDataProtokolObject
                ),
                StrictByteArraysDataProtokolObject
            )
        }
    }

}