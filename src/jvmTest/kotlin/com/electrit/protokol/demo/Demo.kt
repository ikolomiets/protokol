package com.electrit.protokol.demo

import com.electrit.protokol.ByteArrayProtokolCodec
import com.electrit.protokol.Protokol
import com.electrit.protokol.ProtokolObject

data class User(var id: Long, var name: String)

object UserProtokolObject : ProtokolObject<User> {
    override val protokol: Protokol.(User) -> Unit = {
        with(it) {
            LONG(::id)
            STRING(::name)
        }
    }

    override fun create() = User(0, "")
}

data class Model(var users: List<User>)

object ModelProtokolObject : ProtokolObject<Model> {
    override val protokol: Protokol.(Model) -> Unit = {
        with(it) {
            OBJECTS(::users, UserProtokolObject)
        }
    }

    override fun create() = Model(emptyList())
}

data class ComplexResult(
    var errCode: Int = 0,
    var errText: String = "",
    var cv: ComplexValue? = null
)

object ComplexResultProtokolObject : ProtokolObject<ComplexResult> {
    override val protokol: Protokol.(ComplexResult) -> Unit = {
        with(it) {
            // Protokol DSL starts
            INT(::errCode)
            if (errCode > 0) {
                STRING(::errText)
            } else {
                OBJECT(::cv, ComplexValueProtokolObject)
            }
        }
    }

    override fun create() = ComplexResult()
}

data class ComplexValue(
    var text: String = "",
    var number: Int = 0
)

object ComplexValueProtokolObject : ProtokolObject<ComplexValue> {
    override val protokol: Protokol.(ComplexValue) -> Unit = {
        with(it) {
            // Protokol DSL starts
            STRING(::text)
            INT(::number)
        }
    }

    override fun create() = ComplexValue()
}

data class ListResult(
    var errCode: Int = 0,
    var errText: String = "",
    var values: List<Boolean> = emptyList()
)

object ListResultProtokolObject : ProtokolObject<ListResult> {
    override val protokol: Protokol.(ListResult) -> Unit = {
        with(it) {
            // Protokol DSL starts
            INT(::errCode) { errCode -> if (errCode < 0) throw IllegalArgumentException("errCode can't be negative number: $errCode") }
            if (errCode > 0) {
                STRING(::errText)
            } else {
                BOOLEANS(
                    ::values,
                    sizeChecker = { size -> if (size > 10) throw IllegalArgumentException("list of values is too long") })
            }
        }
    }

    override fun create() = ListResult()
}

enum class ErrorCode {
    NoError, BadRequest, Unauthorized, Forbidden, NotFound, Timeout
}

data class EnumResult(
    var errCode: ErrorCode = ErrorCode.NoError,
    var errText: String = "",
    var value: Boolean = false
)

object EnumResultProtokolObject : ProtokolObject<EnumResult> {
    override val protokol: Protokol.(EnumResult) -> Unit = {
        with(it) {
            // Protokol DSL starts
            ENUM8(::errCode, ErrorCode.values())
            if (errCode != ErrorCode.NoError) {
                STRING(::errText)
            } else {
                BOOLEAN(::value)
            }
        }
    }

    override fun create() = EnumResult()
}

data class BitsResult(
    var errCode: Int = 0,
    var errText: String = "",
    var flagA: Boolean = false, // bit #0
    var flagB: Boolean = false, // bit #1
    var flagC: Boolean = false  // bit #5
)

object BitsResultProtokolObject : ProtokolObject<BitsResult> {
    override val protokol: Protokol.(BitsResult) -> Unit = {
        with(it) {
            // Protokol DSL starts
            INT(::errCode)
            if (errCode > 0) {
                STRING(::errText)
            } else {
                BITSET8(::flagA, ::flagB, b5 = ::flagC)
            }
        }
    }

    override fun create() = BitsResult()
}

data class Result(
    var errCode: Int = 0,
    var errText: String = "",
    var value: Boolean = false
)

object ResultProtokolObject : ProtokolObject<Result> {
    override val protokol: Protokol.(Result) -> Unit = {
        with(it) {
            // Protokol DSL starts
            INT(::errCode) { errorCode -> if (errorCode < 0) throw IllegalArgumentException("errCode can't be negative number: $errorCode") }
            if (errCode > 0) {
                STRING(::errText)
            } else {
                BOOLEAN(::value)
            }
        }
    }

    override fun create() = Result()
}

fun main() {
    val result = Result(value = true)
    val bytes: ByteArray = ByteArrayProtokolCodec.encode(result, ResultProtokolObject)
    println("bytes array size: ${bytes.size}")
    val decodedResult: Result = ByteArrayProtokolCodec.decode(bytes, ResultProtokolObject)
    println(decodedResult)

    val errResult = Result(errCode = 1, errText = "Result error")
    val errBytes: ByteArray = ByteArrayProtokolCodec.encode(errResult, ResultProtokolObject)
    println("errBytes array size: ${errBytes.size}")
    val decodedErrResult: Result = ByteArrayProtokolCodec.decode(errBytes, ResultProtokolObject)
    println(decodedErrResult)
}