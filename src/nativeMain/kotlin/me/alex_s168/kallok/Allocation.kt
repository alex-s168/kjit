package me.alex_s168.kallok

import kotlinx.cinterop.*
import me.alex_s168.kjit.tricks.make_exec

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

    fun free()
}