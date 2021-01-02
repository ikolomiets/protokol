package com.electrit.protokol

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BitsetTest {

    @Suppress("EqualsOrHashCode")
    class BitsetData(
        var b0: Boolean = false,
        var b1: Boolean = false,
        var b2: Boolean = false,
        var b3: Boolean = false,
        var b4: Boolean = false,
        var b5: Boolean = false,
        var b6: Boolean = false,
        var b7: Boolean = false
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as BitsetData

            if (b0 != other.b0) return false
            if (b1 != other.b1) return false
            if (b2 != other.b2) return false
            if (b3 != other.b3) return false
            if (b4 != other.b4) return false
            if (b5 != other.b5) return false
            if (b6 != other.b6) return false
            if (b7 != other.b7) return false

            return true
        }
    }

    object BitsetDataProtokolObject : ProtokolObject<BitsetData> {
        override fun use(value: BitsetData, p: Protokol) = with(p) {
            with(value) {
                BITSET8(b1 = ::b1, b3 = ::b3, b5 = ::b5, b7 = ::b7)
            }
        }

        override fun create() = BitsetData()
    }

    object StrictBitsetDataProtokolObject : ProtokolObject<BitsetData> {
        override fun use(value: BitsetData, p: Protokol) = with(p) {
            with(value) {
                BITSET8(b1 = ::b1, b3 = ::b3, b5 = ::b5, b7 = ::b7) { _, b1, _, b3, _, b5, _, b7 ->
                    if (b1 && b3 && b5 && b7)
                        throw IllegalArgumentException("b1 && b3 && b5 && b7 can't all be true")
                }
            }
        }

        override fun create() = BitsetData()
    }

    @Test
    fun test() {
        fun assert(bitset: BitsetData, po: ProtokolObject<BitsetData>) {
            val bytes = ByteArrayProtokolCodec.encode(bitset, po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(bitset, data)
        }

        for (byte in 0..255) {
            val bit1 = byte and 0b00000010 > 0
            val bit3 = byte and 0b00001000 > 0
            val bit5 = byte and 0b00100000 > 0
            val bit7 = byte and 0b10000000 > 0

            val bitset = BitsetData(b1 = bit1, b3 = bit3, b5 = bit5, b7 = bit7)
            assert(bitset, BitsetDataProtokolObject)

            if (bit1 && bit3 && bit5 && bit7) {
                assertFailsWith<IllegalArgumentException> { assert(bitset, StrictBitsetDataProtokolObject) }
            } else {
                assert(bitset, StrictBitsetDataProtokolObject)
            }
        }
    }

}