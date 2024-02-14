package me.alex_s168.kallok

import kotlinx.cinterop.*
import kotlin.math.ceil

/**
 * A basic allocator that operates on an [Allocation]
 *
 * It tries to re-use old unused allocated memory as good as it can.
 * It is however recommended to use multiple [BasicAllocator] for different sizes.
 * You could for example create a [BasicAllocator] for 8-byte variables and one for 32-byte variables.
 * This helps reducing memory usage over the runtime of your program.
 *
 * example:
 * ```
 * [       a l l o c a t i o n       ]
 * [x] [-] [-] [x] [-   -] [x] [-   -]
 *      ^   ^        ^           ^
 *    old allocated memory       |
 *                           un-touched memory
 * ```
 */
class BasicAllocator(
    val allocation: Allocation
): Allocator {
    // TODO: use sorted list instead
    private val free = ArrayList<BAllocation>()

    private var next: Int = 0

    init {
        allocation.set(0)
    }

    // TODO: merge smaller allocations into bigger ones and bigger ones into smaller ones
    @OptIn(ExperimentalForeignApi::class)
    override fun alloc(size: Int): Allocation? {
        val existing = free.asSequence()
            .filter { it.size >= size }
            .sortedBy { it.size }
            .firstOrNull()

        if (existing != null) {
            free -= existing
            return existing
        }

        val sizePlus = ceil(size.toFloat() / 8).times(8).toInt()

        if (next + sizePlus > allocation.size)
            return null

        val new = BAllocation(this, sizePlus, (allocation.ptr + next)!!)
        next += sizePlus
        return new
    }

    @OptIn(ExperimentalForeignApi::class)
    private class BAllocation(
        val parent: BasicAllocator,
        override val size: Int,
        override val ptr: CPointer<ByteVar>
    ): Allocation {
        override fun free() {
            parent.free += this
        }
    }
}