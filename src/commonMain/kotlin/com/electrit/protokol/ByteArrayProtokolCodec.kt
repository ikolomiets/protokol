package com.electrit.protokol

object ByteArrayProtokolCodec {

    private class ListWrapper<T>(var value: List<T> = emptyList())

    private class ListWrapperProtokolObject<T>(val po: ProtokolObject<T>) : ProtokolObject<ListWrapper<T>> {
        override fun use(value: ListWrapper<T>, p: Protokol) = with(p) {
            with(value) {
                OBJECTS(::value, po)
            }
        }

        override fun create() = ListWrapper<T>()
    }

    fun <T> encodeList(value: List<T>, po: ProtokolObject<T>): ByteArray {
        val listWrapper = ListWrapper(value)
        return encode(listWrapper, ListWrapperProtokolObject(po))
    }

    fun <T> decodeList(bytes: ByteArray, po: ProtokolObject<T>): List<T> {
        val listWrapper = decode(bytes, ListWrapperProtokolObject(po))
        return listWrapper.value
    }

    private class MapWrapper<K, V>(var value: Map<K, V> = emptyMap())

    private class MapWrapperProtokolObject<K, V>(
        val po: ProtokolObject<ProtokolMapEntry<K, V>>
    ) : ProtokolObject<MapWrapper<K, V>> {
        override fun use(value: MapWrapper<K, V>, p: Protokol) = with(p) {
            with(value) {
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
        val sizer = Sizer()
        po.use(value, sizer)
        val composer = ByteArrayProtokolComposer(sizer.size)
        po.use(value, composer)
        return composer.bytes
    }

    fun <T> decode(bytes: ByteArray, po: ProtokolObject<T>): T {
        val value = po.create()
        po.use(value, ByteArrayProtokolParser(bytes))
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

