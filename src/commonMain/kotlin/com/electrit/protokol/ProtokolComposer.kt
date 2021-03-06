package com.electrit.protokol

import kotlin.reflect.KMutableProperty0

abstract class ProtokolComposer : Protokol {

    abstract fun composeByte(value: Byte)

    abstract fun composeByteArray(value: ByteArray)

    private fun composeBITSET8(
        b0: Boolean,
        b1: Boolean,
        b2: Boolean,
        b3: Boolean,
        b4: Boolean,
        b5: Boolean,
        b6: Boolean,
        b7: Boolean,
        validator: (
            b0: Boolean,
            b1: Boolean,
            b2: Boolean,
            b3: Boolean,
            b4: Boolean,
            b5: Boolean,
            b6: Boolean,
            b7: Boolean
        ) -> Unit,
    ) {
        validator(b0, b1, b2, b3, b4, b5, b6, b7)
        val value = (((((((
                (if (b7) 1 else 0) shl 1) or
                (if (b6) 1 else 0) shl 1) or
                (if (b5) 1 else 0) shl 1) or
                (if (b4) 1 else 0) shl 1) or
                (if (b3) 1 else 0) shl 1) or
                (if (b2) 1 else 0) shl 1) or
                (if (b1) 1 else 0) shl 1) or
                (if (b0) 1 else 0)
        composeByte(value.toByte())
    }

    private fun composeSTRING(value: String) = composeByteArray(value.encodeToByteArray())

    private fun composeBOOLEAN(value: Boolean) = composeByte(if (value) 1 else 0)

    private fun composeSHORT(value: Short) {
        composeByte((value.toInt() ushr 8).toByte())
        composeByte(value.toByte())
    }

    private fun composeINT(value: Int) {
        composeByte((value ushr 24).toByte())
        composeByte((value ushr 16).toByte())
        composeByte((value ushr 8).toByte())
        composeByte(value.toByte())
    }

    private fun composeLONG(value: Long) {
        composeByte((value ushr 56).toByte())
        composeByte((value ushr 48).toByte())
        composeByte((value ushr 40).toByte())
        composeByte((value ushr 32).toByte())
        composeByte((value ushr 24).toByte())
        composeByte((value ushr 16).toByte())
        composeByte((value ushr 8).toByte())
        composeByte(value.toByte())
    }

    private fun composeFLOAT(value: Float, validator: (Float) -> Unit) {
        validator(value)
        composeINT(value.toBits())
    }

    private fun composeDOUBLE(value: Double, validator: (Double) -> Unit) {
        validator(value)
        composeLONG(value.toBits())
    }

    private fun <E : Enum<E>> composeENUM8(value: E, values: Array<E>, validator: (E) -> Unit) {
        require(values.size <= 256) { "ENUM8 supports enums with up to 256 instances, actual: ${values.size}" }
        validator(value)
        composeByte(value.ordinal.toByte())
    }

    private fun <E : Enum<E>> composeENUM16(value: E, values: Array<E>, validator: (E) -> Unit) {
        require(values.size <= 65536) { "ENUM16 supports enums with up to 65536 instances, actual: ${values.size}" }
        validator(value)
        composeSHORT(value.ordinal.toShort())
    }

    private fun <T> composeOBJECT(value: T?, po: ProtokolObject<T>, validator: T.() -> Unit) {
        value?.validator()
        if (value != null) {
            composeByte(1)
            po.protokol(this, value)
        } else {
            composeByte(0)
        }
    }

    internal fun composeSize(size: Int) = when {
        size < 0 -> throw IllegalArgumentException("size can't be negative: $size")
        size < 128 -> composeByte(size.toByte())
        else -> composeINT(size - 1 - Int.MAX_VALUE) // this sets sign bit while preserving the rest
    }

    private fun <T> composeList(
        prop: KMutableProperty0<List<T>>,
        sizeChecker: (Int) -> Unit,
        composer: (T) -> Unit
    ) {
        val list = prop.get()
        sizeChecker(list.size)
        composeSize(list.size)
        list.forEach { composer(it) }
    }

