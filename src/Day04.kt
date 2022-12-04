fun main() {
    val f = readInput("Day04")

    fun parseLine(line: String): List<Pair<Int, Int>> {
        return line.split(',').map { it.split('-').map { it.toInt() }.toPair() }
    }

    fun part1(input: List<String>): Int {
        return input
            .map(::parseLine)
            .count { (x, y) -> x.contains(y) || y.contains(x)}
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

// convert iterable to pair, it's assumed that the iterable's length is 2
fun <T> Iterable<T>.toPair(): Pair<T, T> {
    return zipWithNext()[0]
}

fun Pair<Int, Int>.contains(other: Pair<Int, Int>): Boolean {
    return (first .. second) .let { it.contains(other.first) && it.contains(other.second)}
}

fun Pair<Int, Int>.overlaps(other: Pair<Int, Int>): Boolean {
    return (first .. second).contains(other.first) || (other.first .. other.second).contains(first)
}

