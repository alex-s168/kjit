package me.alex_s168.kollektions

import kotlin.math.max

/**
 * A DoubleLinkedList of any other list type.
 */
class MultiList<T>(
    private val maxAutomaticLen: Int = 64,
    private val listProvider: () -> MutableList<T>,
): MutableList<T> {
    private var nextId = 0

    private val chunks = DoubleLinkedList<MutableList<T>>()
    private val ranges = RangeMap()

    override var size: Int = 0
        private set

    override fun clear() {
        chunks.clear()
        ranges.clear()
        size = 0
        nextId = 0
    }

    private fun addChunk(list: MutableList<T>) {
        chunks.add(list)
        ranges.map(nextId, size..<(size + list.size))
        size += list.size
        nextId++
    }

    private fun makeMutable(elements: Collection<T>): MutableList<T> =
        if (elements is MutableList) {
            elements
        } else {
            val new = listProvider()
            new.addAll(elements)
            new
        }

    override fun addAll(elements: Collection<T>): Boolean {
        addChunk(makeMutable(elements))
        return true
    }

    // TODO: make it consider split the "old" list of in between the indexes if the new list is much bigger than the old one (reduced mem copy)
    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        if (index <= 0) {
            ranges.remapAll { id, it ->
                (it.first + elements.size)..(it.last + elements.size)
            }
            val elem = makeMutable(elements)
            chunks.add(elem)
            ranges.map(nextId, 0..<(elements.size))
            size += elements.size
            nextId++
            return true
        }
        if (index >= size)
            return addAll(elements)

        val where = ranges.resolve(index)
            ?: throw IllegalStateException("THIS SHOULD NOT HAPPEN: MultiList:addAll:resolve")

        ((where.id + 1)..<nextId).forEach { id ->
            ranges.remap(id) {
                (it.first + elements.size)..(it.last + elements.size)
            }
        }

        chunks[where.id]
            .addAll(where.offset, elements)

        ranges.remap(where.id) {
            (it.first)..(it.last + elements.size)
        }

        size += elements.size

        return true
    }

    override fun add(index: Int, element: T) {
        addAll(index, mutableListOf(element))
    }

    override fun add(element: T): Boolean {
        if (chunks.isEmpty() || chunks.lastOrNull()?.size?.let { it >= maxAutomaticLen } == true) {
            if (chunks.size != nextId) throw IllegalStateException("SHOULD NOT HAPPEN (MuliList:add chunks.size != nextId)")
            val li = listProvider()
            chunks.add(li)
            ranges.map(nextId, size..size)
            nextId++
            size++
            return li.add(element)
        }
        size++
        ranges.remap(nextId - 1) { it.first..(it.last + 1) }
        return chunks.last().add(element)
    }

    private fun <R> transformed(index: Int, block: (list: MutableList<T>, index: Int) -> R): R {
        val where = ranges.resolve(index)
            ?: throw IndexOutOfBoundsException()
        return block(chunks[where.id], where.offset)
    }

    override fun get(index: Int): T =
        transformed(index) { list, ind ->
            list[ind]
        }

    override fun isEmpty(): Boolean =
        size == 0

    override fun iterator(): MutableIterator<T> =
        object : MutableIterator<T> {
            val prov = this@MultiList.chunks.iterator()
            var curr: MutableIterator<T>? = null
            var index = 0

            override fun hasNext(): Boolean {
                while (curr == null) {
                    if (!prov.hasNext())
                        return false

                    curr = prov.next().iterator()

                    if (!curr!!.hasNext())
                        curr = null
                }
                return curr!!.hasNext()
            }

            override fun next(): T {
                if (!hasNext())
                    throw Exception("No next element")
                index++
                return curr!!.next()
            }

            override fun remove() {
                if (curr == null)
                    throw IllegalStateException()

                this@MultiList.removeAt(index - 1)
            }
        }

    override fun listIterator(): MutableListIterator<T> {
        TODO("Not yet implemented")
    }

    override fun listIterator(index: Int): MutableListIterator<T> {
        TODO("Not yet implemented")
    }

    private fun swapChunks(a: Int, b: Int) {
        val aList = chunks[a]
        val bList = chunks[b]
        chunks[b] = aList
        chunks[a] = bList
        ranges.changeID(a, -165)
        ranges.changeID(b, a)
        ranges.changeID(-165, b)
    }

    override fun removeAt(index: Int): T {
        val where = ranges.resolve(index)
            ?: throw IndexOutOfBoundsException()

        val list = chunks[where.id]

        val emptyIDs = mutableSetOf<Int>()
        ranges.remapAll { id, it ->
            val new = if (id > where.id)
                (it.first-1)..(it.last-1)
            else if (id == where.id)
                (it.first)..(it.last - 1)
            else
                it

            if (new.first == 0 && new.last == -1) {
                emptyIDs.add(id)
            }

            new
        }

        size --

        var rev = nextId - 1
        emptyIDs.sortedDescending().forEach { id ->
            swapChunks(id, rev)
            rev --
        }
        // rev is now the new size without empty chunks
        repeat(nextId - 1) {}

        nextId = rev

        return list.removeAt(where.offset)
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        TODO("Not yet implemented")
    }

    override fun set(index: Int, element: T): T =
        transformed(index) { list, i ->
            list.set(i, element)
        }

    override fun retainAll(elements: Collection<T>): Boolean {
        val it = listIterator()
        var removed = false
        while (it.hasNext()) {
            if (it.next() !in elements) {
                it.remove()
                removed = true
            }
        }
        return removed
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        var rem = false
        for (x in elements) {
            if (remove(x))
                rem = true
        }
        return rem
    }

    override fun remove(element: T): Boolean {
        val i = indexOf(element)
        if (i == -1)
            return false

        removeAt(i)

        return true
    }

    override fun lastIndexOf(element: T): Int {
        var index = size - 1
        while (index > 0) {
            val x = this[index]
            if (x == element)
                return index
            index--
        }
        return -1
    }

    override fun indexOf(element: T): Int {
        chunks.forEachIndexed { index, chunk ->
            val i = chunk.indexOf(element)
            if (i != -1)
                return ranges.get(index).first().first + i
        }
        return -1
    }

    override fun containsAll(elements: Collection<T>): Boolean =
        elements.all { indexOf(it) != -1 }

    override fun contains(element: T): Boolean =
        indexOf(element) != -1

    fun debug() {
        println("MultiList debug:")
        println(" contents: $contents")
        println(" index map: $ranges")
        println(" chunks: ${chunks.contents}")
    }
}