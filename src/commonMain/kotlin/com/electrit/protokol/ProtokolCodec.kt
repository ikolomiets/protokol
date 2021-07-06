package com.electrit.protokol

abstract class ProtokolCodec<D> {

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
    ): D {
        val listWrapper = ListWrapper(value)
        return encode(listWrapper, ListWrapperProtokolObject(po, sizeChecker, validator))
    }

    fun <T> decodeList(
        data: D,
        po: ProtokolObject<T>,
        sizeChecker: (Int) -> Unit = {},
        validator: T.() -> Unit = {}
    ): List<T> {
        val listWrapper = decode(data, ListWrapperProtokolObject(po, sizeChecker, validator))
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

    fun <K, V> encodeMap(value: Map<K, V>, po: ProtokolObject<ProtokolMapEntry<K, V>>): D {
        val mapWrapper = MapWrapper(value)
        return encode(mapWrapper, MapWrapperProtokolObject(po))
    }

    fun <K, V> decodeMap(data: D, po: ProtokolObject<ProtokolMapEntry<K, V>>): Map<K, V> {
        val mapWrapper = decode(data, MapWrapperProtokolObject(po))
        return mapWrapper.value
    }

    fun <T> encode(value: T, po: ProtokolObject<T>): D {
        val protokol = po.protokol
        val size = Sizer().apply { protokol(value) }.size
        val data = allocate(size)
        val composer = createComposer(data)
        composer.protokol(value)
        return data
    }

    fun <T> decode(data: D, po: ProtokolObject<T>): T {
        val value = po.create()
        val protokol = po.protokol
        val parser = createParser(data)
        parser.protokol(value)
        return value
    }

    internal abstract fun allocate(size: Int): D

    internal abstract fun createComposer(data: D): StandardProtokolComposer

    internal abstract fun createParser(data: D): StandardProtokolParser

    private class Sizer : StandardProtokolComposer() {
        var size: Int = 0

        override fun composeBYTE(value: Byte) {
            size++
        }

        override fun composeBYTEARRAY(value: ByteArray) {
            composeSize(value.size)
            size += value.size
        }
    }

}

