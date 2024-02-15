package me.alex_s168.kallok

fun ArenaAllocator(arenaSize: Int = 1024): Allocator {
    val ls = LargeSegmentAllocator()
    return MultiAllocator {
        ls.alloc(arenaSize)?.let { BasicAllocator(it) }
    }
}