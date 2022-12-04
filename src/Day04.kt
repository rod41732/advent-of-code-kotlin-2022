fun main() {
    val f = readInput("Day04")

    fun parseLine(line: String): List<IntRange> {
        return line.split(',')
            .map { it.split('-').map { it.toInt() }.let { (x, y) -> x..y } }
    }

    fun part1(input: List<String>): Int {
        return input
            .map(::parseLine)
            .count { (x, y) -> x.contains(y) || y.contains(x) }
    }

    fun part2(input: List<String>): Int {
        return input
            .map(::parseLine)
            .count { (x, y) -> x.overlaps(y) }
    }

    val testF = readInput("Day04_test")
    check(part1(testF) == 2)
    check(part2(testF) == 4)

    println("Part 1")
    println(part1(f))


    println("Part 2")
    println(part2(f))
}

fun IntRange.contains(other: IntRange): Boolean = other.first in this && other.last in this
fun IntRange.overlaps(other: IntRange): Boolean = other.first in this || other.last in this  || other.contains(this)

