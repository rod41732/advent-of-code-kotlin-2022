import kotlin.math.abs

fun main() {
    val input = readInput("Day10")

    fun part1(input: List<String>): Int {
        val reg = calculateRegister(input)
        return (20..220 step 40).map { reg[it] * it }.sum()
    }

    // return 40x6 CRT screen seperated by newline
    fun part2(input: List<String>): String {
        val reg = calculateRegister(input)
        return (1..240)
            .joinToString("") { time -> if (abs(reg[time] - (time - 1) % 40) <= 1) "#" else "." }
            .chunked(40)
            .joinToString("\n")
    }

    val inputTest = readInput("Day10_test")
    check(part1(inputTest) == 13140)

    val part2image = """
        ##..##..##..##..##..##..##..##..##..##..
        ###...###...###...###...###...###...###.
        ####....####....####....####....####....
        #####.....#####.....#####.....#####.....
        ######......######......######......####
        #######.......#######.......#######.....
        """.trimIndent()
    check(part2(inputTest) == part2image)

    println("Part 1")
    println(part1(input))

    println("Part 2")
    println(part2(input))

}

/** return List<int> where list[idx] is value of register at *start* of cycle `idx` (idx starts at 1) */
private fun calculateRegister(input: List<String>): List<Int> {
    return listOf(1) + input.map { it.split(" ") }
        .flatMap { if (it.size == 2) listOf(0, it[1].toInt()) else listOf(0) }
        .runningFold(1) { acc, inc -> acc + inc }
}
