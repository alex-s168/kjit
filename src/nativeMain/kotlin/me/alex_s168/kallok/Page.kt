package me.alex_s168.kallok

import kotlinx.cinterop.*
import me.alex_s168.kjit.tricks.alloc_page
import me.alex_s168.kjit.tricks.allocated
import me.alex_s168.kjit.tricks.free_page

/**
 * A full OS memory manager page. (Usually 4kB)
 * Can be split into multiple split-pages with different sizes, but freeing a split-page doesn't make space for freeing a new one.
 * If all split-pages are freed, automatically calls free() on itself.
 * If you do not use split-pages, you need to manually call free().
 * When free() is called, calls the given freeListener function with this.
 * userData can be used to make a multi-page memory allocator.
 */
@OptIn(ExperimentalForeignApi::class)
class Page private constructor(
    override val size: Int,
    override val ptr: CPointer<ByteVar>,
    private val freeListener: ((Page) -> Unit)? = null
): Allocation, Allocator {
    var userData: Any? = null

    companion object {
       fun allocate(freeListener: ((Page) -> Unit)? = null): Page =
           alloc_page().useContents {
               ptr ?: throw Exception("Could not allocate page!")

               Page(size.convert(), ptr!!.reinterpret(), freeListener)
           }
    }

    var splitPages = 0
        private set

    private var nextOffset = 0

    internal fun free(sp: SplitPage) {
        splitPages --
        if (splitPages == 0) {
            free()
        }
    }

    fun nextSplitPage(spSize: Int): SplitPage? {
        if (spSize + nextOffset > size.convert<Int>())
            return null

        splitPages ++
        val off = nextOffset
        nextOffset += spSize
        return SplitPage(spSize, (ptr + off)!!, this)
    }

    override fun alloc(size: Int): Allocation? =
        nextSplitPage(size)

    override fun free() {
        if (splitPages > 0)
            throw Exception("Can not free because there are still sub-pages attached!")

        memScoped {
            val a = alloc<allocated>()
            a.size = size.convert()
            a.ptr = ptr
            free_page(a.readValue())
        }

        freeListener?.invoke(this)
    }
}