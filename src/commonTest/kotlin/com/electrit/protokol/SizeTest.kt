package com.electrit.protokol

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SizeTest {

    @Test
    fun test() {
        fun assertSize(size: Int, arraySize: Int) {
            val composer = ByteArrayProtokolComposer(arraySize)
            composer.composeSize(size)
            assertEquals(arraySize, composer.bytes.size)
            val parser = ByteArrayProtokolParser(composer.bytes)
            assertEquals(size, parser.parseSize())
        }

        assertSize(0, 1)
        assertSize(127, 1)
        assertSize(128, 4)
        assertSize(Int.MAX_VALUE, 4)
        assertFailsWith<IllegalArgumentException> { ByteArrayProtokolComposer(1).composeSize(-1) }
    }

}