package com.electrit.protokol

interface ProtokolObject<T> {

    val protokol: Protokol.(T) -> Unit

    fun create(): T

}