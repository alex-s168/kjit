package me.alex_s168.kollektions

open class RangeMap {
    private val entries = mutableListOf<Pair<Int, IntRange>>()

    fun map(id: Int, range: IntRange) {
        entries.add(id to range)
    }

    fun get(id: Int): Iterable<IntRange> {
        val out = mutableListOf<IntRange>()
        for (x in entries) {
            if (x.first == id)
                out.add(x.second)
        }
        return out
    }

    fun unmap(id: Int): Iterable<IntRange> {
        val out = mutableListOf<IntRange>()
        val it = entries.listIterator()
        while (it.hasNext()) {
            val x = it.next()
            if (x.first == id) {
                out.add(x.second)
                it.remove()
            }
        }
        return out
    }

    fun remap(id: Int, transform: (IntRange) -> IntRange) {
        val old = unmap(id)
        old.forEach {
            map(id, transform(it))
        }
    }

    val all get() = entries.toList()

    fun decrementIDAllAfter(id: Int) {
        val it = entries.listIterator()
        while (it.hasNext()) {
            val x = it.next()
            if (x.first > id) {
                it.set(x.first - 1 to x.second)
            }
        }
    }

    inline fun mapAdditional(id: Int, vararg ranges: IntRange) {
        ranges.forEach {
            map(id, it)
        }
    }

    fun remapAll(transform: (Int, IntRange) -> IntRange) {
        val iter = entries.listIterator()
        while (iter.hasNext()) {
            val x = iter.next()
            iter.set(x.first to transform(x.first, x.second))
        }
    }

    fun changeID(old: Int, new: Int) {
        val iter = entries.listIterator()
        while (iter.hasNext()) {
            val x = iter.next()
            if (x.first == old)
                iter.set(new to x.second)
        }
    }

    fun getEmpty(): Sequence<Int> {
        val iter = entries.iterator()
        return sequence {
            if (iter.hasNext()) {
                val x = iter.next()
                if (x.second.isEmpty())
                    yield(x.first)
            }
        }
    }

    fun clear() {
        entries.clear()
    }

    data class ResolveResult(
        val id: Int,
        val offset: Int,
    )

    fun resolve(num: Int): ResolveResult? {
        entries.forEach { (id, range) ->
            if (num in range)
                return ResolveResult(id, num - range.first)
        }
        return null
    }

    override fun toString(): String =
        entries.joinToString(prefix = "[", postfix = "]") { (k, v) ->
            "id $k: $v"
        }
}