typealias ListPos = Pair<List<Coord2D>, Int>

fun ListPos.next() = ListPos(
    first, (second + 1).mod(first.size)
)

fun ListPos.prev() = ListPos(
    first, (second - 1).mod(first.size)
)

fun ListPos.toCoord() = first[second]

private inline fun <T> List<List<T>>.getXD(r: Int, c: Int) = this[r][c]
data class Map(val raw: List<List<Char>>) {
    val byRow: MutableMap<Coord2D, ListPos> = mutableMapOf()
    val byCol: MutableMap<Coord2D, ListPos> = mutableMapOf()

    init {
        raw.withIndex().forEach { (y, row) ->
            row.withIndex().toList().chunkBy { it.value != ' ' }
                // row part of Coord2Ds
                .map { group -> group.map { Coord2D(x = it.index, y = y) } }
                // ref Coord2D back to row
                .forEach { group -> group.forEachIndexed { idx, coord -> byRow[coord] = Pair(group, idx) } }
        }

        raw.transpose().withIndex().forEach { (x, col) ->
            col.withIndex().toList().chunkBy { it.value != ' ' }
                // row part of Coord2Ds
                .map { group -> group.map { Coord2D(x = x, y = it.index) } }
                // ref Coord2D back to row
                .forEach { group -> group.forEachIndexed { idx, coord -> byCol[coord] = Pair(group, idx) } }
        }
    }

    /* zero based */
    fun at(row: Int, col: Int): Char {
        return kotlin.runCatching { raw[row][col] }.getOrDefault(' ')
    }
}

fun move(pos: ListPos, facing: Char): ListPos {
    return when (facing) {
        '>', 'V' -> pos.next()
        '<', '^' -> pos.prev()
        else -> throw Error("XD")
    }
}

fun unMove(pos: ListPos, facing: Char): ListPos {
    return when (facing) {
        '>', 'V' -> pos.prev()
        '<', '^' -> pos.next()
        else -> throw Error("XD")
    }
}

val facings = ">V<^".toList()
fun toPos(map: Map, coord: Coord2D, facing: Char): ListPos {
    return when (facing) {
        '>', '<' -> map.byRow[coord]!!
        '^', 'V' -> map.byCol[coord]!!
        else -> throw Error("XD")
    }
}

fun turn(currentFacing: Char, turn: Char): Char {
    val idx = facings.indexOf(currentFacing)
    return when (turn) {
        'R' -> facings[(idx + 1).mod(4)]
        'L' -> facings[(idx - 1).mod(4)]
        else -> throw Error("XD")
    }
}

sealed class Instruction
data class MoveInstruction(val steps: Int) : Instruction()
data class TurnInstruction(val turn: Char) : Instruction()

fun String.toInstruction(): Instruction {
    return runCatching {
        MoveInstruction(toInt())
    }.getOrElse { TurnInstruction(this.first()) }
}

private val splitter = Regex("(\\d+)|L|R")

typealias PlayerState = Pair<Coord2D, Char>


fun coordRange(xRange: IntRange, y: Int) = xRange.map { Coord2D(it, y )}
fun coordRange(x: Int, yRange: IntRange) = yRange.map { Coord2D(x, it)}

fun <T, U> List<T>.pairWith(o: U) = this.map { it to o }

fun Char.oppositeDir() = when (this) {
    '>' -> '<'
    '<' -> '>'
    '^' -> 'V'
    'V' -> '^'
    else -> throw Error("invalid direction to invert")
}

fun warpPair(l1: List<Coord2D>, dir1: Char, l2: List<Coord2D>, dir2: Char): List<Pair<PlayerState, PlayerState>> {
    return  l1.pairWith(dir1).zip(l2.pairWith(dir2))
}


