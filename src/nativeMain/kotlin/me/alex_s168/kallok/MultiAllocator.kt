package me.alex_s168.kallok

/**
 * You should **NOT** pass [Page] objects over [provider], as it might cause weird issues.
 * Use a [LargeSegmentAllocator] instead.
 */
class MultiAllocator(
    private val provider: () -> Allocator?
): Allocator {
    private var allocator: Allocator? = null

    override fun alloc(size: Int): Allocation? {
        if (allocator == null)
            allocator = provider()
                ?: return null

        var allok = allocator!!.alloc(size)
        while (allok == null) {
            allocator = provider()
                ?: return null
            allok = allocator!!.alloc(size)
        }
        return allok
    }
}