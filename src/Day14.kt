private fun parsePath(input: String): List<Coord2D> {
    return input.split(" -> ").map {
        it.split(",").map { it.toInt() }.let { (x, y) -> Coord2D(x, y) }
    }
}

private infix fun Int.smartRange(o: Int) = if (this < o) this .. o else o .. this
private fun Coord2D.lineTo(other: Coord2D): Sequence<Coord2D> {
    return when {
        x == other.x -> (y smartRange other.y).asSequence().map { Coord2D(x, it) }
        y == other.y -> (x smartRange other.x).asSequence().map { Coord2D(it, y) }
        else -> throw IllegalArgumentException("the other point must have same x or same y")
    }
}


private fun drawOnMap(m: MutableSet<Coord2D>, path: List<Coord2D>) {
    path.zipWithNext().forEach { (x, y) -> m.addAll(x.lineTo(y)) }
}


/** Mutable Coord 2D */
data class Sand(var x: Int, var y: Int) {
    fun toCoord(dx: Int = 0, dy: Int = 0) = Coord2D(x + dx, y + dy)
    fun move(dx: Int = 0, dy: Int = 0) {
        x += dx
        y += dy
    }

    /** simulate fall, stops when y == maxY */
    fun simulateFall(map: Set<Coord2D>, maxY: Int) {
        if (y >= maxY) return
        if (toCoord(dy = 1) !in map) {
            move(dy = 1)
            simulateFall(map, maxY = maxY)
        } else if (toCoord(dy = 1, dx = -1) !in map) {
            move(dy = 1, dx = -1)
            simulateFall(map, maxY = maxY)
        } else if (toCoord(dy = 1, dx = 1) !in map) {
            move(dy = 1, dx = 1)
            simulateFall(map, maxY = maxY)
        }
    }
}


fun main() {
    fun part1(input: List<String>): Int {
        val paths = input.map(::parsePath)

        val map = mutableSetOf<Coord2D>()
        paths.forEach { drawOnMap(map, it) }

        var cnt = 0
        val lowestY = paths.maxOf { points -> points.maxOf { point -> point.y } }
        while (true) {
            val sand = Sand(500, 0).also { it.simulateFall(map, lowestY) }
            if (sand.y == lowestY) {
                break
            }
            map.add(sand.toCoord())
            cnt++
        }
        return cnt
    }

    fun part2(input: List<String>): Int {
        val paths = input.map(::parsePath)

        val map = mutableSetOf<Coord2D>()
        paths.forEach { drawOnMap(map, it) }

        var cnt = 0
        val lowestY = paths.maxOf { points -> points.maxOf { point -> point.y } } + 1
        while (true) {
            val sand = Sand(500, 0).also { it.simulateFall(map, lowestY) }
            map.add(sand.toCoord())
            cnt++
            if (sand.toCoord() == Coord2D(500, 0)) break
        }
        return cnt
    }

    val testInput = readInput("Day14_test")
    println(part1(testInput))
    check(part1(testInput) == 24)
    println(part2(testInput))
    check(part2(testInput) == 93)

    val input = readInput("Day14")
    println("Part 1")
    println(part1(input))
    println("Part 2")
    println(part2(input))
}