val warpMap = listOf(
    warpPair(coordRange(50 until 100, 0), '^', coordRange(0, 150 until 200), '>'),
    warpPair(coordRange(50, 0 until 50), '<', coordRange(0, 100 until 150), '>'),
    warpPair(coordRange(50, 50 until 100), '<', coordRange(0 until 50, 100), 'V'),
    warpPair(coordRange(100 until 150, 0), '^', coordRange(0 until 50, 199), '^'),
    warpPair(coordRange(149, 0 until 50), '>', coordRange(99, 100 until 150), '<'),
    warpPair(coordRange(100 until 150, 49), 'V', coordRange(99, 50 until 100), '<'),
    warpPair(coordRange(50 until 100, 149), 'V', coordRange(49, 150 until 200), '<'),
).reduce {acc, it -> acc + it}.toMap()

fun move(coord: Coord2D, facing: Char): Coord2D {
    return when (facing) {
        '^' -> coord.copy(y = coord.y - 1)
        'V' -> coord.copy(y = coord.y + 1)
        '<' -> coord.copy(y = coord.x - 1)
        '>' -> coord.copy(y = coord.x + 1)
        else -> throw Error("Invalid direction to move")
    }
}
fun move2(state: PlayerState): PlayerState {
    return when {
        state in warpMap -> warpMap[state]!!
        else -> {
            val (pos, facing) = state
            move(pos, facing) to facing
        }
    }
}
fun main() {
    fun part1(input: List<String>): Int {
        val ln = input.dropLast(2).maxOf { it.length }
        val map = Map(input.dropLast(2).map { it.padEnd(ln).toList() })
        val instructionRaw = input.last()
        val x0 = input.first().indexOfFirst { it != ' ' }

        var coord = Coord2D(x = x0, y = 0)
        var facing = '>'
        val instructions = splitter.findAll(instructionRaw).map { it.value.toInstruction() }
        instructions.forEach {
            when (it) {
                is MoveInstruction -> {
                    var pos = toPos(map, coord, facing)
//                    println("start move ${it.steps} at $coord")
                    repeat(it.steps) {
                        pos = move(pos, facing)
                        val newCoord = pos.toCoord()
                        if (map.at(newCoord.y, newCoord.x) == '#') {
                            pos = unMove(pos, facing)
                            coord = pos.toCoord()
//                            println("after hitting # now at y = ${coord.y} x = ${coord.x} tile = ${map.raw[coord.y][coord.x]}")
                            return@forEach
                        }
                        coord = pos.toCoord()
//                        println("after moved now at y = ${coord.y} x = ${coord.x} tile = ${map.raw[coord.y][coord.x]}")
                    }
                }

                is TurnInstruction -> {
                    facing = turn(facing, it.turn)
//                    println("turned ${it.turn} now facing ${facing}")
                }
            }
        }
        println("final row = ${coord.y + 1} col = ${coord.x + 1} facing = $facing (${facings.indexOf(facing)})")
        return 1000 * (coord.y + 1) + 4 * (coord.x + 1) + facings.indexOf(facing)
    }

    fun part2(
        input: List<String>
    ): Int {
        val map = input.map { it.toList() } // grid
        val instructionRaw = input.last()
        val x0 = input.first().indexOfFirst { it != ' ' }

        var state: PlayerState = Coord2D(x = x0, y = 0) to '>'
        val instructions = splitter.findAll(instructionRaw).map { it.value.toInstruction() }
        instructions.forEach {
            when (it) {
                is MoveInstruction -> {
                    repeat (it.steps) {
                        val newState = move2(state)
                        val coord = newState.first
                        if (map.getXD(coord.y, coord.x) == '#') {
                            return@forEach
                        }
                        state = newState
                    }
                }

                is TurnInstruction -> {
                    val facing = turn(state.second, it.turn)
                    state = state.first to facing
                    println("turned ${it.turn} now facing ${facing}")
                }
            }
        }
        val coord = state.first
        val facing = state.second
        println("final row = ${coord.y + 1} col = ${coord.x + 1} facing = $facing (${facings.indexOf(facing)})")
        return 1000 * (coord.y + 1) + 4 * (coord.x + 1) + facings.indexOf(facing)
    }

    val testInput = readInput("Day22_test")
    println(part1(testInput))
    check(part1(testInput) == 6032)

    val input = readInput("Day22")
    println("Part 1")
    println(part1(input))
    println("Part 2")
    println(part2(input))
}

