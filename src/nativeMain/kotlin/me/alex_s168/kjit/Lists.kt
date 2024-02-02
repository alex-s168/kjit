package me.alex_s168.kjit

fun <T> List<T>.reversedIterator(): Iterator<T> {
    var pos = size
    return iterator {
        pos --
        if (pos >= 0)
            yield(this@reversedIterator[pos])
    }
}