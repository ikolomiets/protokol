 [![Download](https://api.bintray.com/packages/ikolomiets/maven/protokol/images/download.svg) ](https://bintray.com/ikolomiets/maven/protokol/_latestVersion)

- [Introduction](#introduction)
- [Supported Types](#supported-types)
    - [BYTE](#byte)
    - [BYTEARRAY](#bytearray)
    - [STRING](#string)
    - [BOOLEAN](#boolean)
    - [BITSET8](#bitset8)
    - [SHORT, INT, LONG](#short-int-long)
    - [ENUM8 and ENUM16](#enum8-and-enum16)
    - [OBJECT](#object)
    - [List types](#list-types)
- [Variable Length Encoding](#variable-length-encoding)

<a name="introduction"></a> 
## Introduction

**Protokol** is a simple Kotlin Multiplatfrom library for data serialization that allows for efficient binary encoding.
**Protokol** only supports serialization of mutable class properties and uses reflection API for access.

To achieve efficient data encoding **Protokol** employs the concept of _dynamic format_ where values of previously
composed or parsed fields may affect the format of the fields that follow (in this document, Kotlin class properties
often be referred as _fields_). To define the logic of such serialization format **Protokol** offers type-safe DSL.

**Protokol** supports serialization of basic Kotlin types such as: `Byte`, `ByteArray`, `String`,
`Boolean`, `Short`, `Int`, `Long` and `Enum`. Also supported are aggregate types such as
`List<T>` and `OBJECT` (i.e. field of any class that supports **Protokol**). Lastly, **Protokol** has special
support for bitsets.

Here's an example of using **Protokol** to serialize simple `Result` class:

```Kotlin
data class Result(
    var errCode: Int = 0,
    var errText: String = "",
    var value: Boolean = false
)

object ResultProtokolObject : ProtokolObject<Result> {
    override fun use(value: Result, p: Protokol) = with (p) {
        with(value) {
            // Protokol DSL starts
            INT(::errCode)
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
```

Program's output:

```
bytes array size: 5
Result(errCode=0, errText=, value=true)
errBytes array size: 17
Result(errCode=1, errText=Result error, value=false)
```

All it takes to add **Protokol** serialization support to `Result` class is to implement `ProtokolObject<Result>`
interface as Kotlin object class. With `ResultProtokolObject` implementation, the **Protokol**'s provided
`ByteArrayProtokolCodec` can be used to encode (serialize) instance of `Result` to bytes, and to decode
(deserialize) from bytes to a new instance of `Result` (in this document the terms _serialize_, _compose_ and
_encode_, as well as _deserialize_, _parse_ and _decode_ are used interchangeably).

`ProtokolObject` interface has two methods:

```Kotlin
interface ProtokolObject<T> {
    fun use(value: T, p: Protokol)

    fun create(): T
}
```

The `create(): T` method is used during parsing to create a new instance of `T`.
The `use(value: T, p: Protokol)` method is where the **Protokol** format is defined.
Using Kotlin's `with()` function to have both `value: T` and `p: Protokol` parameters as implicit receivers,
together with `::` operator to reference **value** property as `KMutableProperty0<T>` allows to express serialization
format's logic as DSL-like code:

```Kotlin
INT(::errCode)
if (errCode > 0) {
    STRING(::errText)
} else {
    BOOLEAN(::value)
}
``` 

The code above defines serialization format as: first goes an "`Int` value bound to `Result.errCode` property";
after goes either a "`String` value bound to `Result.errText` property" or a "`Boolean` value bound to
`Result.value` property" conditional on `errCode` is a positive number or not.

Important quality of **Protokol**'s DSL is its applicability in both serialization and deserialization contexts.
This is possible because of `::` operator. Applied to a property `::` returns an instance of `KMutableProperty0<V>`
type. Reference to an instance of `KMutableProperty0<V>` allows for serialization code to _get_ bound property's
value, and for deserialization code to _set_ property's value.

In **Protokol** DSL, any type declaration clause can optionally be given `(T) -> Unit` lambda to perform validation of
the property's value. For invalid values lambda can throw the exception to stop further (de)serialization.
During serialization such lambda will be called with property's value as an argument before it will be deserialized.
When deserializing this lambda will be called with deserialized value before it will be set to a property.

This is how **Protokol** enforces non-negative values of `errCode: Int` by providing validation code block (lambda):

```Kotlin
INT(::errCode) { if (it < 0) throw IllegalArgumentException("errCode can't be negative: $it") }
if (errCode > 0) {
    STRING(::errText)
} else {
    BOOLEAN(::value)
}
```

<a name="supported-types"></a> 
## Supported Types

<a name="byte"></a> 
### BYTE

Declaration of Protokol's `BYTE` type serializes bound property of `Byte` type (a signed 8-bit integer) directly to a
single byte.

Here's an example of binding `b: Byte` property as Protokol's `BYTE` with optional validation block:

```Kotlin
BYTE(::b) { if (it.toInt() == 0) throw IllegalArgumentException("zero is not allowed") }
```

<a name="bytearray"></a> 
### BYTEARRAY

Similar to `BYTE`, Protokol's `BYTEARRAY` type serializes a property of `ByteArray` type directly to an array of bytes.
But there's an important difference - array's size must also be encoded and prepend actual bytes data.
This allows the parser to know how many bytes to parse, and also create a properly sized array to have them copied into.
To serialize an integer number of array's size **Protokol** uses a technique called _Variable Length Encoding_
(VLE for short). **Protokol** also uses VLE for a variety of its `List<T>` types to encode list's size.
VLE limits the maximum size of `ByteArray` to 1Gb.

This is how you bind `bytes: ByteArray` property as **Protokol**'s `BYTEARRAY`:

```Kotlin
BYTEARRAY(::bytes)
```

<a name="string"></a> 
### STRING

**Protokol**'s `STRING` type serializes `String` property as `ByteArray` assuming UTF-8 encoding.
Because of this `String` and `ByteArray` share the same encoding and limits. Specifically, maximum size of
`String` (in bytes) as imposed by Variable Length Encoding is 1Gb.

The example below shows how to bind `str: String` property as **Protokol**'s `STRING` with optional validation block:

```Kotlin
STRING(::str) { if (it.endsWith("ism")) throw IllegalArgumentException("Toxic!") }
```

<a name="boolean"></a> 
### BOOLEAN

Properties of `Boolean` type can be serialized using **Protokol**'s `BOOLEAN` type declaration. `BOOLEAN` encodes
boolean value as a single byte: `0` for `false` and `1` for `true`. In the case of a class with many `Boolean` properties
to be serialized you may consider using **Protokol**'s `BITSET8` type that allows using individual bits to store
boolean values.

This is how you declare **Protokol**'s `BOOLEAN` with bound `b: Boolean` property:

```Kotlin
BOOLEAN(::b)
```

<a name="bitset8"></a> 
### BITSET8

**Protokol** allows serializing a `Boolean` field as a specific bit of the single byte with its `BITSET8` type.

In the following example `BitsResult` class has three `Boolean` fields that will be serialized as bits #0, #1 and #5:

```Kotlin
data class BitsResult(
    var errCode: Int = 0,
    var errText: String = "",
    var flagA: Boolean = false, // bit #0
    var flagB: Boolean = false, // bit #1
    var flagC: Boolean = false  // bit #5
)

object BitsResultProtokolObject : ProtokolObject<BitsResult> {
    override fun use(value: BitsResult, p: Protokol) = with (p) {
        with(value) {
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
```

`BITSET8` can be useful not only for compressing `Boolean` fields into fewer bytes of serialized data, but also for
supporting existing binary formats that make use of bitsets.

<a name="short-int-long"></a>
### SHORT, INT, LONG

Properties of `Short` (16-bit signed integer), `Int` (32-bit signed integer) and `Long` (64-bit signed integer) types
can be serialized using **Protokol**'s respective `SHORT`, `INT` and `LONG` type declarations.
Bytes representing integers (2 bytes for `Short`, 4 bytes for `Int` and 8 bytes for `Long`) are big-endian encoded.

The following example shows **Protokol** declarations for `s: Short`, `i: Int` and `l: Long` properties, complemented
by optional validation code:

```Kotlin
SHORT(::s) { if (it.toInt() == 0) throw IllegalArgumentException("value can't be 0") }
INT(::i) { if (it < 0) throw IllegalArgumentException("value can't be negative") }
LONG(::l) { if (it > 10_000_000_000) throw IllegalArgumentException("value can't be greater than 10 Billions") }
``` 

<a name="enum8-and-enum16"></a>
### ENUM8 and ENUM16

**Protokol** supports serialization of `Enum` properties. For this enum constant's `ordinal: Int` value is used.
When deserializing enum's `ordinal` value is parsed as Int and then used as index in the provided `Enum.values()`
array to get corresponding enum constant. 

In **Protokol** enums can be declared as either `ENUM8` or `ENUM16` types. `ENUM8` uses a single byte to encode enum's
ordinal value and supports enums with up to 256 constants, where `ENUM16` uses two bytes to encode ordinal and supports
enums with up to 65536 constants.

Here's an example of **Protokol** support for `EnumResult` class with `errCode: ErrorCode` property of enum type:

```Kotlin
enum class ErrorCode {
    NoError, BadRequest, Unauthorized, Forbidden, NotFound, Timeout
}

data class EnumResult(
    var errCode: ErrorCode = ErrorCode.NoError,
    var errText: String = "",
    var value: Boolean = false
)

object EnumResultProtokolObject : ProtokolObject<EnumResult> {
    override fun use(value: EnumResult, p: Protokol) = with (p) {
        with(value) {
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
```

The following line of **Protokol** DSL declares `ENUM8` field, binds `errCode: ErrorCode` property to it, and provides
an array of `ErrorCode.values()` for deserialization:

```Kotlin
ENUM8(::errCode, ErrorCode.values())
```

<a name="object"></a>
### OBJECT

**Protokol**'s `OBJECT` type is used for serialization of fields of any reference type `T` that supports **Protokol**
(i.e. provides implementation of `ProtokolObject<T>`). `OBJECT` declaration takes corresponding `ProtokolObject`
instance as its second argument.

The following example of `ComplexResult` class demonstrates serialization of its `value: ComplexValue` property:

```Kotlin
data class ComplexResult(
    var errCode: Int = 0,
    var errText: String = "",
    var cv: ComplexValue? = null
)

object ComplexResultProtokolObject : ProtokolObject<ComplexResult> {
    override fun use(value: ComplexResult, p: Protokol) = with (p) {
        with(value) {
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
    override fun use(value: ComplexValue, p: Protokol) = with (p) {
        with(value) {
            // Protokol DSL starts
            STRING(::text)
            INT(::number)
        }
    }

    override fun create() = ComplexValue()
}
```

The `ComplexValueProtokolObject` class enables **Protokol** support for `ComplexValue` class.
`ComplexResultProtokolObject` uses `OBJECT` declaration to bind `cv: ComplexValue?` field:

```Kotlin
OBJECT(::cv, ComplexValueProtokolObject)
```

**Protokol**'s `OBJECT` is the only type that requires a nullable field for binding (i.e. field must be declared as `T?`).
This allows simple and efficient serialization of the optional fields. When serializing an `OBJECT` first goes
special _marker_ byte indicating whether it is a null or not. It only takes a single (marker) byte to serialize
`null` field.

<a name="list-types"></a>
### List types

**Protokol** supports serialization of properties of `List<T>` type, where `T` is one of the supported basic types:
`Byte`, `ByteArray`, `String`, `Boolean`, `Short`, `Int`, `Long`, `Enum`; or any other type which class has
**Protokol** support (that is has implementation of `ProtokolObject<T>` interface).

Such list types are named by adding plural 'S' suffix to **Protokol**'s basic type name: `BYTES`, `BYTEARRAYS`,
`STRINGS`, `BOOLEANS`, `SHORTS`, `INTS`, `LONGS`, `ENUM8S`, `ENUM16S` and `OBJECTS`.

List types declarations may have optional `sizeChecker: (Int) -> Unit` lambda in addition to
`validator: (T) -> Unit` lambda (also optional) that is used to validate serialized list's size as well as each of its
elements.

Similar to `BYTEARRAY`, the List types prepend serialized data with list's size integer encoded using
_Variable Length Encoding_. 

Example below has ListResult's `values: List<Boolean>` property bound as **Protokol**'s `BOOLEANS` field:

```Kotlin
data class ListResult(
    var errCode: Int = 0,
    var errText: String = "",
    var values: List<Boolean> = emptyList()
)

object ListResultProtokolObject : ProtokolObject<ListResult> {
    override fun use(value: ListResult, p: Protokol) = with(p) {
        with(value) {
            // Protokol DSL starts
            INT(::errCode) { if (it < 0) throw IllegalArgumentException("errCode can't be negative number: $it") }
            if (errCode > 0) {
                STRING(::errText)
            } else {
                BOOLEANS(::values, sizeChecker = { if (it > 10) throw IllegalArgumentException("list of values is too long") })
            }
        }
    }

    override fun create() = ListResult()
}
```

<a name="variable-length-encoding"></a>
## Variable Length Encoding

To parse `ByteArray` or `List<T>` the parser must know its size, that is how many bytes or list's elements will
follow. To allow for this, **Protokol** encodes integer number for size as prefix byte(s) to `ByteArray` or `List` data
using _Variable Length Encoding_. VLE allows for single byte to encode sizes up to 127, two bytes for sizes up to 16383
and four bytes for sizes up to 1073741824.

![Encoding of ByteArrays And Lists](doc/EncodingOfByteArraysAndLists.svg)

To know how many bytes encode size, parser first checks the highest bit (#7) of the first byte. If that bit is zero,
then size is encoded with a single byte using the remaining 7 bits. Otherwise the next bit is checked. If bit #6 is 0, then
size is encoded with 2 bytes using 14 bits. If bit #6 is 1, then size is encoded with 4 bytes using 30 bits.

![Variable Length Encoding](doc/VLE.svg)

VLE is especially useful for efficient encoding of many small strings (in **Protokol** backing type for serialized
`String` is `ByteArray`) and lists. If size of string's byte array or list is less than 128, it only takes extra
byte to encode it. Also, VLE naturally allows encoding of empty strings and lists with just a single zero byte.
