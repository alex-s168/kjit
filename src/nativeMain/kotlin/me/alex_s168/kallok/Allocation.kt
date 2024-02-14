package me.alex_s168.kallok

import kotlinx.cinterop.*
import me.alex_s168.kjit.tricks.make_exec
import platform.posix.memset

@OptIn(ExperimentalForeignApi::class)
interface Allocation {
    val size: Int
    val ptr: CPointer<ByteVar>

    fun freezeAndMakeExec(offset: Int = 0, length: Int = size) {
        if (length + offset > size)
            throw IndexOutOfBoundsException("$length + $offset > $size")

        make_exec(ptr + offset.convert(), length.convert())
    }

    fun <T: Function<Unit>> functionPointerTo(offset: Int): CPointer<CFunction<T>> {
        if (offset >= size)
            throw IndexOutOfBoundsException("Offset is out of bounds!")

        return (ptr + offset)!!.reinterpret()
    }

    fun set(value: Byte, range: IntRange = 0..size) {
        if (range.last > size || range.first > size || range.first < 0 || range.last < 0)
            throw IndexOutOfBoundsException()

        memset(ptr + range.first, value.convert(), range.last.convert())
    }

    fun free()
}