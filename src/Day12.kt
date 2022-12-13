import java.util.*

private typealias Coord = Pair<Int, Int>


fun main() {
    val input = readInput("Day12")
    val inputTest = readInput("Day12_test")

    fun part1(input: List<String>): Int {
        val mapRaw = input.map { it.toList() }
        val start = mapRaw.findIndex2D('S').first()
        val end = mapRaw.findIndex2D('E').first()

        val map = mapRaw.map { row -> row.map(::calculateHeight) }
        return bfsDistance(listOf(start), map, end)
    }

    fun part2(input: List<String>): Int {
        val mapRaw = input.map { it.toList() }
        val starts = mapRaw.findIndex2D { it == 'S' || it == 'a' }.toList()
        val end = mapRaw.findIndex2D('E').first()

        val map = mapRaw.map { row -> row.map(::calculateHeight) }
        return bfsDistance(starts, map, end)
    }

    println(part1(inputTest))
    check(part1(inputTest) == 31)
    println(part2(inputTest))
    check(part2(inputTest) == 29)

    println("Part 1")
    println(part1(input))
    println("Part 2")
    println(part2(input))
}

private fun calculateHeight(code: Char): Int {
    return when (code) {
        'S' -> 0
        'E' -> 25
        else -> code - 'a'
    }
}

private fun Coord.adjacents(): List<Coord> {
    return listOf(
        first to second + 1,
        first + 1 to second,
        first - 1 to second,
        first to second - 1,
    )
}

private fun <T> MutableList<MutableList<T>>.setAt(pos: Coord, v: T) {
    val (x, y) = pos
    this[x][y] = v
}

private fun <T> List<List<T>>.getAt(pos: Coord): T {
    val (x, y) = pos
    return this[x][y]
}

private fun bfsDistance(starts: List<Coord>, map: List<List<Int>>, end: Coord): Int {
    val height = map.size
    val width = map.first().size

    val dist = MutableList(map.size) { MutableList(map[0].size) { Int.MAX_VALUE } }
    starts.forEach { dist.setAt(it, 0) }
    val q: Queue<Coord> = LinkedList(starts)

    while (!q.isEmpty()) {
        val pos = q.remove()
        val currentDist = dist.getAt(pos)
        val currentHeight = map.getAt(pos)
//        println("at: $pos dist: $currentDist height: ${map.getAt(pos)}")
        if (pos == end) break

        pos.adjacents().forEach { nextPos ->
            if (!(0 until height).contains(nextPos.first)) return@forEach
            if (!(0 until width).contains(nextPos.second)) return@forEach
//            println("possible next $nextPos ${map.getAt(nextPos)}")
            if (map.getAt(nextPos) > currentHeight + 1) return@forEach
//            println("possible next $nextPos")

            if (dist.getAt(nextPos) == Int.MAX_VALUE) {
                dist.setAt(nextPos, currentDist + 1)
                q.add(nextPos)
            }
        }
    }
    return dist.getAt(end)
}

private fun <T> List<List<T>>.findIndex2D(elem: T): Sequence<Coord> = sequence {
    forEachIndexed { i, row -> row.forEachIndexed { j, value -> if (value == elem) yield(i to j) } }
}

private fun <T> List<List<T>>.findIndex2D(pred: (T) -> Boolean): Sequence<Coord> = sequence {
    forEachIndexed { i, row -> row.forEachIndexed { j, value -> if (pred(value)) yield(i to j) } }
}
