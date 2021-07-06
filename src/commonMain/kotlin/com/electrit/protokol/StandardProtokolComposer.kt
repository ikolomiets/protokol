package com.electrit.protokol

abstract class StandardProtokolComposer : ProtokolComposer() {

    override fun composeBITSET8(
        b0: Boolean,
        b1: Boolean,
        b2: Boolean,
        b3: Boolean,
        b4: Boolean,
        b5: Boolean,
        b6: Boolean,
        b7: Boolean,
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

    override fun composeSTRING(value: String) = composeBYTEARRAY(value.encodeToByteArray())

    override fun composeBOOLEAN(value: Boolean) = composeBYTE(if (value) 1 else 0)

    override fun composeSHORT(value: Short) {
        composeBYTE((value.toInt() ushr 8).toByte())
        composeBYTE(value.toByte())
    }

    override fun composeINT(value: Int) {
        composeBYTE((value ushr 24).toByte())
        composeBYTE((value ushr 16).toByte())
        composeBYTE((value ushr 8).toByte())
        composeBYTE(value.toByte())
    }

    override fun composeLONG(value: Long) {
        composeBYTE((value ushr 56).toByte())
        composeBYTE((value ushr 48).toByte())
        composeBYTE((value ushr 40).toByte())
        composeBYTE((value ushr 32).toByte())
        composeBYTE((value ushr 24).toByte())
        composeBYTE((value ushr 16).toByte())
        composeBYTE((value ushr 8).toByte())
        composeBYTE(value.toByte())
    }

    override fun composeFLOAT(value: Float) = composeINT(value.toBits())

    override fun composeDOUBLE(value: Double) = composeLONG(value.toBits())

    override fun <E : Enum<E>> composeENUM8(value: E, values: Array<E>) = composeUBYTE(value.ordinal.toUByte())

    override fun <E : Enum<E>> composeENUM16(value: E, values: Array<E>) = composeUSHORT(value.ordinal.toUShort())

    override fun <T> composeOBJECT(value: T?, po: ProtokolObject<T>) {
        if (value != null) {
            composeBYTE(1)
            po.protokol(this, value)
        } else {
            composeBYTE(0)
        }
    }

    override fun composeSize(size: Int) = when {
        size < 0 -> throw IllegalArgumentException("size can't be negative: $size")
        size < 128 -> composeBYTE(size.toByte())
        else -> composeINT(size - 1 - Int.MAX_VALUE) // this sets sign bit while preserving the rest
    }

    override fun composeUBYTE(value: UByte): Unit = composeBYTE(value.toByte())

    override fun composeUSHORT(value: UShort): Unit = composeSHORT(value.toShort())

    override fun composeUINT(value: UInt): Unit = composeINT(value.toInt())

    override fun composeULONG(value: ULong): Unit = composeLONG(value.toLong())

}