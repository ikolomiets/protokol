package com.electrit.protokol

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BitsetTest {

    data class BitsetData(
        var b0: Boolean = false,
        var b1: Boolean = false,
        var b2: Boolean = false,
        var b3: Boolean = false,
        var b4: Boolean = false,
        var b5: Boolean = false,
        var b6: Boolean = false,
        var b7: Boolean = false
    )

    data class SparseBitsetData(
        var b1: Boolean = false,
        var b3: Boolean = false,
        var b5: Boolean = false,
        var b7: Boolean = false
    )

    object BitsetDataProtokolObject : ProtokolObject<BitsetData> {
        override val protokol: Protokol.(BitsetData) -> Unit = {
            with(it) {
                BITSET8(b0 = ::b0, b1 = ::b1, b2 = ::b2, b3 = ::b3, b4 = ::b4, b5 = ::b5, b6 = ::b6, b7 = ::b7)
            }
        }

        override fun create() = BitsetData()
    }

    object StrictBitsetDataProtokolObject : ProtokolObject<BitsetData> {
        override val protokol: Protokol.(BitsetData) -> Unit = {
            with(it) {
                BITSET8(::b0, ::b1, ::b2, ::b3, ::b4, ::b5, ::b6, ::b7) { _, b1, _, b3, _, b5, _, b7 ->
                    if (b1 && b3 && b5 && b7)
                        throw IllegalArgumentException("b1 && b3 && b5 && b7 can't all be true")
                }
            }
        }

        override fun create() = BitsetData()
    }

    object SparseBitsetDataProtokolObject : ProtokolObject<SparseBitsetData> {
        override val protokol: Protokol.(SparseBitsetData) -> Unit = {
            with(it) {
                BITSET8(b1 = ::b1, b3 = ::b3, b5 = ::b5, b7 = ::b7)
            }
        }

        override fun create() = SparseBitsetData()
    }

    object StrictSparseBitsetDataProtokolObject : ProtokolObject<SparseBitsetData> {
        override val protokol: Protokol.(SparseBitsetData) -> Unit = {
            with(it) {
                BITSET8(b1 = ::b1, b3 = ::b3, b5 = ::b5, b7 = ::b7) { _, b1, _, b3, _, b5, _, b7 ->
                    if (b1 && b3 && b5 && b7)
                        throw IllegalArgumentException("b1 && b3 && b5 && b7 can't all be true")
                }
            }
        }

        override fun create() = SparseBitsetData()
    }

    @Test
    fun test() {
        fun <T> assert(bitset: T, po: ProtokolObject<T>) {
            val bytes = ByteArrayProtokolCodec.encode(bitset, po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(bitset, data)
        }

        for (byte in 0..255) {
            val bit0 = byte and 0b00000001 > 0
            val bit1 = byte and 0b00000010 > 0
            val bit2 = byte and 0b00000100 > 0
            val bit3 = byte and 0b00001000 > 0
            val bit4 = byte and 0b00010000 > 0
            val bit5 = byte and 0b00100000 > 0
            val bit6 = byte and 0b01000000 > 0
            val bit7 = byte and 0b10000000 > 0

            val bitset = BitsetData(bit0, bit1, bit2, bit3, bit4, bit5, bit6, bit7)
            val sparseBitset = SparseBitsetData(bit1, bit3, bit5, bit7)
            assert(bitset, BitsetDataProtokolObject)
            assert(sparseBitset, SparseBitsetDataProtokolObject)

            if (bit1 && bit3 && bit5 && bit7) {
                assertFailsWith<IllegalArgumentException> { assert(bitset, StrictBitsetDataProtokolObject) }
                assertFailsWith<IllegalArgumentException> { assert(sparseBitset, StrictSparseBitsetDataProtokolObject) }
            } else {
                assert(bitset, StrictBitsetDataProtokolObject)
                assert(sparseBitset, StrictSparseBitsetDataProtokolObject)
            }
        }
    }

}