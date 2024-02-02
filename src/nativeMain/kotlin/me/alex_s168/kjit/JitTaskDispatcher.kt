package me.alex_s168.kjit

import kotlinx.cinterop.*
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.newFixedThreadPoolContext
import me.alex_s168.kallok.Allocation
import kotlin.concurrent.AtomicNativePtr
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.measureTime

object JitTaskDispatcher {
    var nJitThreads = 1

    private val jitDispatcher by lazy {
        newFixedThreadPoolContext(nJitThreads, "JIT-Thread")
    }

    private var nextId = 0

    @OptIn(ExperimentalForeignApi::class)
    fun <R, F: Function<R>> jit(asm: Stream<String>, kfun: CPointer<CFunction<F>>, alloc: Allocation): JitFunction {
        val jf = JitFunction(AtomicNativePtr(kfun.rawValue))
        val fp = jf.nativePtr
        val id = nextId ++
        jitDispatcher.dispatch(EmptyCoroutineContext, Runnable {
            val t = measureTime {
                val bin = assemble(asm, id)
                bin.collect(alloc)
                alloc.freezeAndMakeExec()
                fp.value = alloc.ptr.rawValue
            }
            println("JitC took ${t.inWholeMilliseconds}ms")
        })
        return jf
    }
}