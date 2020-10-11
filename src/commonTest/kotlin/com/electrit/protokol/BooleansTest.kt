package com.electrit.protokol

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BooleansTest {

    class BooleansData(var list: List<Boolean> = emptyList())

    object BooleansDataProtokolObject : ProtokolObject<BooleansData> {
        override fun use(value: BooleansData, p: Protokol) = with(p) {
            with(value) {
                BOOLEANS(::list)
            }
        }

        override fun create() = BooleansData()
    }

    object StrictBooleansDataProtokolObject : ProtokolObject<BooleansData> {
        override fun use(value: BooleansData, p: Protokol) = with(p) {
            with(value) {
                BOOLEANS(::list, { size -> if (size == 0) throw IllegalArgumentException("size can't be 0") }) {
                    if (!it) throw IllegalArgumentException("value can't be false")
                }
            }
        }

        override fun create() = BooleansData()
    }

    @Test
    fun test() {
        fun assert(list: List<Boolean>, po: ProtokolObject<BooleansData>) {
            val bytes = ByteArrayProtokolCodec.encode(BooleansData(list), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(list, data.list)
        }

        assert(emptyList(), BooleansDataProtokolObject)
        assert(List(127) { Random.nextBoolean() }, BooleansDataProtokolObject) // still one byte for list size
        assert(List(128) { Random.nextBoolean() }, BooleansDataProtokolObject) // two bytes for list size

        assertFailsWith<IllegalArgumentException> { assert(emptyList(), StrictBooleansDataProtokolObject) }
        assert(listOf(true, true, true), StrictBooleansDataProtokolObject)
        assertFailsWith<IllegalArgumentException> { assert(listOf(true, false, true), StrictBooleansDataProtokolObject) }
    }

    @Test
    fun testParseError() {
        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(BooleansData(emptyList()), BooleansDataProtokolObject),
                StrictBooleansDataProtokolObject
            )
        }

        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(BooleansData(listOf(true, false, true)), BooleansDataProtokolObject),
                StrictBooleansDataProtokolObject
            )
        }
    }

}