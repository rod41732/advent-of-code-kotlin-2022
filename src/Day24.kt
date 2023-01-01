import java.util.*

private data class Blizzard(val coord: Coord2D, val direction: Char)

/** wrap the integer if it goes outside the range e.g.
 * (1..5).wrap(0) == 5
 * (1..5).wrap(6) == 1
 * (1..5).wrap(3) == 1
 */
private fun IntRange.wrap(num: Int): Int {
    return first + (num - first).mod(last - first + 1)
}
private fun progressBlizzard(blizzards: List<Blizzard>, xRange: IntRange, yRange: IntRange): List<Blizzard> {
    return blizzards.map {
        val newCoord = move(it.coord, it.direction).let {
            Coord2D(xRange.wrap(it.x), yRange.wrap(it.y))
        }
        Blizzard(newCoord, it.direction)
    }
}

private data class PlayerState24(val coord: Coord2D, val time: Int)


private inline fun Coord2D.forEachPossibleMoves(action: (Coord2D) -> Unit) {
    action(copy(x = x + 1))
    action(copy(x = x - 1))
    action(copy(y = y + 1))
    action(copy(y = y - 1))
    action(copy())
}

fun main() {
    fun findPath(
        blizzInit: List<Blizzard>,
        startPos: Coord2D,
        validX: IntRange,
        validY: IntRange,
        endPos: Coord2D,
        map: List<List<Char>>
    ): Pair<List<Blizzard>, Int> {
        var blizzards = blizzInit
        var blizzTime = 0
        var blizzCoords = blizzards.map { it.coord }.toSet()
        val lastVisit = mutableMapOf<Coord2D, Int>()


        val q: Queue<PlayerState24> = LinkedList()
        q.add(PlayerState24(startPos, 0))
        while (!q.isEmpty()) {
            val (coord, time) = q.remove()
    //            println("at $coord, time $time")
            if (time == blizzTime) {
    //                println("blizztime progress at $time")
                blizzards = progressBlizzard(blizzards, validX, validY)
                blizzTime += 1
                blizzCoords = blizzards.map { it.coord }.toSet()
            }
            coord.forEachPossibleMoves {
                if (it != endPos && it != startPos && (it.x !in validX || it.y !in validY)) return@forEachPossibleMoves
                if (it in blizzCoords) return@forEachPossibleMoves
                if (map[it.y][it.x] == '#') return@forEachPossibleMoves

                // prevent same state multiple times
                if (lastVisit.getOrPut(it) { 0 } >= time + 1) return@forEachPossibleMoves
                lastVisit[it] = time + 1

                if (it == endPos) {
                    println("took ${time + 1}")
                    return blizzards to time + 1
                }
                q.add(PlayerState24(it, time + 1))
            }
        }
        throw Error("BUG: not reached goal")
    }

    fun part1(input: List<String>): Int {
        val startPos = Coord2D(x = input.first().indexOf('.'), y = 0)
        val endPos = Coord2D(x = input.last().indexOf('.'), y = input.lastIndex)
        val validX = 1 until input[0].length - 1
        val validY = 1 until input.size - 1
        val map = input.map { it.toList() }

        var blizzards = buildList<Blizzard> {
            input.map { it.toList() }.forEachIndexed { i, row ->
                row.forEachIndexed { j, c ->
                    when (c) {
                        '^', 'v', '<', '>' -> add(Blizzard(Coord2D(x = j, y = i), c))
                    }
                }
            }
        }
        val (_, time) = findPath(blizzards, startPos, validX, validY, endPos, map)
        return time
    }
    fun part2(input: List<String>): Int {
        val startPos = Coord2D(x = input.first().indexOf('.'), y = 0)
        val endPos = Coord2D(x = input.last().indexOf('.'), y = input.lastIndex)
        val validX = 1 until input[0].length - 1
        val validY = 1 until input.size - 1
        val map = input.map { it.toList() }

        val blizzards = buildList<Blizzard> {
            input.map { it.toList() }.forEachIndexed { i, row ->
                row.forEachIndexed { j, c ->
                    when (c) {
                        '^', 'v', '<', '>' -> add(Blizzard(Coord2D(x = j, y = i), c))
                    }
                }
            }
        }

        val (b1, t1) = findPath(blizzards, startPos, validX, validY, endPos, map)
        val (b2, t2) = findPath(b1, endPos, validX, validY, startPos, map)
        val (_, t3) = findPath(b2, startPos, validX, validY, endPos, map)
        return t1 + t2 + t3

    }


    val testInput = readInput("Day24_test")
    println(part1(testInput))
    check(part1(testInput) == 18)
    println(part2(testInput))
    check(part2(testInput) == 18 + 23 + 13)

    val input = readInput("Day24")
    println("Part 1")
    println(part1(input))
    println("Part 2")
    println(part2(input))
}
