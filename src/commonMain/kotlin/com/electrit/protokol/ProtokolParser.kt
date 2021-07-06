package com.electrit.protokol

import kotlin.reflect.KMutableProperty0

abstract class ProtokolParser : Protokol {

    protected abstract fun parseBYTE(): Byte

    protected abstract fun parseUBYTE(): UByte

    protected abstract fun parseBYTEARRAY(): ByteArray

    protected abstract fun parseSTRING(): String

    protected abstract fun parseBOOLEAN(): Boolean

    protected abstract fun parseSHORT(): Short

    protected abstract fun parseUSHORT(): UShort

    protected abstract fun parseINT(): Int

    protected abstract fun parseUINT(): UInt

    protected abstract fun parseLONG(): Long

    protected abstract fun parseULONG(): ULong

    protected abstract fun parseFLOAT(): Float

    protected abstract fun parseDOUBLE(): Double

    protected abstract fun <E : Enum<E>> parseENUM8(values: Array<E>): E

    protected abstract fun <E : Enum<E>> parseENUM16(values: Array<E>): E

    protected abstract fun <T> parseOBJECT(po: ProtokolObject<T>): T?

    protected abstract fun parseSize(): Int

    protected abstract fun parseBITSET8(): List<Boolean>

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

    final override fun BYTE(prop: KMutableProperty0<Byte>, validator: (Byte) -> Unit) =
        validateAndSet(prop, validator) { parseBYTE() }

    final override fun BYTES(
        prop: KMutableProperty0<List<Byte>>,
        sizeChecker: (Int) -> Unit,
        validator: (Byte) -> Unit
    ) = parseList(prop, sizeChecker, validator) { parseBYTE() }

    final override fun UBYTE(prop: KMutableProperty0<UByte>, validator: (UByte) -> Unit): Unit =
        validateAndSet(prop, validator) { parseUBYTE() }

    final override fun UBYTES(
        prop: KMutableProperty0<List<UByte>>,
        sizeChecker: (Int) -> Unit,
        validator: (UByte) -> Unit
    ): Unit = parseList(prop, sizeChecker, validator) { parseUBYTE() }

    final override fun BYTEARRAY(prop: KMutableProperty0<ByteArray>, validator: (ByteArray) -> Unit) =
        validateAndSet(prop, validator) { parseBYTEARRAY() }

    final override fun BYTEARRAYS(
        prop: KMutableProperty0<List<ByteArray>>,
        sizeChecker: (Int) -> Unit,
        validator: (ByteArray) -> Unit
    ) = parseList(prop, sizeChecker, validator) { parseBYTEARRAY() }

    final override fun STRING(prop: KMutableProperty0<String>, validator: (String) -> Unit) =
        validateAndSet(prop, validator) { parseSTRING() }

    final override fun STRINGS(
        prop: KMutableProperty0<List<String>>,
        sizeChecker: (Int) -> Unit,
        validator: (String) -> Unit
    ) = parseList(prop, sizeChecker, validator) { parseSTRING() }

    final override fun BOOLEAN(prop: KMutableProperty0<Boolean>, validator: (Boolean) -> Unit) =
        validateAndSet(prop, validator) { parseBOOLEAN() }

    final override fun BOOLEANS(
        prop: KMutableProperty0<List<Boolean>>,
        sizeChecker: (Int) -> Unit,
        validator: (Boolean) -> Unit
    ) = parseList(prop, sizeChecker, validator) { parseBOOLEAN() }

    final override fun SHORT(prop: KMutableProperty0<Short>, validator: (Short) -> Unit) =
        validateAndSet(prop, validator) { parseSHORT() }

    final override fun SHORTS(
        prop: KMutableProperty0<List<Short>>,
        sizeChecker: (Int) -> Unit,
        validator: (Short) -> Unit
    ) = parseList(prop, sizeChecker, validator) { parseSHORT() }

    final override fun USHORT(prop: KMutableProperty0<UShort>, validator: (UShort) -> Unit): Unit =
        validateAndSet(prop, validator) { parseUSHORT() }

    final override fun USHORTS(
        prop: KMutableProperty0<List<UShort>>,
        sizeChecker: (Int) -> Unit,
        validator: (UShort) -> Unit
    ): Unit = parseList(prop, sizeChecker, validator) { parseUSHORT() }

    final override fun INT(prop: KMutableProperty0<Int>, validator: (Int) -> Unit) =
        validateAndSet(prop, validator) { parseINT() }

