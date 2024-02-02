package me.alex_s168.kallok

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toCPointer
import kotlin.math.ceil

interface Allocator {
    fun alloc(size: Int): Allocation?

    @OptIn(ExperimentalForeignApi::class)
    fun alloc(size: Int, align: Int): Allocation? {
        val al = alloc(size + align) ?: return null
        val f = al.ptr.rawValue.toLong().toDouble()
        val x = ceil(f / align).toLong() * align
        val pt = x.toCPointer<ByteVar>()!!
        return SubAllocation(al, pt, size)
    }

    private class SubAllocation @OptIn(ExperimentalForeignApi::class) constructor(
        private val alloc: Allocation,
        override val ptr: CPointer<ByteVar>,
        override val size: Int
    ): Allocation {
        override fun free() {
            alloc.free()
        }
    }
}