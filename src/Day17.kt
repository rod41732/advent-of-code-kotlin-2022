private fun simulateFall(rockGroup: Chunk, jets: Iterator<Int>, chamber: Chamber) {
    while (true) {
        val jet = jets.next()
        if (!chamber.rocksWillCollide(rockGroup.rocks, dx = jet, dy = 0)) {
            rockGroup.move(dx = jet, dy = 0)
        }
        // fall
        if (!chamber.rocksWillCollide(rockGroup.rocks, dy = -1)) {
            rockGroup.move(dy = -1)
        } else {
            break
        }
    }
}


fun main() {
    // bf XD
    fun part1(input: String, rounds: Int): Int {
        return Chamber(7).let {chamber ->
            val jetStream = parseJet(input).asSequence().cycled().iterator()
            val rockGroups = rockPrototype.asSequence().cycled().iterator()

            repeat(rounds) {
                val rockGroup = rockGroups.next().movedTo(3, chamber.height + 4)
                simulateFall(rockGroup, jetStream,chamber)
                chamber.addRocks(rockGroup.rocks)
            }
            chamber.height
        }
    }

    fun part2(input: String, rounds: Long): Long {
        check((rounds % 5).toInt() == 0)
        val jetStream = parseJet(input).asSequence().cycled().iterator()
        val rockGroups = rockPrototype.asSequence().withIndex().cycled().iterator()
        val chamber = Chamber(7)

        val rockEnd = rockPrototype.size - 1
        var lastHeight = 0
        val increment = mutableListOf<Int>()
        repeat(rounds) {
            val (rockGroup, rockIndex) = rockGroups.next()
                .let{ it.value.movedTo(3, chamber.height + 4) to it.index }

            simulateFall(rockGroup, jetStream, chamber)
            chamber.addRocks(rockGroup.rocks)
            // cycle optimization
            if (rockIndex == rockEnd) {
                chamber.height.let {
                    increment.add(it - lastHeight)
                    lastHeight = it
                }
                val cycle = findCycle(increment)
                if (cycle != null) {
                    val accum = increment.runningReduce { acc, v -> acc + v }
                    val ansIndex = rounds / 5 - 1
                    // need to add at least one cycle before optimization
                    // e.g. consider optimizing this 1, 2, 100, 200, 300, 100, 200, 300, 100, 200, 300 ...
                    return accum[ansIndex.mod(cycle.size) + cycle.size] + (ansIndex / cycle.size - 1) * cycle.sum()
                }
            }
        }
        return chamber.height.toLong()
    }


    val testInput = readInput("Day17_test").first()
    check(part1(testInput, rounds = 1) == 1)
    check(part1(testInput, rounds = 2) == 4)
    check(part1(testInput, rounds = 3) == 6)
    check(part1(testInput, rounds = 4) == 7)
    check(part1(testInput, rounds = 5) == 9)
    check(part1(testInput, rounds = 6) == 10)
    check(part1(testInput, rounds = 7) == 13)
    check(part1(testInput, rounds = 8) == 15)

    println(part1(testInput, rounds = 2022))
    check(part1(testInput, rounds = 2022) == 3068)
    println(part1(testInput, rounds = 2022))
    check(part1(testInput, rounds = 2022) == 3068)

    val input = readInput("Day17").first()
    println(part1(input, rounds = 2022))


//    println(part2Test(testInput, rounds = 2000))
    listOf(5, 10, 20, 100, 500, 725, 1000, 1500, 2100, 5000, 10000, 12005).forEach {
        println("checking $it")
        println("${part1(testInput, it).toLong()} ${part2(testInput, it.toLong())}")
        check(part1(testInput, it).toLong() == part2(testInput, it.toLong()))
        println("$it OK")
    }
    println("LUL")
//    part2Test(input)
//    part2Test(input, rounds = 1000)
    println(part2(input, 1_000_000_000_000L))
    // 1562536023044 too  big
    // 1562536022966

}

private fun parseJet(input: String): List<Int> {
    return input.trim().map { if (it == '<') -1 else 1 }
}

private fun findCycle(l: List<Int>): List<Int>? {
    (1..l.size / 2).forEach { repeatSize ->
        l.reversed().chunked(repeatSize).filter { it.size == repeatSize }.let { chunks ->
            val first = chunks[0]
            val remaining = chunks.subList(1, chunks.size)
            if (remaining.all { it == first }) {
                return first
            }
        }
    }
    return null
}


private data class Chamber(val width: Int) {
    // Chamber has rock at position x = 0 (left), x = width + 1 (right), y = 0 (bottom)
    private val settledRocks = mutableSetOf<Rock>()
    var height = 0 // height of the highest point in the chamber, or 0 (the floor) if there's no rock

    /** return whether the rocks will collide with rocks/floor/side of chamber if it were moved that way */
    fun rocksWillCollide(rocks: Iterable<Rock>, dx: Int = 0, dy: Int = 0): Boolean {
        return rocks.asSequence().map { it.moved(dx = dx, dy = dy) }.any {
            it.x == 0 || it.x == width + 1 || it.y == 0 || it in this.settledRocks
        }
    }

    /** add rocks, update height as appropriate */
    fun addRocks(rocks: Iterable<Rock>) {
        rocks.forEach {
            settledRocks.add(it)
            if (it.y > height) height = it.y
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

data class Chunk(val rocks: List<Rock>) {
    var lowerLeft = Coord2D(
        x = rocks.minOf { it.x },
        y = rocks.minOf { it.y },
    )

    /** return new rock group with same shape but lowerLeft position moved to specified point */
    fun movedTo(lowerLeftX: Int, lowerLeftY: Int): Chunk {
        val dx = lowerLeftX - lowerLeft.x
        val dy = lowerLeftY - lowerLeft.y
        return Chunk(rocks.map { it.moved(dx, dy) })
    }

    /** move all rocks by specified deltas */
    fun move(dx: Int = 0, dy: Int = 0) {
        rocks.forEach { it.move(dx, dy) }
    }
}
private fun <T> Sequence<T>.cycled() = sequence {
    while (true) {
        yieldAll(this@cycled)
    }
}

val rockPrototype = listOf(
    // _
    Chunk((0 until 4).map { Rock(it, 0) }),
    // + shape
    Chunk(
        listOf(
            Rock(0, 0),
            Rock(1, 0),
            Rock(0, 1),
            Rock(-1, 0),
            Rock(0, -1),
        ),
    ),
    // _| shape
    Chunk(
        listOf(
            Rock(0, 0),
            Rock(-1, 0),
            Rock(-2, 0),
            Rock(0, 1),
            Rock(0, 2),
        ),
    ),
    // |
    Chunk((0 until 4).map { Rock(0, it) }),
    // square
    Chunk((0 until 4).map { Rock(it / 2, it % 2) }),
)
