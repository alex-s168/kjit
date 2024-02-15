package me.alex_s168.kallok

import kotlinx.cinterop.*
import me.alex_s168.kollektions.SortedArrayList
import kotlin.math.ceil

/**
 * A basic allocator that operates on an [Allocation]
 *
 * It tries to re-use old unused allocated memory as good as it can.
 * It is however recommended to use multiple [BasicAllocator] for different sizes.
 * You could for example create a [BasicAllocator] for 8-byte variables and one for 32-byte variables.
 * This helps with reducing memory usage over the runtime of your program.
 *
 * It is not recommended to free allocations made by this allocator because it uses a ArrayList internally to
 * store the freed allocations.
 * Instead, you should free the underlying allocation after you are done.
 *
 * When to use:
 * - Allocating many similar sized allocations
 * - Allocating many differently sized resources that all need to last around the same time
 *   (then you can just free the underlying allocation to free all the small allocations)
 *
 * When not to use:
 * - Allocating big continues allocations
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
    // TODO: use sorted linked list instead
    private val free = SortedArrayList<BAllocation, Int> { it.size }

    private var next: Int = 0

    init {
        allocation.set(0)
    }

    // TODO: merge smaller allocations into bigger ones and bigger ones into smaller ones
    @OptIn(ExperimentalForeignApi::class)
    override fun alloc(size: Int): Allocation? {
        val existing = free.asSequence()
            .filter { it.size >= size }
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