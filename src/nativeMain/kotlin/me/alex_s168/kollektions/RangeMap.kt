package me.alex_s168.kollektions

open class RangeMap {
    private val entries = mutableMapOf<Int, IntRange>()

    fun map(id: Int, range: IntRange) {
        entries[id] = range
    }

    fun unmap(id: Int) {
        entries.remove(id)
    }

    fun remap(id: Int, transform: (IntRange) -> IntRange) =
        map(id, transform(entries[id]!!))

    fun remapAll(transform: (IntRange) -> IntRange) =
        entries.keys.forEach { remap(it, transform) }

    fun getEmpty(): Sequence<Int> {
        val iter = entries.iterator()
        return sequence {
            if (iter.hasNext()) {
                val x = iter.next()
                if (x.value.isEmpty())
                    yield(x.key)
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
}