package com.electrit.protokol

import kotlin.reflect.KMutableProperty0

abstract class ProtokolComposer : Protokol {

    protected abstract fun composeBYTE(value: Byte)

    protected abstract fun composeUBYTE(value: UByte)

    protected abstract fun composeBYTEARRAY(value: ByteArray)

    protected abstract fun composeSTRING(value: String)

    protected abstract fun composeBOOLEAN(value: Boolean)

    protected abstract fun composeSHORT(value: Short)

    protected abstract fun composeUSHORT(value: UShort)

    protected abstract fun composeINT(value: Int)

    protected abstract fun composeUINT(value: UInt)

    protected abstract fun composeLONG(value: Long)

    protected abstract fun composeULONG(value: ULong)

    protected abstract fun composeFLOAT(value: Float)

    protected abstract fun composeDOUBLE(value: Double)

    protected abstract fun <E : Enum<E>> composeENUM8(value: E, values: Array<E>)

    protected abstract fun <E : Enum<E>> composeENUM16(value: E, values: Array<E>)

    protected abstract fun <T> composeOBJECT(value: T?, po: ProtokolObject<T>)

    protected abstract fun composeBITSET8(
        b0: Boolean,
        b1: Boolean,
        b2: Boolean,
        b3: Boolean,
        b4: Boolean,
        b5: Boolean,
        b6: Boolean,
        b7: Boolean,
    )

    protected abstract fun composeSize(size: Int)

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

    final override fun BYTE(prop: KMutableProperty0<Byte>, validator: (Byte) -> Unit) {
        validator(prop.get())
        composeBYTE(prop.get())
    }

    final override fun BYTES(
        prop: KMutableProperty0<List<Byte>>,
        sizeChecker: (Int) -> Unit,
        validator: (Byte) -> Unit
    ) = composeList(prop, sizeChecker) { validator(it); composeBYTE(it) }

    final override fun UBYTE(prop: KMutableProperty0<UByte>, validator: (UByte) -> Unit) {
        validator(prop.get())
        composeUBYTE(prop.get())
    }

    final override fun UBYTES(
        prop: KMutableProperty0<List<UByte>>,
        sizeChecker: (Int) -> Unit,
        validator: (UByte) -> Unit
    ): Unit = composeList(prop, sizeChecker) { validator(it); composeUBYTE(it) }

    final override fun BYTEARRAY(prop: KMutableProperty0<ByteArray>, validator: (ByteArray) -> Unit) {
        validator(prop.get())
        composeBYTEARRAY(prop.get())
    }

    final override fun BYTEARRAYS(
        prop: KMutableProperty0<List<ByteArray>>,
        sizeChecker: (Int) -> Unit,
        validator: (ByteArray) -> Unit
    ) = composeList(prop, sizeChecker) { validator(it); composeBYTEARRAY(it) }

    final override fun STRING(prop: KMutableProperty0<String>, validator: (String) -> Unit) {
        validator(prop.get())
        composeSTRING(prop.get())
    }

    final override fun STRINGS(
        prop: KMutableProperty0<List<String>>,
        sizeChecker: (Int) -> Unit,
        validator: (String) -> Unit
    ) = composeList(prop, sizeChecker) { validator(it); composeSTRING(it) }

    final override fun BOOLEAN(prop: KMutableProperty0<Boolean>, validator: (Boolean) -> Unit) {
        validator(prop.get())
        composeBOOLEAN(prop.get())
    }

    final override fun BOOLEANS(
        prop: KMutableProperty0<List<Boolean>>,
        sizeChecker: (Int) -> Unit,
        validator: (Boolean) -> Unit
    ) = composeList(prop, sizeChecker) { validator(it); composeBOOLEAN(it) }

    final override fun SHORT(prop: KMutableProperty0<Short>, validator: (Short) -> Unit) {
        validator(prop.get())
        composeSHORT(prop.get())
    }

    final override fun SHORTS(
        prop: KMutableProperty0<List<Short>>,
        sizeChecker: (Int) -> Unit,
        validator: (Short) -> Unit
    ) = composeList(prop, sizeChecker) { validator(it); composeSHORT(it) }

    final override fun USHORT(prop: KMutableProperty0<UShort>, validator: (UShort) -> Unit) {
        validator(prop.get())
        composeUSHORT(prop.get())
    }

    final override fun USHORTS(
        prop: KMutableProperty0<List<UShort>>,
        sizeChecker: (Int) -> Unit,
        validator: (UShort) -> Unit
    ): Unit = composeList(prop, sizeChecker) { validator(it); composeUSHORT(it) }

    final override fun INT(prop: KMutableProperty0<Int>, validator: (Int) -> Unit) {
        validator(prop.get())
        composeINT(prop.get())
    }

    final override fun INTS(
        prop: KMutableProperty0<List<Int>>,
        sizeChecker: (Int) -> Unit,
        validator: (Int) -> Unit
    ) = composeList(prop, sizeChecker) { validator(it); composeINT(it) }

    final override fun UINT(prop: KMutableProperty0<UInt>, validator: (UInt) -> Unit) {
        validator(prop.get())
        composeUINT(prop.get())
    }

