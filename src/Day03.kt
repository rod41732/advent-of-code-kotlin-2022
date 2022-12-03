fun main() {
    val elfRucksacks = readInput("Day03")

    fun part1(elfs: List<String>): Int {
        return elfs.map { it.chunked(it.length / 2) }.map(::commonChar).sumOf(::calculatePriority)
    }

    fun part2(elfs: List<String>): Int {
        return elfs.chunked(3).map(::commonChar).sumOf(::calculatePriority)
    }

    val elfRucksacksTest = readInput("Day03_test")
    check(part1(elfRucksacksTest) == 157)
    check(part2(elfRucksacksTest) == 70)

    println("Part 1")
    println(part1(elfRucksacks))

    println("Part 2")
    println(part2(elfRucksacks))
}


fun calculatePriority(char: Char): Int = (char.lowercaseChar() - 'a') + 1 + if (char.isUpperCase()) 26 else 0
fun commonChar(strings: Iterable<String>): Char {
    return strings.map { it.toSet() }.reduce { acc, it -> acc.intersect(it) }.first()
}