    final override fun INTS(
        prop: KMutableProperty0<List<Int>>,
        sizeChecker: (Int) -> Unit,
        validator: (Int) -> Unit
    ) = parseList(prop, sizeChecker, validator) { parseINT() }

    final override fun UINT(prop: KMutableProperty0<UInt>, validator: (UInt) -> Unit): Unit =
        validateAndSet(prop, validator) { parseUINT() }

    final override fun UINTS(
        prop: KMutableProperty0<List<UInt>>,
        sizeChecker: (Int) -> Unit,
        validator: (UInt) -> Unit
    ): Unit = parseList(prop, sizeChecker, validator) { parseUINT() }

    final override fun LONG(prop: KMutableProperty0<Long>, validator: (Long) -> Unit) =
        validateAndSet(prop, validator) { parseLONG() }

    final override fun LONGS(
        prop: KMutableProperty0<List<Long>>,
        sizeChecker: (Int) -> Unit,
        validator: (Long) -> Unit
    ) = parseList(prop, sizeChecker, validator) { parseLONG() }

    final override fun ULONG(prop: KMutableProperty0<ULong>, validator: (ULong) -> Unit): Unit =
        validateAndSet(prop, validator) { parseULONG() }

    final override fun ULONGS(
        prop: KMutableProperty0<List<ULong>>,
        sizeChecker: (Int) -> Unit,
        validator: (ULong) -> Unit
    ): Unit = parseList(prop, sizeChecker, validator) { parseULONG() }

    final override fun FLOAT(prop: KMutableProperty0<Float>, validator: (Float) -> Unit) =
        validateAndSet(prop, validator) { parseFLOAT() }

    final override fun FLOATS(prop: KMutableProperty0<List<Float>>, sizeChecker: (Int) -> Unit, validator: (Float) -> Unit) =
        parseList(prop, sizeChecker, validator) { parseFLOAT() }

    final override fun DOUBLE(prop: KMutableProperty0<Double>, validator: (Double) -> Unit) =
        validateAndSet(prop, validator) { parseDOUBLE() }

    final override fun DOUBLES(
        prop: KMutableProperty0<List<Double>>,
        sizeChecker: (Int) -> Unit,
        validator: (Double) -> Unit
    ) = parseList(prop, sizeChecker, validator) { parseDOUBLE() }

    final override fun <E : Enum<E>> ENUM8(prop: KMutableProperty0<E>, values: Array<E>, validator: (E) -> Unit) =
        validateAndSet(prop, validator) { parseENUM8(values) }

    final override fun <E : Enum<E>> ENUM8S(
        prop: KMutableProperty0<List<E>>,
        values: Array<E>,
        sizeChecker: (Int) -> Unit,
        validator: (E) -> Unit
    ) = parseList(prop, sizeChecker, validator) { parseENUM8(values) }

    final override fun <E : Enum<E>> ENUM16(prop: KMutableProperty0<E>, values: Array<E>, validator: (E) -> Unit) =
        validateAndSet(prop, validator) { parseENUM16(values) }

    final override fun <E : Enum<E>> ENUM16S(
        prop: KMutableProperty0<List<E>>,
        values: Array<E>,
        sizeChecker: (Int) -> Unit,
        validator: (E) -> Unit
    ) = parseList(prop, sizeChecker, validator) { parseENUM16(values) }

    final override fun <T> OBJECT(prop: KMutableProperty0<T?>, po: ProtokolObject<T>, validator: T?.() -> Unit) =
        validateAndSet(prop, validator) { parseOBJECT(po) }

    final override fun <T> OBJECTS(
        prop: KMutableProperty0<List<T>>,
        po: ProtokolObject<T>,
        sizeChecker: (Int) -> Unit,
        validator: T.() -> Unit
    ) = parseList(prop, sizeChecker, validator) { parseOBJECT(po) ?: throw NullPointerException() }

    final override fun BITSET8(
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
        val bits = parseBITSET8()

        require(bits.size == 8)

        val bit0 = bits[0]
        val bit1 = bits[1]
        val bit2 = bits[2]
        val bit3 = bits[3]
        val bit4 = bits[4]
        val bit5 = bits[5]
        val bit6 = bits[6]
        val bit7 = bits[7]

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

    final override fun <K, V> MAP(prop: KMutableProperty0<Map<K, V>>, po: ProtokolObject<ProtokolMapEntry<K, V>>) {
        val map = mutableMapOf<K, V>()
        val size = parseSize()
        repeat(size) {
            val entry = po.create()
            po.protokol(this, entry)
            map[entry.key] = entry.value
        }
        prop.set(map)
    }
}

