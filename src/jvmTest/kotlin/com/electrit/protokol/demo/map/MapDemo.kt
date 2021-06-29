package com.electrit.protokol.demo.map

import com.electrit.protokol.Protokol
import com.electrit.protokol.ProtokolMapEntry
import com.electrit.protokol.ProtokolObject

data class Data(
    var id: Int = 0,
    var name: String = "",
    var flag: Boolean = false
)

object DataProtokolObject : ProtokolObject<Data> {
    override val protokol: Protokol.(Data) -> Unit = {
        with(it) {
            INT(::id)
            STRING(::name)
            BOOLEAN(::flag)
        }
    }

    override fun create() = Data()
}

object MapProtokolObject1 : ProtokolObject<ProtokolMapEntry<String, Data?>> {
    override val protokol: Protokol.(ProtokolMapEntry<String, Data?>) -> Unit = {
        with(it) {
            STRING(::key)
            OBJECT(::value, DataProtokolObject)
        }
    }

    override fun create(): ProtokolMapEntry<String, Data?> = ProtokolMapEntry("", null)
}

class Model1(var map : Map<String, Data?> = emptyMap())

object Model1ProtokolObject : ProtokolObject<Model1> {
    override val protokol: Protokol.(Model1) -> Unit = {
        with(it) {
            MAP(::map, MapProtokolObject1)
        }
    }

    override fun create() = Model1()
}

class Model(var map : Map<String, String> = emptyMap())

object Model2ProtokolObject : ProtokolObject<Model> {
    override val protokol: Protokol.(Model) -> Unit = {
        with(it) {
            MAP(::map, MapProtokolObject)
        }
    }

    override fun create() = Model()
}

object MapProtokolObject : ProtokolObject<ProtokolMapEntry<String, String>> {
    override val protokol: Protokol.(ProtokolMapEntry<String, String>) -> Unit = {
        with(it) {
            STRING(::key)
            STRING(::value)
        }
    }

    override fun create() = ProtokolMapEntry("", "")
}