    override fun BYTE(prop: KMutableProperty0<Byte>, validator: (Byte) -> Unit) {
        validator(prop.get())
        composeByte(prop.get())
    }

    override fun BYTES(
        prop: KMutableProperty0<List<Byte>>,
        sizeChecker: (Int) -> Unit,
        validator: (Byte) -> Unit
    ) = composeList(prop, sizeChecker) { validator(it); composeByte(it) }

    override fun UBYTE(prop: KMutableProperty0<UByte>, validator: (UByte) -> Unit) {
        validator(prop.get())
        composeByte(prop.get().toByte())
    }

    override fun UBYTES(
        prop: KMutableProperty0<List<UByte>>,
        sizeChecker: (Int) -> Unit,
        validator: (UByte) -> Unit
    ): Unit = composeList(prop, sizeChecker) { validator(it); composeByte(it.toByte()) }

    override fun BYTEARRAY(prop: KMutableProperty0<ByteArray>, validator: (ByteArray) -> Unit) {
        validator(prop.get())
        composeByteArray(prop.get())
    }

    override fun BYTEARRAYS(
        prop: KMutableProperty0<List<ByteArray>>,
        sizeChecker: (Int) -> Unit,
        validator: (ByteArray) -> Unit
    ) = composeList(prop, sizeChecker) { validator(it); composeByteArray(it) }

    override fun STRING(prop: KMutableProperty0<String>, validator: (String) -> Unit) {
        validator(prop.get())
        composeSTRING(prop.get())
    }

    override fun STRINGS(
        prop: KMutableProperty0<List<String>>,
        sizeChecker: (Int) -> Unit,
        validator: (String) -> Unit
    ) = composeList(prop, sizeChecker) { validator(it); composeSTRING(it) }

    override fun BOOLEAN(prop: KMutableProperty0<Boolean>, validator: (Boolean) -> Unit) {
        validator(prop.get())
        composeBOOLEAN(prop.get())
    }

    override fun BOOLEANS(
        prop: KMutableProperty0<List<Boolean>>,
        sizeChecker: (Int) -> Unit,
        validator: (Boolean) -> Unit
    ) = composeList(prop, sizeChecker) { validator(it); composeBOOLEAN(it) }

    override fun SHORT(prop: KMutableProperty0<Short>, validator: (Short) -> Unit) {
        validator(prop.get())
        composeSHORT(prop.get())
    }

    override fun SHORTS(
        prop: KMutableProperty0<List<Short>>,
        sizeChecker: (Int) -> Unit,
        validator: (Short) -> Unit
    ) = composeList(prop, sizeChecker) { validator(it); composeSHORT(it) }

    override fun USHORT(prop: KMutableProperty0<UShort>, validator: (UShort) -> Unit) {
        validator(prop.get())
        composeSHORT(prop.get().toShort())
    }

    override fun USHORTS(
        prop: KMutableProperty0<List<UShort>>,
        sizeChecker: (Int) -> Unit,
        validator: (UShort) -> Unit
    ): Unit = composeList(prop, sizeChecker) { validator(it); composeSHORT(it.toShort()) }

    override fun INT(prop: KMutableProperty0<Int>, validator: (Int) -> Unit) {
        validator(prop.get())
        composeINT(prop.get())
    }

    override fun INTS(
        prop: KMutableProperty0<List<Int>>,
        sizeChecker: (Int) -> Unit,
        validator: (Int) -> Unit
    ) = composeList(prop, sizeChecker) { validator(it); composeINT(it) }

    override fun UINT(prop: KMutableProperty0<UInt>, validator: (UInt) -> Unit) {
        validator(prop.get())
        composeINT(prop.get().toInt())
    }

