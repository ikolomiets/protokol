package com.electrit.protokol

import kotlin.reflect.KMutableProperty0

@Suppress("FunctionName")
interface Protokol {

    fun BYTE(prop: KMutableProperty0<Byte>, validator: (Byte) -> Unit = {})

    fun BYTES(prop: KMutableProperty0<List<Byte>>, sizeChecker: (Int) -> Unit = {}, validator: (Byte) -> Unit = {})

    fun BYTEARRAY(prop: KMutableProperty0<ByteArray>, validator: (ByteArray) -> Unit = {})

    fun BYTEARRAYS(
        prop: KMutableProperty0<List<ByteArray>>,
        sizeChecker: (Int) -> Unit = {},
        validator: (ByteArray) -> Unit = {}
    )

    fun STRING(prop: KMutableProperty0<String>, validator: (String) -> Unit = {})

    fun STRINGS(
        prop: KMutableProperty0<List<String>>,
        sizeChecker: (Int) -> Unit = {},
        validator: (String) -> Unit = {}
    )

    fun BOOLEAN(prop: KMutableProperty0<Boolean>, validator: (Boolean) -> Unit = {})

    fun BOOLEANS(
        prop: KMutableProperty0<List<Boolean>>,
        sizeChecker: (Int) -> Unit = {},
        validator: (Boolean) -> Unit = {}
    )

    fun SHORT(prop: KMutableProperty0<Short>, validator: (Short) -> Unit = {})

    fun SHORTS(
        prop: KMutableProperty0<List<Short>>,
        sizeChecker: (Int) -> Unit = {},
        validator: (Short) -> Unit = {}
    )

    fun INT(prop: KMutableProperty0<Int>, validator: (Int) -> Unit = {})

    fun INTS(prop: KMutableProperty0<List<Int>>, sizeChecker: (Int) -> Unit = {}, validator: (Int) -> Unit = {})

    fun LONG(prop: KMutableProperty0<Long>, validator: (Long) -> Unit = {})

    fun LONGS(prop: KMutableProperty0<List<Long>>, sizeChecker: (Int) -> Unit = {}, validator: (Long) -> Unit = {})

    fun FLOAT(prop: KMutableProperty0<Float>, validator: (Float) -> Unit = {})

    fun FLOATS(prop: KMutableProperty0<List<Float>>, sizeChecker: (Int) -> Unit = {}, validator: (Float) -> Unit = {})

    fun DOUBLE(prop: KMutableProperty0<Double>, validator: (Double) -> Unit = {})

    fun DOUBLES(
        prop: KMutableProperty0<List<Double>>,
        sizeChecker: (Int) -> Unit = {},
        validator: (Double) -> Unit = {}
    )

    fun <E : Enum<E>> ENUM8(prop: KMutableProperty0<E>, values: Array<E>, validator: (E) -> Unit = {})

    fun <E : Enum<E>> ENUM8S(
        prop: KMutableProperty0<List<E>>,
        values: Array<E>,
        sizeChecker: (Int) -> Unit = {},
        validator: (E) -> Unit = {}
    )

    fun <E : Enum<E>> ENUM16(prop: KMutableProperty0<E>, values: Array<E>, validator: (E) -> Unit = {})

    fun <E : Enum<E>> ENUM16S(
        prop: KMutableProperty0<List<E>>,
        values: Array<E>,
        sizeChecker: (Int) -> Unit = {},
        validator: (E) -> Unit = {}
    )

    fun <T> OBJECT(prop: KMutableProperty0<T?>, po: ProtokolObject<T>, validator: T?.() -> Unit = {})

    fun <T> OBJECTS(
        prop: KMutableProperty0<List<T>>,
        po: ProtokolObject<T>,
        sizeChecker: (Int) -> Unit = {},
        validator: T.() -> Unit = {}
    )

    fun BITSET8(
        b0: KMutableProperty0<Boolean>? = null,
        b1: KMutableProperty0<Boolean>? = null,
        b2: KMutableProperty0<Boolean>? = null,
        b3: KMutableProperty0<Boolean>? = null,
        b4: KMutableProperty0<Boolean>? = null,
        b5: KMutableProperty0<Boolean>? = null,
        b6: KMutableProperty0<Boolean>? = null,
        b7: KMutableProperty0<Boolean>? = null,
        validator: (
            b0: Boolean,
            b1: Boolean,
            b2: Boolean,
            b3: Boolean,
            b4: Boolean,
            b5: Boolean,
            b6: Boolean,
            b7: Boolean
        ) -> Unit = { _, _, _, _, _, _, _, _ -> }
    )

}