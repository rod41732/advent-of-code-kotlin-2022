data class Elf(var currentCoord: Coord2D, var nextCoord: Coord2D? = null)
operator fun Coord2D.plus(other: Coord2D) = Coord2D(x + other.x, y + other.y)

fun makeStrat(dirs: List<Int>, outDir: Int) = fun(cur: Coord2D, existing: Set<Coord2D>) =
    if (dirs.all { it.toDirection() + cur !in existing }) cur + outDir.toDirection() else null


fun Int.toDirection() = when (this) {
    in (0..8) -> Coord2D(
        x = this % 3 - 1, y = this / 3 - 1
    )

    else -> throw IllegalArgumentException("invalid number for converting to direction")
}


val zeroStrats = listOf(
    makeStrat(
        (0 until 9).toList(
        ) - 4, 4
    )
)


val strats = listOf(
    makeStrat(listOf(0, 1, 2), 1),
    makeStrat(listOf(6, 7, 8), 7),
    makeStrat(listOf(0, 3, 6), 3),
    makeStrat(listOf(2, 5, 8), 5),
)

val fallbackStrats = listOf(
    makeStrat(listOf(), 4)
)

fun <T> List<T>.shifted(offset: Int) = (0 until size).map { this[(offset + it).mod(size)] }.toList()


fun Elf.proposeMove(strats: List<(Coord2D, Set<Coord2D>) -> Coord2D?>, existing: Set<Coord2D>) {
    nextCoord = strats.asSequence().map { strat -> strat(currentCoord, existing) }.first { it != null }
}


fun stratForRound(roundNo: Int) = zeroStrats + strats.shifted(roundNo) + fallbackStrats

fun <T> List<T>.counts() = buildMap<T, Int> {
    this@counts.forEach {
        this[it] = this.getOrPut(it) { 0 } + 1
    }
}


fun move(elfs: List<Elf>, roundNo: Int): Boolean {
    val coords = elfs.map { it.currentCoord }.toSet()
    val strats = stratForRound(roundNo)
    elfs.forEach { it.proposeMove(strats, coords) }
    val permittedMoves = elfs.map { it.nextCoord!! }.counts().filter { (_, v) -> v == 1 }.keys
    var hasMoved = false
    elfs.forEach {
        if (it.nextCoord!! in permittedMoves) {
            if (it.nextCoord != it.currentCoord) hasMoved = true
            it.currentCoord = it.nextCoord!!
            it.nextCoord = null
        }
    }
    return hasMoved
}

fun main() {
    fun part1(input: List<String>): Int {
        val elves = buildList {
            input.forEachIndexed { i, row ->
                row.forEachIndexed { j, ch ->
                    if (ch == '#') add(Elf(Coord2D(y = i, x = j)))
                }
            }
        }
        repeat(10) {
            move(elves, it.toInt())
        }
        val w = elves.maxOf { it.currentCoord.x } - elves.minOf { it.currentCoord.x } + 1
        val h = elves.maxOf { it.currentCoord.y } - elves.minOf { it.currentCoord.y } + 1
        return w * h - elves.size
    }

    fun part2(input: List<String>): Int {
        val elves = buildList {
            input.forEachIndexed { i, row ->
                row.forEachIndexed { j, ch ->
                    if (ch == '#') add(Elf(Coord2D(y = i, x = j)))
                }
            }
        }
        var moved = false
        var i = 0
        do {
            moved = move(elves, i)
            i++
        } while (moved)
        return i
    }

    val testInput = readInput("Day23_test")
    println(part1(testInput))
    check(part1(testInput) == 110)
    println(part2(testInput))
    check(part2(testInput) == 20)

    val input = readInput("Day23")
    println("Part 1")
    println(part1(input))
    println("Part 2")
    println(part2(input))

}
