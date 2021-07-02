package com.electrit.protokol

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SizeTest {

    private val bytes = mutableListOf<Byte>()

    private val composer = object : ProtokolComposer() {
        override fun composeByte(value: Byte) {
            bytes += value
        }

        override fun composeByteArray(value: ByteArray): Unit = throw NotImplementedError()
    }

    private val parser = object : ProtokolParser() {
        override fun parseBYTE(): Byte = bytes.removeFirst()

        override fun parseBYTEARRAY(): ByteArray = throw NotImplementedError()
    }

    @Test
    fun test() {
        fun assertSize(size: Int, expectedBytes: Int) {
            bytes.clear()
            composer.composeSize(size)
            assertEquals(expectedBytes, bytes.size)
            assertEquals(size, parser.parseSize())
        }

        assertFailsWith<IllegalArgumentException> { assertSize(-1, 1) }
        assertSize(0, 1)
        assertSize(127, 1)
        assertSize(128, 4)
        assertSize(Int.MAX_VALUE, 4)
        assertFailsWith<IllegalArgumentException> { assertSize(Int.MIN_VALUE, 4) }
    }

}