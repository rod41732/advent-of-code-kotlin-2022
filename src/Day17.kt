private data class Chamber(val width: Int) {
    // Chamber has rock at position x = 0 (left), x = width + 1 (right), y = 0 (bottom)
    val rocks = mutableSetOf<Rock>()

    fun rocksWillCollide(rocks: Iterable<Rock>, dx: Int = 0, dy: Int = 0): Boolean {
        return rocks.asSequence().map { it.moved(dx = dx, dy = dy) }.any {
            it.x == 0 || it.x == width + 1 || it.y == 0 || it in rocks
        }
    }
}
data class Rock(var x: Int, var y: Int) {
    fun move(dx: Int = 0, dy: Int = 0) {
        x += dx
        y += dy
    }

    fun moved(dx: Int = 0, dy: Int = 0) = Rock(x + dx, y + dy)
}

data class RockGroup(val rocks: List<Rock>) {
    var lowerLeft = Coord2D(
        x = rocks.minOf { it.x },
        y = rocks.minOf { it.y },
    )

    /** return rock group with lowerLeft position moved to specified point */
    fun movedTo(lowerLeftX: Int, lowerLeftY: Int): RockGroup {
        val dx = lowerLeftX - lowerLeft.x
        val dy = lowerLeftY - lowerLeft.y
        return RockGroup(rocks.map{ it.moved(dx, dy)})
    }
}

private fun <T> Sequence<T>.cycled() = sequence {
    while (true) {
        yieldAll(this@cycled)
    }
}


fun main() {

}



