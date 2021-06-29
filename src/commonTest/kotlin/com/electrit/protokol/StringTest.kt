package com.electrit.protokol

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StringTest {

    class StringData(var str: String = "")

    object StringDataProtokolObject : ProtokolObject<StringData> {
        override val protokol: Protokol.(StringData) -> Unit = {
            with(it) {
                STRING(::str)
            }
        }

        override fun create() = StringData()
    }

    object StrictStringDataProtokolObject : ProtokolObject<StringData> {
        private val regex = Regex("\\d+")

        override val protokol: Protokol.(StringData) -> Unit = {
            with(it) {
                STRING(::str) { value -> if (!regex.matches(value)) throw IllegalArgumentException("Only numbers allowed") }
            }
        }

        override fun create() = StringData()
    }

    @Test
    fun test() {
        fun assert(s: String, po: ProtokolObject<StringData>) {
            val bytes = ByteArrayProtokolCodec.encode(StringData(s), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(s, data.str)
        }

        assert("", StringDataProtokolObject)
        assert(Random.nextBytes(Random.nextInt(1, 128)).decodeToString(), StringDataProtokolObject)
        assert(Random.nextBytes(Random.nextInt(128, 1024)).decodeToString(), StringDataProtokolObject)
        assert("Пятый, пятый, я - десятый. Как слышишь? Приём.", StringDataProtokolObject)

        assert("2020", StrictStringDataProtokolObject)
        assertFailsWith<IllegalArgumentException> { assert("Twenty twenty", StrictStringDataProtokolObject) }
    }

    @Test
    fun testParseError() {
        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(StringData("Twenty twenty"), StringDataProtokolObject),
                StrictStringDataProtokolObject
            )
        }
    }

}