package com.electrit.protokol

import kotlin.reflect.KMutableProperty0

abstract class ProtokolComposer : Protokol {

    abstract fun composeBYTE(value: Byte, validator: (Byte) -> Unit = {})

    abstract fun composeBYTEARRAY(value: ByteArray, validator: (ByteArray) -> Unit = {})

    open fun composeBITSET8(
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
        val value = (((((((
                (if (b7) 1 else 0) shl 1) or
                (if (b6) 1 else 0) shl 1) or
                (if (b5) 1 else 0) shl 1) or
                (if (b4) 1 else 0) shl 1) or
                (if (b3) 1 else 0) shl 1) or
                (if (b2) 1 else 0) shl 1) or
                (if (b1) 1 else 0) shl 1) or
                (if (b0) 1 else 0)
        composeBYTE(value.toByte())
    }

    open fun composeSTRING(value: String, validator: (String) -> Unit) =
        composeBYTEARRAY(value.encodeToByteArray())

    open fun composeBOOLEAN(value: Boolean, validator: (Boolean) -> Unit) = composeBYTE(if (value) 1 else 0)

    open fun composeSHORT(value: Short, validator: (Short) -> Unit = {}) {
        composeBYTE((value.toInt() ushr 8).toByte())
        composeBYTE(value.toByte())
    }

    open fun composeINT(value: Int, validator: (Int) -> Unit) {
        composeBYTE((value ushr 24).toByte())
        composeBYTE((value ushr 16).toByte())
        composeBYTE((value ushr 8).toByte())
        composeBYTE(value.toByte())
    }

    open fun composeLONG(value: Long, validator: (Long) -> Unit) {
        composeBYTE((value ushr 56).toByte())
        composeBYTE((value ushr 48).toByte())
        composeBYTE((value ushr 40).toByte())
        composeBYTE((value ushr 32).toByte())
        composeBYTE((value ushr 24).toByte())
        composeBYTE((value ushr 16).toByte())
        composeBYTE((value ushr 8).toByte())
        composeBYTE(value.toByte())
    }

    open fun <E : Enum<E>> composeENUM8(value: E, values: Array<E>, validator: (E) -> Unit) =
        composeBYTE(value.ordinal.toByte())

    open fun <E : Enum<E>> composeENUM16(value: E, values: Array<E>, validator: (E) -> Unit) =
        composeSHORT(value.ordinal.toShort())

    open fun <T> composeOBJECT(value: T?, po: ProtokolObject<T>, validator: T.() -> Unit) =
        if (value != null) {
            composeBYTE(1)
            po.use(value, this)
        } else {
            composeBYTE(0)
        }

    fun composeSize(size: Int) = when {
        size < 0 -> throw IllegalArgumentException("size can't be negative: $size")
        size < 128 -> { // 2^(8-1)
            composeBYTE(size.toByte())
        }
        size < 16384 -> { // 2^(16-2)
            val i = 0b1000000000000000 or size
            composeBYTE((i shr 8).toByte())
            composeBYTE(i.toByte())
        }
        size < 1073741824 -> { // 2^(32-2)
            val i = 0b11000000000000000000000000000000.toInt() or size
            composeBYTE((i shr 24).toByte())
            composeBYTE((i shr 16).toByte())
            composeBYTE((i shr 8).toByte())
            composeBYTE(i.toByte())
        }
        else -> throw IllegalArgumentException("size is too big: $size")
    }

    open fun <T> composeList(
        prop: KMutableProperty0<List<T>>,
        sizeChecker: (Int) -> Unit,
        composer: (T) -> Unit
    ) {
        val list = prop.get()
        composeSize(list.size)
        list.forEach { composer(it) }
    }

    override fun BYTE(prop: KMutableProperty0<Byte>, validator: (Byte) -> Unit) =
        composeBYTE(prop.get(), validator)

    override fun BYTES(
        prop: KMutableProperty0<List<Byte>>,
        sizeChecker: (Int) -> Unit,
        validator: (Byte) -> Unit
    ) = composeList(prop, sizeChecker) { composeBYTE(it, validator) }

    override fun BYTEARRAY(prop: KMutableProperty0<ByteArray>, validator: (ByteArray) -> Unit) =
        composeBYTEARRAY(prop.get(), validator)

    override fun BYTEARRAYS(
        prop: KMutableProperty0<List<ByteArray>>,
        sizeChecker: (Int) -> Unit,
        validator: (ByteArray) -> Unit
    ) = composeList(prop, sizeChecker) { composeBYTEARRAY(it, validator) }

    override fun STRING(prop: KMutableProperty0<String>, validator: (String) -> Unit) =
        composeSTRING(prop.get(), validator)

    override fun STRINGS(
        prop: KMutableProperty0<List<String>>,
        sizeChecker: (Int) -> Unit,
        validator: (String) -> Unit
    ) = composeList(prop, sizeChecker) { composeSTRING(it, validator) }

    override fun BOOLEAN(prop: KMutableProperty0<Boolean>, validator: (Boolean) -> Unit) =
        composeBOOLEAN(prop.get(), validator)

    override fun BOOLEANS(
        prop: KMutableProperty0<List<Boolean>>,
        sizeChecker: (Int) -> Unit,
        validator: (Boolean) -> Unit
    ) = composeList(prop, sizeChecker) { composeBOOLEAN(it, validator) }

    override fun SHORT(prop: KMutableProperty0<Short>, validator: (Short) -> Unit) =
        composeSHORT(prop.get(), validator)

    override fun SHORTS(
        prop: KMutableProperty0<List<Short>>,
        sizeChecker: (Int) -> Unit,
        validator: (Short) -> Unit
    ) = composeList(prop, sizeChecker) { composeSHORT(it, validator) }

    override fun INT(prop: KMutableProperty0<Int>, validator: (Int) -> Unit) =
        composeINT(prop.get(), validator)

    override fun INTS(
        prop: KMutableProperty0<List<Int>>,
        sizeChecker: (Int) -> Unit,
        validator: (Int) -> Unit
    ) = composeList(prop, sizeChecker) { composeINT(it, validator) }

    override fun LONG(prop: KMutableProperty0<Long>, validator: (Long) -> Unit) =
        composeLONG(prop.get(), validator)

    override fun LONGS(
        prop: KMutableProperty0<List<Long>>,
        sizeChecker: (Int) -> Unit,
        validator: (Long) -> Unit
    ) = composeList(prop, sizeChecker) { composeLONG(it, validator) }

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

}