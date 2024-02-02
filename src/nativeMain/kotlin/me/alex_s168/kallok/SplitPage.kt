package me.alex_s168.kallok

import kotlinx.cinterop.*

@OptIn(ExperimentalForeignApi::class)
data class SplitPage internal constructor(
    override val size: Int,
    override val ptr: CPointer<ByteVar>,
    val parent: Page
): Allocation {
    override fun free() {
        parent.free(this)
    }
}