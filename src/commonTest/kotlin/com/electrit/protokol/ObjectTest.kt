package com.electrit.protokol

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ObjectTest {

    enum class Enum8 { INST1, INST2, INST3 }

    enum class Enum16 { INST1, INST2, INST3 }

    @Suppress("EqualsOrHashCode")
    class Data(
        var byte: Byte = 0,
        var bytes: ByteArray = ByteArray(0),
        var boolean: Boolean = false,
        var string: String = "",
        var short: Short = 0,
        var int: Int = 0,
        var long: Long = 0,
        var enum8: Enum8 = Enum8.INST1,
        var enum16: Enum16 = Enum16.INST1,
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Data

            if (byte != other.byte) return false
            if (!bytes.contentEquals(other.bytes)) return false
            if (boolean != other.boolean) return false
            if (string != other.string) return false
            if (short != other.short) return false
            if (int != other.int) return false
            if (long != other.long) return false
            if (enum8 != other.enum8) return false
            if (enum16 != other.enum16) return false

            return true
        }
    }

    object DataProtokolObject : ProtokolObject<Data> {
        override fun use(value: Data, p: Protokol) = with(p) {
            with(value) {
                BYTE(::byte)
                BYTEARRAY(::bytes)
                BOOLEAN(::boolean)
                STRING(::string)
                SHORT(::short)
                INT(::int)
                LONG(::long)
                ENUM8(::enum8, Enum8.values())
                ENUM16(::enum16, Enum16.values())
            }
        }

        override fun create() = Data()
    }

    @Suppress("EqualsOrHashCode")
    class ComplexData (
        var id: Int = 0,
        var verified: Boolean = false,
        var optData: Data? = null,
        var name: String = "",
        var reqData: Data? = null,
        var bytes: ByteArray = ByteArray(0),
        var enum16: Enum16 = Enum16.INST1
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as ComplexData

            if (id != other.id) return false
            if (verified != other.verified) return false
            if (optData != other.optData) return false
            if (name != other.name) return false
            if (reqData != other.reqData) return false
            if (!bytes.contentEquals(other.bytes)) return false
            if (enum16 != other.enum16) return false

            return true
        }
    }

    open class AbstractComplexDataProtokolObject(private val validate: Boolean) : ProtokolObject<ComplexData> {
        override fun use(value: ComplexData, p: Protokol) = with(p) {
            with(value) {
                INT(::id)
                BOOLEAN(::verified)
                OBJECT(::optData, DataProtokolObject) { if (validate && this != null) throw IllegalArgumentException("data must be null") }
                STRING(::name)
                OBJECT(::reqData, DataProtokolObject) {
                    if (validate && (this == null || string == "covid-19"))
                        throw IllegalArgumentException("null and virus are not allowed")
                }
                BYTEARRAY(::bytes)
                ENUM16(::enum16, Enum16.values())
            }
        }

        override fun create() = ComplexData()
    }

    object ComplexDataProtokolObject : AbstractComplexDataProtokolObject(false)
    object StrictComplexDataProtokolObject : AbstractComplexDataProtokolObject(true)

    @Test
    fun test() {
        fun assert(value: ComplexData, po: AbstractComplexDataProtokolObject) {
            val bytes = ByteArrayProtokolCodec.encode(value, po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(value, data)
        }

        val value = ComplexData()
        value.id = Random.nextInt()
        value.verified = Random.nextBoolean()
        value.name = Random.nextBytes(Random.nextInt(150)).decodeToString()
        value.bytes = Random.nextBytes(150)
        value.enum16 = Enum16.values()[Random.nextInt(Enum16.values().size)]
        assertFailsWith<IllegalArgumentException> { assert(value, StrictComplexDataProtokolObject) }

        // test validation on parse
        val bytes = ByteArrayProtokolCodec.encode(value, ComplexDataProtokolObject)
        assertFailsWith<IllegalArgumentException> { ByteArrayProtokolCodec.decode(bytes, StrictComplexDataProtokolObject) }

        value.reqData = Data()
        value.reqData?.byte = Random.nextInt().toByte()
        value.reqData?.bytes = Random.nextBytes(130)
        value.reqData?.boolean = Random.nextBoolean()
        value.reqData?.string = "covid-19"
        value.reqData?.short = Random.nextInt(Short.MAX_VALUE + 1).toShort()
        value.reqData?.int = Random.nextInt()
        value.reqData?.long = Random.nextLong()
        value.reqData?.enum8 = Enum8.values()[Random.nextInt(Enum8.values().size)]
        value.reqData?.enum16 = Enum16.values()[Random.nextInt(Enum16.values().size)]

        assertFailsWith<IllegalArgumentException> { assert(value, StrictComplexDataProtokolObject) }

        value.reqData?.string = "Sputnik-V"
        assert(value, ComplexDataProtokolObject)
    }

}