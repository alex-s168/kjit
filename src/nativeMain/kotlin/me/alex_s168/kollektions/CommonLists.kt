package me.alex_s168.kollektions

object CommonLists {
    /* =================== MultiList < ArrayList > ==================== */

    inline fun <T> fastMutableListOf(): MutableList<T> =
        MultiList { ArrayList() }

    fun <T> fastMutableListOf(vararg elems: T): MutableList<T> {
        val list = fastMutableListOf<T>()
        list.addAll(elems.toMutableList())
        return list
    }

    /* ========== MultiList < SynchronizedList < ArrayList > > ========== */

    inline fun <T> concurrentMutableListOf(): MutableList<T> =
        MultiList { SynchronizedList(ArrayList()) }

    fun <T> concurrentMutableListOf(vararg elems: T): MutableList<T> {
        val list = concurrentMutableListOf<T>()
        list.addAll(elems.toMutableList())
        return list
    }

    /* ======================== DoubleLinkedList ======================== */

    inline fun <T> linkedListOf(): MutableList<T> =
        DoubleLinkedList()

    fun <T> linkedListOf(vararg elems: T): MutableList<T> {
        val list = linkedListOf<T>()
        list.addAll(elems.toMutableList())
        return list
    }
}