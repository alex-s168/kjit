package me.alex_s168.kjit

import platform.posix.F_OK
import platform.posix.access
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
val pathDelim = if (Platform.osFamily == OsFamily.WINDOWS) '\\' else '/'

fun isFile(path: String): Boolean =
    access(path, F_OK) == 0

val Byte.unsigned get() = this.toUInt() and 0xFFu