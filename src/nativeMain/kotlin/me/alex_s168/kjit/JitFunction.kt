package me.alex_s168.kjit

import kotlinx.cinterop.*
import kotlin.concurrent.AtomicNativePtr

@OptIn(ExperimentalForeignApi::class)
value class JitFunction internal constructor(
    val nativePtr: AtomicNativePtr
) {
    fun <R, F: Function<R>> get(): CPointer<CFunction<F>> =
        nativePtr
            .value
            .toLong()
            .toCPointer()!!
}