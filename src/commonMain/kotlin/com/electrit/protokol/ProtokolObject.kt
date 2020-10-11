package com.electrit.protokol

interface ProtokolObject<T> {

    fun use(value: T, p: Protokol)

    fun create(): T

}