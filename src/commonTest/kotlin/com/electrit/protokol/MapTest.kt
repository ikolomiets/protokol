package com.electrit.protokol

import kotlin.test.Test
import kotlin.test.assertEquals

class MapTest {

    data class Model(var map: Map<Int, String>)

    object ModelProtokolObject : ProtokolObject<Model> {
        override fun use(value: Model, p: Protokol) = with(p) {
            with(value) {
                MAP(::map, SimpleMapEntryProtokolObject)
            }
        }

        override fun create() = Model(emptyMap())
    }

    object SimpleMapEntryProtokolObject : ProtokolObject<ProtokolMapEntry<Int, String>> {
        override fun use(value: ProtokolMapEntry<Int, String>, p: Protokol) = with(p) {
            with(value) {
                INT(::key)
                STRING(::value)
            }
        }

        override fun create(): ProtokolMapEntry<Int, String> = ProtokolMapEntry(0, "")
    }

    @Test
    fun testModel() {
        val inputMap: Map<Int, String> = mapOf(1 to "one", 2 to "two", 3 to "three")
        val inModel = Model(inputMap)
        val bytes: ByteArray = ByteArrayProtokolCodec.encode(inModel, ModelProtokolObject)
        val outModel = ByteArrayProtokolCodec.decode(bytes, ModelProtokolObject)
        assertEquals(inModel, outModel)
    }

    @Test
    fun testSimpleMap() {
        val inputMap: Map<Int, String> = mapOf(1 to "one", 2 to "two", 3 to "three")
        val bytes: ByteArray = ByteArrayProtokolCodec.encodeMap(inputMap, SimpleMapEntryProtokolObject)
        val outputMap: Map<Int, String> = ByteArrayProtokolCodec.decodeMap(bytes, SimpleMapEntryProtokolObject)
        assertEquals(inputMap, outputMap)
    }

    data class Data(var id: Int = 0, var name: String = "", var flag: Boolean = false)

    object DataProtokolObject : ProtokolObject<Data> {
        override fun use(value: Data, p: Protokol) = with(p) {
            with(value) {
                INT(::id)
                STRING(::name)
                BOOLEAN(::flag)
            }
        }

        override fun create() = Data()
    }

    object DataMapEntryProtokolObject : ProtokolObject<ProtokolMapEntry<String, Data?>> {
        override fun use(value: ProtokolMapEntry<String, Data?>, p: Protokol) = with(p) {
            with(value) {
                STRING(::key)
                OBJECT(::value, DataProtokolObject)
            }
        }

        override fun create(): ProtokolMapEntry<String, Data?> = ProtokolMapEntry("", null)
    }

    @Test
    fun testComplexMap() {
        val inputMap: Map<String, Data> = mapOf(*Array(128) {
            "key$it" to Data(it, "name$it", it % 2 == 0)
        })

        val bytes = ByteArrayProtokolCodec.encodeMap(inputMap, DataMapEntryProtokolObject)
        val outputMap = ByteArrayProtokolCodec.decodeMap(bytes, DataMapEntryProtokolObject)
        assertEquals(inputMap, outputMap)
    }

    @Test
    fun testComplexMapWithNulls() {
        val inputMap: Map<String, Data?> = mapOf(*Array(128) {
            "key$it" to if (it % 10 == 0) null else Data(it, "name$it", it % 2 == 0)
        })

        val bytes: ByteArray = ByteArrayProtokolCodec.encodeMap(inputMap, DataMapEntryProtokolObject)
        val outputMap: Map<String, Data?> = ByteArrayProtokolCodec.decodeMap(bytes, DataMapEntryProtokolObject)
        assertEquals(inputMap, outputMap)
    }

}