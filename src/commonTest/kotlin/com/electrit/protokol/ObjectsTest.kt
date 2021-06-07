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
        override val protokol: Protokol.(Data) -> Unit = {
            with(it) {
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
        override val protokol: Protokol.(ComplexData) -> Unit = {
            with(it) {
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

    data class ObjectsData(var list: List<ComplexData> = emptyList())

    object ObjectsDataProtokolObject : ProtokolObject<ObjectsData> {
        override val protokol: Protokol.(ObjectsData) -> Unit = {
            with(it) {
                OBJECTS(::list, ComplexDataProtokolObject)
            }
        }

        override fun create() = ObjectsData()
    }

    private val sizeChecker: (Int) -> Unit = { size ->
        if (size == 0) throw IllegalArgumentException("size can't be 0")
    }

    private val validator: ComplexData.() -> Unit = {
        if (name == "test") throw IllegalArgumentException("List elements can't have name='test'")
    }

    @Test
    fun test() {
        fun assert(list: List<ComplexData>) {
            val bytes = ByteArrayProtokolCodec.encodeList(list, ComplexDataProtokolObject)
            val decodedList = ByteArrayProtokolCodec.decodeList(bytes, ComplexDataProtokolObject)
            assertEquals(list, decodedList)
        }

        fun strictAssert(list: List<ComplexData>) {
            val bytes = ByteArrayProtokolCodec.encodeList(list, ComplexDataProtokolObject, sizeChecker, validator)
            val decodedList = ByteArrayProtokolCodec.decodeList(bytes, ComplexDataProtokolObject)
            assertEquals(list, decodedList)
        }

        assert(emptyList())
        assertFailsWith<IllegalArgumentException> {  strictAssert(emptyList()) }

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

        val data = ObjectsData(list)
        val bytes = ByteArrayProtokolCodec.encode(data, ObjectsDataProtokolObject)
        val decodedData = ByteArrayProtokolCodec.decode(bytes, ObjectsDataProtokolObject)
        assertEquals(data, decodedData)

        assert(list)
        assertFailsWith<IllegalArgumentException> { strictAssert(list) }
    }

    @Test
    fun testParseError() {
        assertFailsWith<IllegalArgumentException> {
            val bytes = ByteArrayProtokolCodec.encodeList(emptyList(), ComplexDataProtokolObject)
            ByteArrayProtokolCodec.decodeList(bytes, ComplexDataProtokolObject, sizeChecker, validator)
        }

        assertFailsWith<IllegalArgumentException> {
            val bytes = ByteArrayProtokolCodec.encodeList(List(1) {
                val data = Data()
                data.bytes = Random.nextBytes(Random.nextInt(150))
                data.string = Random.nextBytes(Random.nextInt(150)).decodeToString()


                val complexData = ComplexData()
                complexData.name = "test"
                complexData.data = data
                complexData.bytes = Random.nextBytes(Random.nextInt(150))

                complexData
            }, ComplexDataProtokolObject)
            ByteArrayProtokolCodec.decodeList(bytes, ComplexDataProtokolObject, sizeChecker, validator)
        }
    }

}