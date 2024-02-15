package me.alex_s168.kollektions

fun <T> Iterator<Iterable<T>>.flatten(): Iterator<T> =
    FlatteningIteratorIterable(this)

fun <T> Iterable<Iterable<T>>.flatten(): Iterable<T> =
    Iterable { iterator().flatten() }

private class FlatteningIteratorIterable<T>(
    private val iterator: Iterator<Iterable<T>>
): Iterator<T> {
    private var current: Iterator<T>? = null

    override fun hasNext(): Boolean {
        while (current?.hasNext() == false) {
            if (!iterator.hasNext())
                return false
            current = iterator.next().iterator()
        }
        return true
    }

    override fun next(): T {
        if (!hasNext())
            throw Exception()

        return current!!.next()
    }
}

fun <T> Iterator<Iterator<T>>.flatten(): Iterator<T> =
    FlatteningIteratorIterator(this)

fun <T> Iterable<Iterator<T>>.flatten(): Iterable<T> =
    Iterable { iterator().flatten() }

private class FlatteningIteratorIterator<T>(
    private val iterator: Iterator<Iterator<T>>
): Iterator<T> {
    private var current: Iterator<T>? = null

    override fun hasNext(): Boolean {
        while (current?.hasNext() == false) {
            if (!iterator.hasNext())
                return false
            current = iterator.next()
        }
        return true
    }

    override fun next(): T {
        if (!hasNext())
            throw Exception()

        return current!!.next()
    }
}