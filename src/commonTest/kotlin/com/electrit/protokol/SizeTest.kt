package com.electrit.protokol

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SizeTest {

    private val bytes = mutableListOf<Byte>()

    private val composer = object : StandardProtokolComposer() {
        // public composeSizeForTest() to call protected composeSize()
        fun composeSizeForTest(size: Int) = composeSize(size)

        override fun composeBYTE(value: Byte) {
            bytes += value
        }

        override fun composeBYTEARRAY(value: ByteArray): Unit = throw NotImplementedError()
    }

    private val parser = object : StandardProtokolParser() {
        // public parseSizeForTest() to call protected parseSize()
        fun parseSizeForTest() = parseSize()

        override fun parseBYTE(): Byte = bytes.removeFirst()

        override fun parseBYTEARRAY(): ByteArray = throw NotImplementedError()
    }

    @Test
    fun test() {
        fun assertSize(size: Int, expectedBytes: Int) {
            bytes.clear()
            composer.composeSizeForTest(size)
            assertEquals(expectedBytes, bytes.size)
            assertEquals(size, parser.parseSizeForTest())
        }

        assertFailsWith<IllegalArgumentException> { assertSize(-1, 1) }
        assertSize(0, 1)
        assertSize(127, 1)
        assertSize(128, 4)
        assertSize(Int.MAX_VALUE, 4)
        assertFailsWith<IllegalArgumentException> { assertSize(Int.MIN_VALUE, 4) }
    }

}