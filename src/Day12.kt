import java.util.*

private typealias Coord = Pair<Int, Int>

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

fun main() {
    val input = readInput("Day12")
    val inputTest = readInput("Day12_test")

    fun part1(input: List<String>): Int {
        val startPos = input.indexOfFirst { it.contains('S') }.let {
            it to input[it].indexOf('S')
        }.let { (x, y) -> x + 1 to y + 1 }
        val endPos = input.indexOfFirst { it.contains('E') }.let {
            it to input[it].indexOf('E')
        }.let { (x, y) -> x + 1 to y + 1 }

        val cols = input[0].length + 2
        val paddingRow = listOf(List(cols) { Int.MAX_VALUE })

        val map = paddingRow + input.map { row ->
            val heights = row.map { char ->
                when (char) {
                    'S' -> 0
                    'E' -> 25
                    else -> (char.lowercaseChar() - 'a').toInt()
                }
            }
            listOf(Int.MAX_VALUE) + heights + Int.MAX_VALUE
        } + paddingRow

        val minDist = MutableList(map.size) { MutableList(cols) { Int.MAX_VALUE } }
        minDist.setAt(startPos, 0)

        val q: Queue<Pair<Coord, Int>> = LinkedList()
        q.add(startPos to 0)
        while (!q.isEmpty()) {
            val (pos, dist) = q.remove()
            val currentHeight = map.getAt(pos)
            if (pos == endPos) break
            pos.adjacents().forEach { nextPos ->
                if (map.getAt(nextPos) > currentHeight + 1) return@forEach
                if (minDist.getAt(nextPos) > dist + 1) {
                    minDist.setAt(nextPos, dist + 1)
                    q.add(nextPos to dist + 1)
                }
            }
        }
        return minDist.getAt(endPos)
    }

    fun part2(input: List<String>): Int {
        val endPos = input.indexOfFirst { it.contains('E') }.let {
            it to input[it].indexOf('E')
        }.let { (x, y) -> x + 1 to y + 1 }

        val cols = input[0].length + 2
        val paddingRow = listOf(List(cols) { Int.MAX_VALUE })

        val map = paddingRow + input.map { row ->
            val heights = row.map { char ->
                when (char) {
                    'S' -> 0
                    'E' -> 25
                    else -> (char.lowercaseChar() - 'a').toInt()
                }
            }
            listOf(Int.MAX_VALUE) + heights + Int.MAX_VALUE
        } + paddingRow

        val minDist = MutableList(map.size) { MutableList(cols) { Int.MAX_VALUE } }

        val q: Queue<Pair<Coord, Int>> = LinkedList()
        map.forEachIndexed { i, row ->
            row.forEachIndexed { j, height ->
                if (height == 0) {
                    minDist.setAt(i to j, 0)
                    q.add(Pair(i, j) to 0)
                }
            }
        }

        while (!q.isEmpty()) {
            val (pos, dist) = q.remove()
            val currentHeight = map.getAt(pos)
            if (pos == endPos) break
            pos.adjacents().forEach { nextPos ->
                if (map.getAt(nextPos) > currentHeight + 1) return@forEach
                if (minDist.getAt(nextPos) > dist + 1) {
                    minDist.setAt(nextPos, dist + 1)
                    q.add(nextPos to dist + 1)
                }
            }
        }
        return minDist.getAt(endPos)
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
