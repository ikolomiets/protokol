package com.electrit.protokol

abstract class StandardProtokolParser : ProtokolParser() {

    override fun parseUBYTE(): UByte = parseBYTE().toUByte()

    override fun parseSTRING(): String = parseBYTEARRAY().decodeToString()

    override fun parseBOOLEAN(): Boolean = parseBYTE().toInt() != 0

    override fun parseSHORT(): Short =
        (((parseBYTE().toInt() and 0xff) shl 8) or (parseBYTE().toInt() and 0xff)).toShort()

    override fun parseUSHORT(): UShort = parseSHORT().toUShort()

    override fun parseINT(): Int =
        ((parseBYTE().toInt() and 0xff) shl 24) or
                ((parseBYTE().toInt() and 0xff) shl 16) or
                ((parseBYTE().toInt() and 0xff) shl 8) or
                (parseBYTE().toInt() and 0xff)

    override fun parseUINT(): UInt = parseINT().toUInt()

    override fun parseLONG(): Long =
        ((parseBYTE().toLong() and 0xffL) shl 56) or
                ((parseBYTE().toLong() and 0xffL) shl 48) or
                ((parseBYTE().toLong() and 0xffL) shl 40) or
                ((parseBYTE().toLong() and 0xffL) shl 32) or
                ((parseBYTE().toLong() and 0xffL) shl 24) or
                ((parseBYTE().toLong() and 0xffL) shl 16) or
                ((parseBYTE().toLong() and 0xffL) shl 8) or
                (parseBYTE().toLong() and 0xffL)

    override fun parseULONG(): ULong = parseLONG().toULong()

    override fun parseFLOAT(): Float = Float.fromBits(parseINT())

    override fun parseDOUBLE(): Double = Double.fromBits(parseLONG())

    override fun <E : Enum<E>> parseENUM8(values: Array<E>): E {
        require(values.size <= 256) { "ENUM8 allows for up to 256 values, actual: ${values.size}" }
        return values[parseBYTE().toInt() and 0xff]
    }

    override fun <E : Enum<E>> parseENUM16(values: Array<E>): E {
        require(values.size <= 65536) { "ENUM16 allows for up to 65536 values, actual: ${values.size}" }
        return values[parseSHORT().toInt() and 0b00000000000000001111111111111111]
    }

    override fun <T> parseOBJECT(po: ProtokolObject<T>): T? {
        val marker = parseBYTE()
        return if (marker != 0.toByte()) {
            val value = po.create()
            po.protokol(this, value)
            value
        } else {
            null
        }
    }

    override fun parseSize(): Int {
        val b0 = parseBYTE()
        return when {
            b0 < 0 -> {
                val b1 = parseBYTE()
                val b2 = parseBYTE()
                val b3 = parseBYTE()
                return ((b0.toInt() and 0b01111111) shl 24) or
                        ((b1.toInt() and 0xff) shl 16) or
                        ((b2.toInt() and 0xff) shl 8) or
                        (b3.toInt() and 0xff)
            }
            else -> b0.toInt()
        }
    }

    override fun parseBITSET8(): List<Boolean> {
        val byte = parseBYTE().toInt()

        val bit0 = byte and 0b00000001 > 0
        val bit1 = byte and 0b00000010 > 0
        val bit2 = byte and 0b00000100 > 0
        val bit3 = byte and 0b00001000 > 0
        val bit4 = byte and 0b00010000 > 0
        val bit5 = byte and 0b00100000 > 0
        val bit6 = byte and 0b01000000 > 0
        val bit7 = byte and 0b10000000 > 0

        return listOf(bit0, bit1, bit2, bit3, bit4, bit5, bit6, bit7)
    }

}

