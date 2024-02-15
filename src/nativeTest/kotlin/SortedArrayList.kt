import me.alex_s168.kollektions.SortedArrayList
import kotlin.test.Test
import kotlin.test.assertEquals

@Test
fun testSortedArrayList() {
    val sar = SortedArrayList<Int, Int> { it }

    sar += 5
    sar += 9
    sar += 2
    sar += 3
    sar += 10

    assertEquals(sar[0], 2)
    assertEquals(sar[1], 3)
    assertEquals(sar[2], 5)
    assertEquals(sar[3], 9)
    assertEquals(sar[4], 10)
}