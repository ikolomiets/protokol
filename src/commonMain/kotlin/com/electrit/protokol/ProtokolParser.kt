package com.electrit.protokol

import kotlin.reflect.KMutableProperty0

abstract class ProtokolParser : Protokol {

    abstract fun parseBYTE(): Byte

    abstract fun parseBYTEARRAY(): ByteArray

    private fun parseSTRING(): String = parseBYTEARRAY().decodeToString()

    private fun parseBOOLEAN(): Boolean = parseBYTE().toInt() != 0

    private fun parseSHORT(): Short =
        (((parseBYTE().toInt() and 0xff) shl 8) or (parseBYTE().toInt() and 0xff)).toShort()

    private fun parseINT(): Int =
        ((parseBYTE().toInt() and 0xff) shl 24) or
                ((parseBYTE().toInt() and 0xff) shl 16) or
                ((parseBYTE().toInt() and 0xff) shl 8) or
                (parseBYTE().toInt() and 0xff)

    private fun parseINT64(): Long =
        ((parseBYTE().toLong() and 0xffL) shl 56) or
                ((parseBYTE().toLong() and 0xffL) shl 48) or
                ((parseBYTE().toLong() and 0xffL) shl 40) or
                ((parseBYTE().toLong() and 0xffL) shl 32) or
                ((parseBYTE().toLong() and 0xffL) shl 24) or
                ((parseBYTE().toLong() and 0xffL) shl 16) or
                ((parseBYTE().toLong() and 0xffL) shl 8) or
                (parseBYTE().toLong() and 0xffL)

    private fun <E : Enum<E>> parseENUM8(values: Array<E>): E {
        require(values.size <= 256) { "ENUM8 allows for up to 256 values, actual: ${values.size}" }
        return values[parseBYTE().toInt() and 0xff]
    }

    private fun <E : Enum<E>> parseENUM16(values: Array<E>): E {
        require(values.size <= 65536) { "ENUM16 allows for up to 65536 values, actual: ${values.size}" }
        return values[parseSHORT().toInt() and 0b00000000000000001111111111111111]
    }

    private fun <T> parseOBJECT(po: ProtokolObject<T>): T? {
        val marker = parseBYTE()
        return if (marker != 0.toByte()) {
            val value = po.create()
            po.use(value, this)
            value
        } else {
            null
        }
    }

    fun parseSize(): Int {
        val b0 = parseBYTE()
        return when ((b0.toInt() and 0xff) shr 6) {
            0b10 -> {
                val b1 = parseBYTE()
                ((b0.toInt() and 0b00111111) shl 8) or (b1.toInt() and 0xff)
            }
            0b11 -> {
                val b1 = parseBYTE()
                val b2 = parseBYTE()
                val b3 = parseBYTE()
                return ((b0.toInt() and 0b00111111) shl 24) or
                        ((b1.toInt() and 0xff) shl 16) or
                        ((b2.toInt() and 0xff) shl 8) or
                        (b3.toInt() and 0xff)
            }
            else -> b0.toInt()
        }
    }

    private fun <T> validateAndSet(prop: KMutableProperty0<T>, validator: (T) -> Unit, parseValue: () -> T) {
        val value = parseValue()
        validator(value)
        prop.set(value)
    }

    private fun <T> parseList(
        prop: KMutableProperty0<List<T>>,
        sizeChecker: (Int) -> Unit,
        validator: (T) -> Unit,
        parseElement: () -> T
    ) {
        val size = parseSize()
        sizeChecker(size)
        val list = List(size) {
            val element = parseElement()
            validator(element)
            element
        }
        prop.set(list)
    }

    override fun BYTE(prop: KMutableProperty0<Byte>, validator: (Byte) -> Unit) =
        validateAndSet(prop, validator) { parseBYTE() }

    override fun BYTES(
        prop: KMutableProperty0<List<Byte>>,
        sizeChecker: (Int) -> Unit,
        validator: (Byte) -> Unit
    ) = parseList(prop, sizeChecker, validator) { parseBYTE() }

    override fun BYTEARRAY(prop: KMutableProperty0<ByteArray>, validator: (ByteArray) -> Unit) =
        validateAndSet(prop, validator) { parseBYTEARRAY() }

    override fun BYTEARRAYS(
        prop: KMutableProperty0<List<ByteArray>>,
        sizeChecker: (Int) -> Unit,
        validator: (ByteArray) -> Unit
    ) = parseList(prop, sizeChecker, validator) { parseBYTEARRAY() }

    override fun STRING(prop: KMutableProperty0<String>, validator: (String) -> Unit) =
        validateAndSet(prop, validator) { parseSTRING() }

    override fun STRINGS(
        prop: KMutableProperty0<List<String>>,
        sizeChecker: (Int) -> Unit,
        validator: (String) -> Unit
    ) = parseList(prop, sizeChecker, validator) { parseSTRING() }

