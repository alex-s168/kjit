import me.alex_s168.kjit.StreamingIO
import me.alex_s168.kjit.flatCollect
import kotlin.test.Test

@Test
fun testStreamingWrite() {
    StreamingIO.write(
        "test.txt",
        listOf("Hello,", " World!", "\n").iterator(),
        "w"
    )
}

@Test
fun testStreamingRead() {
    val read = StreamingIO.read(
        "test.txt",
        chunkSize = 2
    )
    val reads = read.flatCollect().toString()
    println(reads)
}