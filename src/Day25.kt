fun digitToNum(digit: Char): Int = when (digit) {
    '-' -> -1
    '=' -> -2
    else -> digit - '0'
}

fun toSNAFU(x: Long): String = buildList {
    var p = x
    while (p != 0L) {
        val rem = p.mod(5)
        p /= 5
        when (rem) {
            in 0..2 -> add(rem.toString())
            3 -> {
                add("=")
                p++
            }

            4 -> {
                add("-")
                p++
            }
        }

    }
}.asReversed().joinToString("")


fun main() {
    fun part1(input: List<String>): String {
        return toSNAFU(input.map(::parseSNAFU)
            .sum())
    }

    val testInput = readInput("Day25_test")
    println(part1(testInput))
    check(part1(testInput) == "2=-1=0")

    check(parseSNAFU("1121-1110-1=0") == 314159265L)
    check(parseSNAFU("1-0---0") == 12345L)
    check(parseSNAFU("1=11-2") == 2022L)
    println(toSNAFU(314159265L))
    check(toSNAFU(314159265L) == "1121-1110-1=0")
    check(toSNAFU(12345L) == "1-0---0")
    check(toSNAFU(2022L) == "1=11-2")

    val input = readInput("Day25")
    println("Part 1")
    println(part1(input))
    // 33010101016442

}

private fun parseSNAFU(digits: String): Long = digits.map { digitToNum(it) }.fold(0L) { acc, v -> acc * 5 + v }
