package com.electrit.protokol

import kotlin.reflect.KMutableProperty0

object ByteArrayProtokolCodec {

    fun <T> encode(value: T, po: ProtokolObject<T>): ByteArray {
        val sizer = ValidatorAndSizer()
        po.use(value, sizer)
        val composer = ByteArrayProtokolComposer(sizer.size)
        po.use(value, composer)
        return composer.bytes
    }

    fun <T> decode(bytes: ByteArray, po: ProtokolObject<T>): T {
        val value = po.create()
        po.use(value, ByteArrayProtokolParser(bytes))
        return value
    }

    private class ValidatorAndSizer : ProtokolComposer() {
        var size: Int = 0

        override fun composeBYTE(value: Byte, validator: (Byte) -> Unit) {
            validator(value)
            size++
        }

        override fun composeBYTEARRAY(value: ByteArray, validator: (ByteArray) -> Unit) {
            composeSize(value.size)
            validator(value)
            size += value.size
        }

        override fun composeSTRING(value: String, validator: (String) -> Unit) {
            validator(value)
            super.composeSTRING(value, validator)
        }

        override fun composeBOOLEAN(value: Boolean, validator: (Boolean) -> Unit) {
            validator(value)
            super.composeBOOLEAN(value, validator)
        }

        override fun composeSHORT(value: Short, validator: (Short) -> Unit) {
            validator(value)
            super.composeSHORT(value, validator)
        }

        override fun composeINT(value: Int, validator: (Int) -> Unit) {
            validator(value)
            super.composeINT(value, validator)
        }

        override fun composeLONG(value: Long, validator: (Long) -> Unit) {
            validator(value)
            super.composeLONG(value, validator)
        }

        override fun <E : Enum<E>> composeENUM8(value: E, values: Array<E>, validator: (E) -> Unit) {
            require(values.size <= 256) { "ENUM8 supports enums with up to 256 instances, actual: ${values.size}" }
            validator(value)
            super.composeENUM8(value, values, validator)
        }

        override fun <E : Enum<E>> composeENUM16(value: E, values: Array<E>, validator: (E) -> Unit) {
            require(values.size <= 65536) { "ENUM16 supports enums with up to 65536 instances, actual: ${values.size}" }
            validator(value)
            super.composeENUM16(value, values, validator)
        }

        override fun <T> composeOBJECT(value: T?, po: ProtokolObject<T>, validator: T.() -> Unit) {
            value?.validator()
            super.composeOBJECT(value, po, validator)
        }

        override fun <T> composeList(
            prop: KMutableProperty0<List<T>>,
            sizeChecker: (Int) -> Unit,
            composer: (T) -> Unit
        ) {
            val list = prop.get()
            sizeChecker(list.size)
            composeSize(list.size)
            list.forEach { composer(it) }
        }

        override fun composeBITSET8(
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
            ) -> Unit
        ) {
            validator(b0, b1, b2, b3, b4, b5, b6, b7)
            super.composeBITSET8(b0, b1, b2, b3, b4, b5, b6, b7, validator)
        }
    }

}

