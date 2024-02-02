package me.alex_s168.kjit

import kotlinx.cinterop.*
import platform.posix.*

object StreamingIO {
    @OptIn(ExperimentalForeignApi::class)
    fun write(file: String, data: Stream<String>, mode: String = "w") {
        val f = fopen(file, mode)
            ?: throw Exception("Could not create file $file!")

        for (i in data) {
            fputs(i, f)
        }

        fclose(f)
    }

    @OptIn(ExperimentalForeignApi::class)
    fun write(file: String, data: Stream<Byte>, mode: String = "w") {
        val f = fopen(file, mode)
            ?: throw Exception("Could not create file $file!")

        for (i in data) {
            fputc(i.toInt(), f)
        }

        fclose(f)
    }

    @OptIn(ExperimentalForeignApi::class)
    fun <R> read(file: String, chunkSize: Int = 512, op: (ByteArray) -> R): Stream<R> {
        val f = fopen(file, "r")
            ?: throw Exception("Could not open file $file!")

        return stream {
            val buf = ByteArray(chunkSize)
            var read: ULong
            while (true) {
                read = fread(buf.refTo(0), 1u, chunkSize.convert(), f)

                if (read == 0uL)
                    break

                val b = buf.copyOfRange(0, read.convert<Int>())
                yield(op(b))
            }
        }.then { fclose(f) }
    }

    @OptIn(ExperimentalForeignApi::class)
    fun read(file: String, chunkSize: Int = 512): Stream<String> =
        read(file, chunkSize) { it.toKString() }
}