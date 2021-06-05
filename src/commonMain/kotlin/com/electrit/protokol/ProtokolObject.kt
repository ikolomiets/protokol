package com.electrit.protokol

interface ProtokolObject<T> {

    val protokol: Protokol.(T) -> Unit
        get() = { use(it, this) }

    @Deprecated("replace with protokol: Protokol.(T) -> Unit", ReplaceWith("protokol: Protokol.(T) -> Unit"))
    fun use(value: T, p: Protokol): Unit = error("Deprecated by use(value: T)")

    fun create(): T

}