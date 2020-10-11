package com.electrit.protokol

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StringsTest {

    class StringsData(var list: List<String> = emptyList())

    object StringsDataProtokolObject : ProtokolObject<StringsData> {
        override fun use(value: StringsData, p: Protokol) = with(p) {
            with(value) {
                STRINGS(::list)
            }
        }

        override fun create() = StringsData()
    }

    object StrictStringsDataProtokolObject : ProtokolObject<StringsData> {
        override fun use(value: StringsData, p: Protokol) = with(p) {
            with(value) {
                STRINGS(::list, { size -> if (size == 0) throw IllegalArgumentException("size can't be 0") }) {
                    if (it.isEmpty()) throw IllegalArgumentException("string can't be empty")
                }
            }
        }

        override fun create() = StringsData()
    }

    @Test
    fun test() {
        fun assert(list: List<String>, po: ProtokolObject<StringsData>) {
            val bytes = ByteArrayProtokolCodec.encode(StringsData(list), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(list, data.list)
        }

        assert(emptyList(), StringsDataProtokolObject)
        assert(
            // still one byte for list size
            List(127) { Random.nextBytes(Random.nextInt(0, 30)).decodeToString() },
            StringsDataProtokolObject
        )
        assert(
            // two bytes for list size
            List(128) { Random.nextBytes(Random.nextInt(0, 30)).decodeToString() },
            StringsDataProtokolObject
        )

        assertFailsWith<IllegalArgumentException> { assert(emptyList(), StrictStringsDataProtokolObject) }
        assert(listOf("a", "bc", "def", "ghfi"), StrictStringsDataProtokolObject)
        assertFailsWith<IllegalArgumentException> {
            assert(
                listOf("a", "bc", "", "ghfi"),
                StrictStringsDataProtokolObject
            )
        }
    }

    @Test
    fun testParseError() {
        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(StringsData(emptyList()), StringsDataProtokolObject),
                StrictStringsDataProtokolObject
            )
        }

        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(StringsData(listOf("a", "bc", "", "ghfi")), StringsDataProtokolObject),
                StrictStringsDataProtokolObject
            )
        }
    }

}