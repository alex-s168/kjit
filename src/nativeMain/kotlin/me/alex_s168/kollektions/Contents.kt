package me.alex_s168.kollektions

class Contents<T> internal constructor(
    private val iterable: Iterable<T>
): Iterable<T> {
    override fun iterator(): Iterator<T> =
        iterable.iterator()

    override fun equals(other: Any?): Boolean {
        if (other !is Contents<*>)
            return false

        val it1 = this.iterable.iterator()
        val it2 = other.iterable.iterator()

        while (true) {
            val hasNext1 = it1.hasNext()
            val hasNext2 = it2.hasNext()

            if ((hasNext1 && !hasNext2) || (hasNext2 && !hasNext1))
                return false

            if (!hasNext1)
                return true

            if (it1.next() != it2.next())
                return false
        }
    }

    override fun hashCode(): Int =
        iterable.hashCode()
}

val <T> Iterable<T>.contents get() =
    Contents(this)