    override fun BOOLEAN(prop: KMutableProperty0<Boolean>, validator: (Boolean) -> Unit) =
        validateAndSet(prop, validator) { parseBOOLEAN() }

    override fun BOOLEANS(
        prop: KMutableProperty0<List<Boolean>>,
        sizeChecker: (Int) -> Unit,
        validator: (Boolean) -> Unit
    ) = parseList(prop, sizeChecker, validator) { parseBOOLEAN() }

    override fun SHORT(prop: KMutableProperty0<Short>, validator: (Short) -> Unit) =
        validateAndSet(prop, validator) { parseSHORT() }

    override fun SHORTS(
        prop: KMutableProperty0<List<Short>>,
        sizeChecker: (Int) -> Unit,
        validator: (Short) -> Unit
    ) = parseList(prop, sizeChecker, validator) { parseSHORT() }

    override fun INT(prop: KMutableProperty0<Int>, validator: (Int) -> Unit) =
        validateAndSet(prop, validator) { parseINT() }

    override fun INTS(
        prop: KMutableProperty0<List<Int>>,
        sizeChecker: (Int) -> Unit,
        validator: (Int) -> Unit
    ) = parseList(prop, sizeChecker, validator) { parseINT() }

    override fun LONG(prop: KMutableProperty0<Long>, validator: (Long) -> Unit) =
        validateAndSet(prop, validator) { parseINT64() }

    override fun LONGS(
        prop: KMutableProperty0<List<Long>>,
        sizeChecker: (Int) -> Unit,
        validator: (Long) -> Unit
    ) = parseList(prop, sizeChecker, validator) { parseINT64() }

    override fun <E : Enum<E>> ENUM8(prop: KMutableProperty0<E>, values: Array<E>, validator: (E) -> Unit) =
        validateAndSet(prop, validator) { parseENUM8(values) }

    override fun <E : Enum<E>> ENUM8S(
        prop: KMutableProperty0<List<E>>,
        values: Array<E>,
        sizeChecker: (Int) -> Unit,
        validator: (E) -> Unit
    ) = parseList(prop, sizeChecker, validator) { parseENUM8(values) }

    override fun <E : Enum<E>> ENUM16(prop: KMutableProperty0<E>, values: Array<E>, validator: (E) -> Unit) =
        validateAndSet(prop, validator) { parseENUM16(values) }

    override fun <E : Enum<E>> ENUM16S(
        prop: KMutableProperty0<List<E>>,
        values: Array<E>,
        sizeChecker: (Int) -> Unit,
        validator: (E) -> Unit
    ) = parseList(prop, sizeChecker, validator) { parseENUM16(values) }

    override fun <T> OBJECT(prop: KMutableProperty0<T?>, po: ProtokolObject<T>, validator: T?.() -> Unit) =
        validateAndSet(prop, validator) { parseOBJECT(po) }

    override fun <T> OBJECTS(
        prop: KMutableProperty0<List<T>>,
        po: ProtokolObject<T>,
        sizeChecker: (Int) -> Unit,
        validator: T.() -> Unit
    ) = parseList(prop, sizeChecker, validator) { parseOBJECT(po) ?: throw NullPointerException() }

    override fun BITSET8(
        b0: KMutableProperty0<Boolean>?,
        b1: KMutableProperty0<Boolean>?,
        b2: KMutableProperty0<Boolean>?,
        b3: KMutableProperty0<Boolean>?,
        b4: KMutableProperty0<Boolean>?,
        b5: KMutableProperty0<Boolean>?,
        b6: KMutableProperty0<Boolean>?,
        b7: KMutableProperty0<Boolean>?,
        validator: (
            b0: Boolean,
            b1: Boolean,
            b2: Boolean,
            b3: Boolean,
            b4: Boolean,
            b5: Boolean,
            b6: Boolean,
            b7: Boolean
        ) -> Unit
    ) {
        val byte = parseBYTE().toInt()

        val bit0 = byte and 0b00000001 > 0
        val bit1 = byte and 0b00000010 > 0
        val bit2 = byte and 0b00000100 > 0
        val bit3 = byte and 0b00001000 > 0
        val bit4 = byte and 0b00010000 > 0
        val bit5 = byte and 0b00100000 > 0
        val bit6 = byte and 0b01000000 > 0
        val bit7 = byte and 0b10000000 > 0

        validator(bit0, bit1, bit2, bit3, bit4, bit5, bit6, bit7)

        b0?.set(bit0)
        b1?.set(bit1)
        b2?.set(bit2)
        b3?.set(bit3)
        b4?.set(bit4)
        b5?.set(bit5)
        b6?.set(bit6)
        b7?.set(bit7)
    }

}

