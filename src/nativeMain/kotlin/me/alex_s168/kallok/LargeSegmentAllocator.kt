package me.alex_s168.kallok

class LargeSegmentAllocator: Allocator {
    private val pages = LinkedHashSet<Page>()

    override fun alloc(size: Int): Allocation? {
        for (p in pages) {
            return p.nextSplitPage(size) ?: continue
        }
        val p = Page.allocate {
            pages -= it
        }
        pages += p
        return p.nextSplitPage(size)
    }
}