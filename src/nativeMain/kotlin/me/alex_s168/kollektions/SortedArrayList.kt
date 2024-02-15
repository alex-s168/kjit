package me.alex_s168.kollektions

import kotlin.math.max

/**
 * A mutable list that uses a [ArrayList] as underlying data structure.
 * The list guarantees all elements to stay sorted unless manually inserted.
 */
class SortedArrayList<T, C: Comparable<C>>(
    private val sorter: (T) -> C
): MutableList<T>, RandomAccess {
    private val al = ArrayList<T>()

    override val size: Int =
        al.size

    override fun clear() =
        al.clear()

    override fun addAll(elements: Collection<T>): Boolean {
        elements.forEach(::add)
        return true
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean =
        al.addAll(index, elements)

    override fun add(index: Int, element: T) =
        al.add(index, element)

    override fun add(element: T): Boolean {
        var rev = max(0, al.size / 2 - 1)
        var prev = 0
        while (true) {
            if (rev < 0) {
                al.add(0, element)
                return true
            }
            if (rev >= al.size) {
                al.add(element)
                return true
            }
            val cmp = sorter(element)
                .compareTo(sorter(al[rev]))
            if (cmp == 0) {
                al.add(rev, element)
                return true
            }
            if (cmp > 0 && prev < 0) {
                al.add(rev - 1, element)
                return true
            }
            if (cmp < 0 && prev > 0) {
                al.add(rev, element)
                return true
            }
            if (cmp < 0)
                rev --
            else if (cmp > 0)
                rev ++
            prev = cmp
        }
    }

    override fun get(index: Int): T =
        al.get(index)

    override fun isEmpty(): Boolean =
        al.isEmpty()

    override fun iterator(): MutableIterator<T> =
        al.iterator()

    override fun listIterator(): MutableListIterator<T> =
        al.listIterator()

    override fun listIterator(index: Int): MutableListIterator<T> =
        al.listIterator(index)

    override fun removeAt(index: Int): T =
        al.removeAt(index)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> =
        al.subList(fromIndex, toIndex)

    override fun set(index: Int, element: T): T =
        al.set(index, element)

    override fun retainAll(elements: Collection<T>): Boolean =
        al.retainAll(elements)

    override fun removeAll(elements: Collection<T>): Boolean =
        al.removeAll(elements)

    override fun remove(element: T): Boolean =
        al.remove(element)

    override fun lastIndexOf(element: T): Int =
        al.indexOf(element)

    override fun indexOf(element: T): Int =
        al.indexOf(element)

    override fun containsAll(elements: Collection<T>): Boolean =
        al.containsAll(elements)

    override fun contains(element: T): Boolean =
        al.contains(element)

    override fun toString(): String =
        al.toString()
}