    final override fun UINTS(
        prop: KMutableProperty0<List<UInt>>,
        sizeChecker: (Int) -> Unit,
        validator: (UInt) -> Unit
    ) = composeList(prop, sizeChecker) { validator(it); composeUINT(it) }

    final override fun LONG(prop: KMutableProperty0<Long>, validator: (Long) -> Unit) {
        validator(prop.get())
        composeLONG(prop.get())
    }

    final override fun LONGS(
        prop: KMutableProperty0<List<Long>>,
        sizeChecker: (Int) -> Unit,
        validator: (Long) -> Unit
    ) = composeList(prop, sizeChecker) { validator(it); composeLONG(it) }

    final override fun ULONG(prop: KMutableProperty0<ULong>, validator: (ULong) -> Unit) {
        validator(prop.get())
        composeULONG(prop.get())
    }

    final override fun ULONGS(
        prop: KMutableProperty0<List<ULong>>,
        sizeChecker: (Int) -> Unit,
        validator: (ULong) -> Unit
    ) = composeList(prop, sizeChecker) { validator(it); composeULONG(it) }

    final override fun FLOAT(prop: KMutableProperty0<Float>, validator: (Float) -> Unit) {
        val value = prop.get()
        validator(value)
        composeFLOAT(value)
    }

    final override fun FLOATS(prop: KMutableProperty0<List<Float>>, sizeChecker: (Int) -> Unit, validator: (Float) -> Unit) =
        composeList(prop, sizeChecker) { validator(it); composeFLOAT(it) }

    final override fun DOUBLE(prop: KMutableProperty0<Double>, validator: (Double) -> Unit) {
        val value = prop.get()
        validator(value)
        composeDOUBLE(value)
    }

    final override fun DOUBLES(
        prop: KMutableProperty0<List<Double>>,
        sizeChecker: (Int) -> Unit,
        validator: (Double) -> Unit
    ) = composeList(prop, sizeChecker) { validator(it); composeDOUBLE(it) }

    final override fun <E : Enum<E>> ENUM8(prop: KMutableProperty0<E>, values: Array<E>, validator: (E) -> Unit) {
        require(values.size <= 256) { "ENUM8 supports enums with up to 256 instances, actual: ${values.size}" }
        val value = prop.get()
        validator(value)
        composeENUM8(value, values)
    }

    final override fun <E : Enum<E>> ENUM8S(
        prop: KMutableProperty0<List<E>>,
        values: Array<E>,
        sizeChecker: (Int) -> Unit,
        validator: (E) -> Unit
    ) {
        require(values.size <= 256) { "ENUM8 supports enums with up to 256 instances, actual: ${values.size}" }
        composeList(prop, sizeChecker) { validator(it); composeENUM8(it, values) }
    }

    final override fun <E : Enum<E>> ENUM16(prop: KMutableProperty0<E>, values: Array<E>, validator: (E) -> Unit) {
        require(values.size <= 65536) { "ENUM16 supports enums with up to 65536 instances, actual: ${values.size}" }
        val value = prop.get()
        validator(value)
        composeENUM16(value, values)
    }

    final override fun <E : Enum<E>> ENUM16S(
        prop: KMutableProperty0<List<E>>,
        values: Array<E>,
        sizeChecker: (Int) -> Unit,
        validator: (E) -> Unit
    ) {
        require(values.size <= 65536) { "ENUM16 supports enums with up to 65536 instances, actual: ${values.size}" }
        composeList(prop, sizeChecker) { validator(it); composeENUM16(it, values) }
    }

    final override fun <T> OBJECT(prop: KMutableProperty0<T?>, po: ProtokolObject<T>, validator: T?.() -> Unit) {
        val value = prop.get()
        value?.validator()
        composeOBJECT(value, po)
    }

    final override fun <T> OBJECTS(
        prop: KMutableProperty0<List<T>>,
        po: ProtokolObject<T>,
        sizeChecker: (Int) -> Unit,
        validator: T.() -> Unit
    ) = composeList(prop, sizeChecker) { it.validator(); composeOBJECT(it, po) }

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
        val bit0 = b0?.get() ?: false
        val bit1 = b1?.get() ?: false
        val bit2 = b2?.get() ?: false
        val bit3 = b3?.get() ?: false
        val bit4 = b4?.get() ?: false
        val bit5 = b5?.get() ?: false
        val bit6 = b6?.get() ?: false
        val bit7 = b7?.get() ?: false
        validator(bit0, bit1, bit2, bit3, bit4, bit5, bit6, bit7)
        composeBITSET8(bit0, bit1, bit2, bit3, bit4, bit5, bit6, bit7)
    }

    final override fun <K, V> MAP(prop: KMutableProperty0<Map<K, V>>, po: ProtokolObject<ProtokolMapEntry<K, V>>) {
        val map = prop.get()
        composeSize(map.size)
        map.forEach {
            val protokol = po.protokol
            protokol(ProtokolMapEntry(it.key, it.value))
        }
    }

}