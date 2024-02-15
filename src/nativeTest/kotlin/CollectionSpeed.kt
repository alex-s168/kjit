import me.alex_s168.kollektions.CommonLists
import me.alex_s168.kollektions.MultiList
import me.alex_s168.kollektions.contents
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.measureTime

data class CollectionSpeed(
    val remove: Duration,
    val removeAt: Duration,
    val insert: Duration,
    val append: Duration,
    val contains: Duration,
    val appendBig: Duration,
    val indexOf: Duration
)

fun measureSpeed(list: MutableList<Int>, times: Int): CollectionSpeed {
    val append = measureTime {
        repeat(times) {
            list.add(it)
        }
    }
    if (list is MultiList) {
        list.debug()
    }
    val remove = measureTime {
        (0..<times).reversed().forEach {
            list.remove(it)
        }
    }
    if (list is MultiList) {
        list.debug()
    }
    repeat(times) {
        list.add(it)
    }
    if (list is MultiList) {
        list.debug()
    }
    val removeAt = measureTime {
        repeat(times) {
            list.removeAt(0)
        }
    }
    repeat(times) {
        list.add(it)
    }
    val insert = measureTime {
        repeat(times - 1) {
            list.add(it, it)
        }
    }
    val indexOf = measureTime {
        repeat(times) {
            list.indexOf(it)
        }
    }
    val contains = measureTime {
        repeat(times) {
            list.contains(it)
        }
    }
    val big = mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val appendBig = measureTime {
        repeat(times) {
            list.addAll(big)
        }
    }
    list.clear()
    return CollectionSpeed(
        remove = remove,
        removeAt = removeAt,
        insert = insert,
        append = append,
        contains = contains,
        appendBig = appendBig,
        indexOf = indexOf,
    )
}

@Test
fun testCollectionSpeeds() {
    val times = 1000

    CommonLists.linkedListOf<Int>().clear()
    val data = measureSpeed(CommonLists.fastMutableListOf(), times)
    println("==== CommonLists.linkedListOf ====")
    println(data)

    CommonLists.fastMutableListOf<Int>().clear()
    val data2 = measureSpeed(CommonLists.fastMutableListOf(), times)
    println("==== CommonLists.fastMutableListOf ====")
    println(data2)

    CommonLists.concurrentMutableListOf<Int>().clear()
    val data3 = measureSpeed(CommonLists.concurrentMutableListOf(), times)
    println("==== CommonLists.concurrentMutableListOf ====")
    println(data3)

    arrayListOf<Int>().clear()
    val data4 = measureSpeed(arrayListOf(), times)
    println("==== ArrayList ====")
    println(data4)
}