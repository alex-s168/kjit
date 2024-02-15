import me.alex_s168.kollektions.DoubleLinkedList
import kotlin.test.Test
import kotlin.test.assertEquals

@Test
fun testDoubleLinkedListAppend() {
    val li = DoubleLinkedList<Int>()

    li.add(2)
    li.add(9)
    li.add(5)
    li.addAll(listOf(8, 1, 3))

    assertEquals(6, li.size)

    assertEquals(2, li[0])
    assertEquals(9, li[1])
    assertEquals(5, li[2])
    assertEquals(8, li[3])
    assertEquals(1, li[4])
    assertEquals(3, li[5])
}

@Test
fun testDoubleLinkedListRemove() {
    val li = DoubleLinkedList<Int>()

    li.add(2)
    li.add(9)
    li.add(5)
    li.addAll(listOf(8, 1, 3))

    li.remove(5) // element "5"

    assertEquals(5, li.size)

    assertEquals(2, li[0])
    assertEquals(9, li[1])
    assertEquals(8, li[2])
    assertEquals(1, li[3])
    assertEquals(3, li[4])
}

@Test
fun testDoubleLinkedListInsert() {
    val li = DoubleLinkedList<Int>()

    li.add(2)
    li.add(9)
    li.add(5)
    li.addAll(listOf(8, 1, 3))

    li.addAll(1, listOf(5, 7)) // element "5"

    assertEquals(8, li.size)

    assertEquals(2, li[0])
    assertEquals(5, li[1])
    assertEquals(7, li[2])
    assertEquals(9, li[3])
    assertEquals(5, li[4])
    assertEquals(8, li[5])
    assertEquals(1, li[6])
    assertEquals(3, li[7])
}