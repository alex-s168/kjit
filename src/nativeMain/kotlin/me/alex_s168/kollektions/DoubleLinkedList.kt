package me.alex_s168.kollektions

import kotlin.math.max

class DoubleLinkedList<E>: MutableList<E>, MutableLinkedCollection<E> {
    private var parent: DoubleLinkedList<E>? = null

    override var size: Int = 0
        private set (it) {
            val diff = it - field
            parent?.let {
                it.size += diff
            }
            field = it
        }

    private var start: Entry<E>? = null
        set (v) {
            parent?.let {
                if (it.start == field)
                    it.start = v
            }
            field = v
        }

    private var end: Entry<E>? = null
        set (v) {
            parent?.let {
                if (it.end == field)
                    it.end = v
            }
            field = v
        }

    override fun clear() {
        start = null
        end = null
    }

    private data class ToEntryResult<E>(
        val first: Entry<E>,
        val last: Entry<E>,
        val count: Int
    )

    private fun Iterator<E>.toEntry(): ToEntryResult<E>? {
        if (!hasNext())
            return null

        val first = Entry(null, next(), null)
        var last = first
        var count = 1

        while (hasNext()) {
            val t = Entry(last, next(), null)
            last.next = t
            last = t
            count ++
        }

        return ToEntryResult(first, last, count)
    }

    private fun entryAt(index: Int): Entry<E>? {
        if (index < 0)
            return null

        if (index >= size)
            return null

        return if (index > size / 2) {
            // search from back
            var pos = size - 1
            var entry = end
            while (pos != index) {
                pos --
                entry = entry?.previous
            }
            entry
        } else {
            // search from front
            var pos = 0
            var entry = start
            while (pos != index) {
                pos ++
                entry = entry?.next
            }
            entry
        }
    }

    override fun addAll(elements: Collection<E>): Boolean {
        val entry = elements
            .iterator()
            .toEntry()
            ?: return false

        if (end != null)
            end!!.next = entry.first
        else
            start = entry.first

        end = entry.last
        size += entry.count

        return true
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        val entry = entryAt(index)
        if (entry == null && index > 0) {
            // append to end
            return addAll(elements)
        }

        val elementsResult = elements
            .iterator()
            .toEntry()
            ?: return false

        if (entry == null) {
            // add to the beginning
            elementsResult.last.next = start
            start = elementsResult.first
            if (end == null)
                end = elementsResult.last
            size += elementsResult.count

            return true
        }

        val prev = entry.previous ?: entry

        prev.next = elementsResult.first
        elementsResult.last.next = entry
        entry.previous = elementsResult.last
        size += elementsResult.count

        return true
    }

    override fun add(index: Int, element: E) {
        // TODO: do better
        addAll(index, listOf(element))
    }

    override fun add(element: E): Boolean {
        val entry = Entry(null, element, null)

        if (end == null) {
            start = entry
        } else {
            end!!.next = entry
            entry.previous = end
        }

        end = entry

        size ++
        return true
    }

    override fun get(index: Int): E =
        entryAt(index)?.element
            ?: throw IndexOutOfBoundsException()

    override fun isEmpty(): Boolean =
        size == 0

    override fun iterator(): MutableIterator<E> =
        Iter(start, 0, this)

    override fun listIterator(): MutableListIterator<E> =
        Iter(start, 0, this)

    override fun listIterator(index: Int): MutableListIterator<E> =
        Iter(entryAt(index), index, this)

    private class Iter<E>(
        private var entry: Entry<E>?,
        private var index: Int,
        private val parent: DoubleLinkedList<E>
    ): MutableListIterator<E> {
        override fun hasNext(): Boolean =
            entry != null

        override fun hasPrevious(): Boolean =
            entry?.previous != null

        private var op: Entry<E>? = null

        override fun next(): E {
            op = entry
            entry = entry?.next
            index ++
            return op?.element
                ?: throw NoSuchElementException()
        }

        override fun nextIndex(): Int =
            index

        override fun previous(): E {
            op = entry
            entry = entry?.previous
            index --
            return op?.element
                ?: throw NoSuchElementException()
        }

        override fun previousIndex(): Int =
            index - 1

        override fun remove() {
            op ?: throw IllegalStateException("next() or previous() has not been called yet")

            parent.removeEntry(op!!)
        }

        override fun set(element: E) {
            op ?: throw IllegalStateException("next() or previous() has not been called yet")
            op?.element = element
        }

        override fun add(element: E) {
            op ?: throw IllegalStateException("next() or previous() has not been called yet")
            val e = Entry(op, element, op?.next)
            op?.previous?.next = e
            op?.next?.previous = e
            parent.size ++
        }
    }

    private fun removeEntry(e: Entry<E>) {
        e.previous?.next = e.next
        e.next?.previous = e.previous
        size --

        if (e == start)
            start = e.next

        if (e == end)
            end = e.previous
    }

    override fun removeAt(index: Int): E {
        val e = entryAt(index)
            ?: throw IndexOutOfBoundsException()

        removeEntry(e)

        return e.element
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> {
        val start = entryAt(fromIndex)
            ?: throw IndexOutOfBoundsException()

        val end = entryAt(toIndex)
            ?: throw IndexOutOfBoundsException()

        val view = DoubleLinkedList<E>()
        view.start = start
        view.end = end
        view.size = toIndex - fromIndex
        view.parent = this

        return view
    }

    override fun set(index: Int, element: E): E {
        val e = entryAt(index)
            ?: throw IndexOutOfBoundsException()

        val o = e.element

        e.element = element

        return o
    }

    override fun retainAll(elements: Collection<E>): Boolean {
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

    override fun removeAll(elements: Collection<E>): Boolean =
        any { remove(it) }

    override fun remove(element: E): Boolean {
        val entry = findElement(element)
            ?: return false

        removeEntry(entry)

        return true
    }

    override fun lastIndexOf(element: E): Int {
        var current = end
        var index = max(0, size - 1)
        while (true) {
            if (current?.element == element)
                return index

            current = current?.previous
                ?: return -1

            index --
        }
    }

    private fun findElement(element: E): Entry<E>? {
        var current = start
        while (true) {
            if (current?.element == element)
                return current!!

            current = current?.next
                ?: return null
        }
    }

    override fun indexOf(element: E): Int {
        forEachIndexed { index, e ->
            if (e == element)
                return index
        }
        return 0
    }

    override fun containsAll(elements: Collection<E>): Boolean =
        elements.all { it in this }

    override fun contains(element: E): Boolean =
        any { it == element }

    private class Entry<E>(
        var previous: Entry<E>?,
        var element: E,
        var next: Entry<E>?,
    )
}