    override fun UINTS(
        prop: KMutableProperty0<List<UInt>>,
        sizeChecker: (Int) -> Unit,
        validator: (UInt) -> Unit
    ): Unit = composeList(prop, sizeChecker) { validator(it); composeINT(it.toInt()) }

    override fun LONG(prop: KMutableProperty0<Long>, validator: (Long) -> Unit) {
        validator(prop.get())
        composeLONG(prop.get())
    }

    override fun LONGS(
        prop: KMutableProperty0<List<Long>>,
        sizeChecker: (Int) -> Unit,
        validator: (Long) -> Unit
    ) = composeList(prop, sizeChecker) { validator(it); composeLONG(it) }

    override fun ULONG(prop: KMutableProperty0<ULong>, validator: (ULong) -> Unit) {
        validator(prop.get())
        composeLONG(prop.get().toLong())
    }

    override fun ULONGS(
        prop: KMutableProperty0<List<ULong>>,
        sizeChecker: (Int) -> Unit,
        validator: (ULong) -> Unit
    ): Unit = composeList(prop, sizeChecker) { validator(it); composeLONG(it.toLong()) }

    override fun FLOAT(prop: KMutableProperty0<Float>, validator: (Float) -> Unit) =
        composeFLOAT(prop.get(), validator)

    override fun FLOATS(prop: KMutableProperty0<List<Float>>, sizeChecker: (Int) -> Unit, validator: (Float) -> Unit) =
        composeList(prop, sizeChecker) { composeFLOAT(it, validator) }

    override fun DOUBLE(prop: KMutableProperty0<Double>, validator: (Double) -> Unit) =
        composeDOUBLE(prop.get(), validator)

    override fun DOUBLES(
        prop: KMutableProperty0<List<Double>>,
        sizeChecker: (Int) -> Unit,
        validator: (Double) -> Unit
    ) = composeList(prop, sizeChecker) { composeDOUBLE(it, validator) }

    override fun <E : Enum<E>> ENUM8(prop: KMutableProperty0<E>, values: Array<E>, validator: (E) -> Unit) =
        composeENUM8(prop.get(), values, validator)

    override fun <E : Enum<E>> ENUM8S(
        prop: KMutableProperty0<List<E>>,
        values: Array<E>,
        sizeChecker: (Int) -> Unit,
        validator: (E) -> Unit
    ) = composeList(prop, sizeChecker) { composeENUM8(it, values, validator) }

    override fun <E : Enum<E>> ENUM16(prop: KMutableProperty0<E>, values: Array<E>, validator: (E) -> Unit) =
        composeENUM16(prop.get(), values, validator)

    override fun <E : Enum<E>> ENUM16S(
        prop: KMutableProperty0<List<E>>,
        values: Array<E>,
        sizeChecker: (Int) -> Unit,
        validator: (E) -> Unit
    ) = composeList(prop, sizeChecker) { composeENUM16(it, values, validator) }

    override fun <T> OBJECT(prop: KMutableProperty0<T?>, po: ProtokolObject<T>, validator: T?.() -> Unit) =
        composeOBJECT(prop.get(), po, validator)

    override fun <T> OBJECTS(
        prop: KMutableProperty0<List<T>>,
        po: ProtokolObject<T>,
        sizeChecker: (Int) -> Unit,
        validator: T.() -> Unit
    ) = composeList(prop, sizeChecker) { composeOBJECT(it, po, validator) }

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
    ) = composeBITSET8(
        b0?.get() ?: false,
        b1?.get() ?: false,
        b2?.get() ?: false,
        b3?.get() ?: false,
        b4?.get() ?: false,
        b5?.get() ?: false,
        b6?.get() ?: false,
        b7?.get() ?: false,
        validator
    )

    override fun <K, V> MAP(prop: KMutableProperty0<Map<K, V>>, po: ProtokolObject<ProtokolMapEntry<K, V>>) {
        val map = prop.get()
        composeSize(map.size)
        map.forEach {
            val protokol = po.protokol
            protokol(ProtokolMapEntry(it.key, it.value))
        }
    }
}