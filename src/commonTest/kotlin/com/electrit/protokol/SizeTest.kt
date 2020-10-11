package com.electrit.protokol

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

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
        assertSize(128, 2)
        assertSize(16383, 2)
        assertSize(16384, 4)
        assertSize(1073741823, 4)
        assertFails { ByteArrayProtokolComposer(4).composeSize(1073741824) } is IllegalArgumentException
    }

}