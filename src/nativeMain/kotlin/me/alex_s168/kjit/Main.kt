package me.alex_s168.kjit

import kotlinx.cinterop.*
import me.alex_s168.kallok.Page

/*
Linux:   *di, *si, *dx, *cx
Windows: *cx, *dx, r8,  r9
 */

@OptIn(ExperimentalForeignApi::class)
fun main() {
    val page = Page.allocate()
    val asm = stream(
        "bits 64\n",
        "mov byte [rcx], 69\n", // rdi on lin
        "ret\n"
    )
    val func = JitTaskDispatcher.jit(
        asm,
        staticCFunction { _: CPointer<ByteVar> -> println("no jit") },
        page
    )
    memScoped {
        val b = alloc<ByteVar>()
        while (b.value.unsigned != 69u) {
            func.get<Unit, (CPointer<ByteVar>) -> Unit>()(b.ptr)
        }
        println("jit!")
    }
    page.free()
}