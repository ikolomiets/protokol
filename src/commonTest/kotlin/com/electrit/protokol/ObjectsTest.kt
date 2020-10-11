package com.electrit.protokol

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ObjectsTest {

    @Suppress("EqualsOrHashCode")
    class Data(
        var bytes: ByteArray = ByteArray(0),
        var string: String = "",
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Data

            if (!bytes.contentEquals(other.bytes)) return false
            if (string != other.string) return false

            return true
        }
    }

    object DataProtokolObject : ProtokolObject<Data> {
        override fun use(value: Data, p: Protokol) = with(p) {
            with(value) {
                BYTEARRAY(::bytes)
                STRING(::string)
            }
        }

        override fun create() = Data()
    }

    @Suppress("EqualsOrHashCode")
    class ComplexData(
        var name: String = "",
        var data: Data? = null,
        var bytes: ByteArray = ByteArray(0),
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as ComplexData

            if (name != other.name) return false
            if (data != other.data) return false
            if (!bytes.contentEquals(other.bytes)) return false

            return true
        }
    }

    object ComplexDataProtokolObject : ProtokolObject<ComplexData> {
        override fun use(value: ComplexData, p: Protokol) = with(p) {
            with(value) {
                STRING(::name)
                OBJECT(::data, DataProtokolObject) {
                    if (this == null || string == "covid-19")
                        throw IllegalArgumentException("null and virus are not allowed")
                }
                BYTEARRAY(::bytes)
            }
        }

        override fun create() = ComplexData()
    }

    class ObjectsData(var list: List<ComplexData> = emptyList())

    object ObjectsDataProtokolObject : ProtokolObject<ObjectsData> {
        override fun use(value: ObjectsData, p: Protokol) = with(p) {
            with(value) {
                OBJECTS(::list, ComplexDataProtokolObject)
            }
        }

        override fun create() = ObjectsData()
    }

    object StrictObjectsDataProtokolObject : ProtokolObject<ObjectsData> {
        override fun use(value: ObjectsData, p: Protokol) = with(p) {
            with(value) {
                OBJECTS(::list, ComplexDataProtokolObject, {
                        size -> if (size == 0) throw IllegalArgumentException("size can't be 0")
                }) {
                    if (name == "test") throw IllegalArgumentException("List elements can't have name='test'")
                }
            }
        }

        override fun create() = ObjectsData()
    }

    @Test
    fun test() {
        fun assert(list: List<ComplexData>, po: ProtokolObject<ObjectsData>) {
            val bytes = ByteArrayProtokolCodec.encode(ObjectsData(list), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(list, data.list)
        }

        assert(emptyList(), ObjectsDataProtokolObject)
        assertFailsWith<IllegalArgumentException> {  assert(emptyList(), StrictObjectsDataProtokolObject) }

        val list = List(128) {
            val data = Data()
            data.bytes = Random.nextBytes(Random.nextInt(150))
            data.string = Random.nextBytes(Random.nextInt(150)).decodeToString()

            val complexData = ComplexData()
            complexData.name = if (it == 9) "test" else Random.nextBytes(Random.nextInt(150)).decodeToString()
            complexData.data = data
            complexData.bytes = Random.nextBytes(Random.nextInt(150))

            complexData
        }

        assert(list, ObjectsDataProtokolObject)
        assertFailsWith<IllegalArgumentException> { assert(list, StrictObjectsDataProtokolObject) }
    }

    @Test
    fun testParseError() {
        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(ObjectsData(emptyList()), ObjectsDataProtokolObject),
                StrictObjectsDataProtokolObject
            )
        }

        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(ObjectsData(List(1) {
                    val data = Data()
                    data.bytes = Random.nextBytes(Random.nextInt(150))
                    data.string = Random.nextBytes(Random.nextInt(150)).decodeToString()


                    val complexData = ComplexData()
                    complexData.name = "test"
                    complexData.data = data
                    complexData.bytes = Random.nextBytes(Random.nextInt(150))

                    complexData
                }), ObjectsDataProtokolObject),
                StrictObjectsDataProtokolObject
            )
        }
    }

}