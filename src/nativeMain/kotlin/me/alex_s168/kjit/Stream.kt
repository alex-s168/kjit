package me.alex_s168.kjit

import kotlinx.cinterop.*
import me.alex_s168.kallok.Allocation

typealias Stream<T> = Iterator<T>

fun <T> Stream<T>.collect(dest: Array<T>, limit: Int = dest.size): Array<T> {
    var i = 0
    while (this.hasNext() && i < limit) {
        dest[i ++] = this.next()
    }
    return dest
}

fun <T> Stream<T>.collect(dest: MutableList<T> = mutableListOf()): MutableList<T> =
    dest.also { forEach { dest += it } }

@OptIn(ExperimentalForeignApi::class)
fun <T> Stream<Byte>.collect(dest: CPointer<ByteVar>, limit: Long): Long {
    var i = 0L
    while (this.hasNext() && i < limit) {
        dest[i ++] = this.next()
    }
    return i // size
}

@OptIn(ExperimentalForeignApi::class)
inline fun Stream<Byte>.collect(dest: CPointer<ByteVar>, limit: Int): Long =
    collect<Byte>(dest, limit.convert<Long>())

@OptIn(ExperimentalForeignApi::class)
inline fun Stream<Byte>.collect(allocation: Allocation, offset: Int = 0): Long {
    if (offset >= allocation.size)
        throw IndexOutOfBoundsException("Offset too big!")

    return collect((allocation.ptr + offset)!!, allocation.size)
}

@OptIn(ExperimentalForeignApi::class)
inline fun <T> Stream<Byte>.collect(dest: CPointer<ByteVar>, limit: ULong) =
    collect<T>(dest, limit.convert<Long>())

@OptIn(ExperimentalForeignApi::class)
inline fun <T> Stream<Byte>.collect(dest: CPointer<ByteVar>, limit: UInt) =
    collect<T>(dest, limit.convert<Long>())

fun Stream<String>.flatCollect(dest: StringBuilder = StringBuilder()): StringBuilder =
    dest.also { forEach { dest.append(it) } }

fun <T> Stream<Iterator<T>>.flatten(): Stream<T> =
    FlatteningStream<T>(this)

fun <T> stream(block: suspend SequenceScope<T>.() -> Unit): Stream<T> =
    iterator<T>(block)

fun <T> Stream<T>.then(block: () -> Unit): Stream<T> =
    OnLastElementStream(this, block)

fun <T> stream(vararg elements: T): Stream<T> =
    arrayOf(*elements).iterator()

private class FlatteningStream<T>(
    private val parent: Stream<Iterator<T>>
): Stream<T> {
    private var current: Iterator<T>? = null

    override fun hasNext(): Boolean =
        current?.hasNext() ?: parent.hasNext()

    override fun next(): T {
        while (current == null) {
            current = parent.next()
            if (!current!!.hasNext()) {
                current = null
            }
        }
        return current!!.next()
    }

}

private class OnLastElementStream<T>(
    private val parent: Stream<T>,
    private val end: () -> Unit,
): Stream<T> {
    override fun hasNext(): Boolean {
        if (!parent.hasNext()) {
            end()
            return false
        }
        return true
    }

    override fun next(): T =
        parent.next()
}