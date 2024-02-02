package me.alex_s168.kjit

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.*
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalForeignApi::class)
object env {
    operator fun get(name: String): String? =
        getenv(name)?.toKString()

    @OptIn(ExperimentalNativeApi::class)
    private val pathVarDelim = if (Platform.osFamily == OsFamily.WINDOWS) ';' else ':'

    val path by lazy {
        get("PATH")?.split(pathVarDelim) ?: listOf()
    }

    fun findInPath(execName: String) =
        path.firstOrNull { isFile(it + pathDelim + execName) }?.plus(pathDelim + execName)
}