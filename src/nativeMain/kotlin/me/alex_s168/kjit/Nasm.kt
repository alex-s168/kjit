package me.alex_s168.kjit

import platform.posix.system
import kotlin.experimental.ExperimentalNativeApi
import me.alex_s168.kollektions.flatten

private var nasmPathOverr: String? = null

fun setNasmPathOverride(p: String) {
    nasmPathOverr = p
}

@OptIn(ExperimentalNativeApi::class)
private fun nasmDownloadPath() =
    when (Platform.osFamily) {
        OsFamily.WINDOWS -> when (Platform.cpuArchitecture) {
            CpuArchitecture.X64 -> "https://www.nasm.us/pub/nasm/releasebuilds/2.16.01/win64/nasm-2.16.01-win64.zip"
            CpuArchitecture.X86 -> "https://www.nasm.us/pub/nasm/releasebuilds/2.16.01/win32/nasm-2.16.01-win32.zip"
            else -> throw Exception("NASM not supported on current platform!")
        }
        OsFamily.MACOSX -> "https://www.nasm.us/pub/nasm/releasebuilds/2.16.01/macosx/nasm-2.16.01-macosx.zip"
        OsFamily.LINUX -> throw Exception("We can not automatically install NASM for you. Try to manually install it and add it to the PATH.")
        else -> throw Exception("NASM not supported on current platform!")
    }

val nasmPath: String by lazy {
    nasmPathOverr
        ?: env.findInPath("nasm.exe")
        ?: env.findInPath("nasm")
        ?: throw Exception("Sorry, we can not automatically install NASM yet. " +
                "Manually download ${nasmDownloadPath()} and extract it and add NASM to the path!")
}

fun assemble(source: Stream<String>, id: Int): Stream<Byte> {
    val asmFile = ".$id.jit.asm"
    StreamingIO.write(asmFile, source)

    val binFile = ".$id.jit.bin"
    val cmd = "$nasmPath -fbin -wall $asmFile -o $binFile -O0"  // TODO: disable opt?

    val err = system(cmd)
    if (err != 0)
        throw Exception("NASM returned error code $err")

    return StreamingIO.read(binFile) { it.iterator() }.flatten()
}