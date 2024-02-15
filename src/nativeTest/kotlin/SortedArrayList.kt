import me.alex_s168.kollektions.SortedArrayList
import kotlin.test.Test

@Test
fun testSortedArrayList() {
    val sar = SortedArrayList<Int, Int> { it }
    sar += 5
    sar += 9
    sar += 2
    sar += 3
    sar += 10
    println(sar)
}