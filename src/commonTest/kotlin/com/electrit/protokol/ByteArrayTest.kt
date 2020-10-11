package com.electrit.protokol

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ByteArrayTest {

    class ByteArrayData(var bytes: ByteArray = ByteArray(0))

    object ByteArrayDataProtokolObject : ProtokolObject<ByteArrayData> {
        override fun use(value: ByteArrayData, p: Protokol) = with(p) {
            with(value) {
                BYTEARRAY(::bytes)
            }
        }

        override fun create() = ByteArrayData()
    }

    object StrictByteArrayDataProtokolObject : ProtokolObject<ByteArrayData> {
        override fun use(value: ByteArrayData, p: Protokol) = with(p) {
            with(value) {
                BYTEARRAY(::bytes) {
                    if (it.size > 1024) throw IllegalArgumentException("bytes size can't be more than 1024")
                }
            }
        }

        override fun create() = ByteArrayData()
    }

    @Test
    fun test() {
        fun assert(ba: ByteArray, po: ProtokolObject<ByteArrayData>) {
            val bytes = ByteArrayProtokolCodec.encode(ByteArrayData(ba), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertTrue(ba.contentEquals(data.bytes))
        }

        assert(ByteArray(0), ByteArrayDataProtokolObject)
        assert(Random.nextBytes(Random.nextInt(1, 128)), ByteArrayDataProtokolObject)
        assert(Random.nextBytes(Random.nextInt(128, 1024)), ByteArrayDataProtokolObject)

        assert(Random.nextBytes(Random.nextInt(128, 1024)), StrictByteArrayDataProtokolObject)
        assertFailsWith<IllegalArgumentException> { assert(Random.nextBytes(1025), StrictByteArrayDataProtokolObject) }
    }

    @Test
    fun testParseError() {
        val bytes = ByteArrayProtokolCodec.encode(ByteArrayData(Random.nextBytes(1025)), ByteArrayDataProtokolObject)
        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                bytes,
                StrictByteArrayDataProtokolObject
            )
        }
    }

}