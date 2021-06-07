package com.electrit.protokol

object ByteArrayProtokolCodec {

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

    fun <T> encode(value: T, po: ProtokolObject<T>): ByteArray {
        val protokol = po.protokol
        val sizer = Sizer()
        sizer.protokol(value)
        val composer = ByteArrayProtokolComposer(sizer.size)
        composer.protokol(value)
        return composer.bytes
    }

    fun <T> decode(bytes: ByteArray, po: ProtokolObject<T>): T {
        val value = po.create()
        val protokol = po.protokol
        val parser = ByteArrayProtokolParser(bytes)
        parser.protokol(value)
        return value
    }

    private class Sizer : ProtokolComposer() {
        var size: Int = 0

        override fun composeBYTE(value: Byte, validator: (Byte) -> Unit) {
            size++
        }

        override fun composeBYTEARRAY(value: ByteArray, validator: (ByteArray) -> Unit) {
            composeSize(value.size)
            size += value.size
        }
    }

}

