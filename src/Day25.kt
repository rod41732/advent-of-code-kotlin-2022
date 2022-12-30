

fun main() {
    fun part1(input: List<String>): String {
        return toSNAFU(
            input.map(::parseSNAFU).sum()
        )
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
    println(part1(input)) // 33010101016442
    // there's no Part 2 for Day25
}

private fun Char.digitValue(): Int = when (this) {
    '0', '1', '2' -> this - '0'
    '-' -> -1
    '=' -> -2
    else -> throw Error("Invalid digit")
}

private fun parseSNAFU(digits: String): Long = digits.map { it.digitValue() }.fold(0L) { acc, v -> acc * 5 + v }

private fun Int.toDigit(): Pair<Char, Int> = when (this) {
    0, 1, 2 -> '0' + this to 0 // n = 5(0) + n
    3 -> '=' to 1 // 3 = 5(1) - 2
    4 -> '-' to 1 // 4 = 5(1) - 1
    else -> throw Error("Invalid position number")
}

private fun toSNAFU(x: Long): String = buildList {
    var p = x
    while (p != 0L) {
        val rem = p.mod(5)
        p /= 5
        rem.toDigit().also { (char, carry) ->
            add(char)
            p += carry
        }
    }
}.joinToString("").reversed()
