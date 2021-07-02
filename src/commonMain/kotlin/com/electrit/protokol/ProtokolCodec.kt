package com.electrit.protokol

abstract class ProtokolCodec {

    private class ListWrapper<T>(var value: List<T> = emptyList())

    private class ListWrapperProtokolObject<T>(
        val po: ProtokolObject<T>,
        val sizeChecker: (Int) -> Unit,
        val validator: T.() -> Unit
    ) : ProtokolObject<ListWrapper<T>> {
        override val protokol: Protokol.(ListWrapper<T>) -> Unit = {
            with(it) {
                OBJECTS(::value, po, sizeChecker, validator)
            }
        }

        override fun create() = ListWrapper<T>()
    }

    fun <T> encodeList(
        value: List<T>,
        po: ProtokolObject<T>,
        sizeChecker: (Int) -> Unit = {},
        validator: T.() -> Unit = {}
    ): ByteArray {
        val listWrapper = ListWrapper(value)
        return encode(listWrapper, ListWrapperProtokolObject(po, sizeChecker, validator))
    }

    fun <T> decodeList(
        bytes: ByteArray,
        po: ProtokolObject<T>,
        sizeChecker: (Int) -> Unit = {},
        validator: T.() -> Unit = {}
    ): List<T> {
        val listWrapper = decode(bytes, ListWrapperProtokolObject(po, sizeChecker, validator))
        return listWrapper.value
    }

    private class MapWrapper<K, V>(var value: Map<K, V> = emptyMap())

    private class MapWrapperProtokolObject<K, V>(
        val po: ProtokolObject<ProtokolMapEntry<K, V>>
    ) : ProtokolObject<MapWrapper<K, V>> {
        override val protokol: Protokol.(MapWrapper<K, V>) -> Unit = {
            with(it) {
                MAP(::value, po)
            }
        }

        override fun create() = MapWrapper<K, V>()
    }

    fun <K, V> encodeMap(value: Map<K, V>, po: ProtokolObject<ProtokolMapEntry<K, V>>): ByteArray {
        val mapWrapper = MapWrapper(value)
        return encode(mapWrapper, MapWrapperProtokolObject(po))
    }

    fun <K, V> decodeMap(bytes: ByteArray, po: ProtokolObject<ProtokolMapEntry<K, V>>): Map<K, V> {
        val mapWrapper = decode(bytes, MapWrapperProtokolObject(po))
        return mapWrapper.value
    }

    abstract fun <T> encode(value: T, po: ProtokolObject<T>): ByteArray

    abstract fun <T> decode(bytes: ByteArray, po: ProtokolObject<T>): T

    internal class Sizer : ProtokolComposer() {
        var size: Int = 0

        override fun composeByte(value: Byte) {
            size++
        }

        override fun composeByteArray(value: ByteArray) {
            composeSize(value.size)
            size += value.size
        }
    }

}

