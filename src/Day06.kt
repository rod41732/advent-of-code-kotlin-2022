fun main() {
    val line = readInput("Day06")[0]

    fun solve(text: String, uniqueCount: Int): Int {
        val count = mutableMapOf<Char, Int>()
        var uniqueCharCount = 0
//        text.zip(text, " ".repeat(uniqueCount ) + text.substring(uniqueCount) ).
        text.forEachIndexed { i, newChar ->
            count[newChar] = (count[newChar] ?: 0) + 1
            if (count[newChar] == 1) uniqueCharCount += 1

            if (i >= uniqueCount) {
                val oldChar = text[i - uniqueCount]
                count[oldChar] = count[oldChar]!! - 1
                if (count[oldChar] == 0) uniqueCharCount -= 1
            }

            if (uniqueCharCount == uniqueCount) return i + 1
        }
        return -1
    }

    /** shorter implementation but "less efficient" (won't matter for this small input anyway) */
    fun solveEasy(text: String, uniqueCount: Int): Int {
        return text.windowed(uniqueCount).indexOfFirst { it.toSet().size == uniqueCount } + uniqueCount
    }

    fun part1(text: String) = solve(text, 4)
    fun part2(text: String) = solve(text, 14)

    check(part1("bwbjplbgvbhsrlpgdmjqwftvncz") == 5)
    check(part1("nppdvjthqldpwncqszvftbrmjlhg") == 6)
    check(part1("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg") == 10)
    check(part1("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw") == 11)

    check(part2("mjqjpqmgbljsphdztnvjfqwrcgsmlb") == 19)
    check(part2("bvwbjplbgvbhsrlpgdmjqwftvncz") == 23)
    check(part2("nppdvjthqldpwncqszvftbrmjlhg") == 23)
    check(part2("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg") == 29)
    check(part2("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw") == 26)

    println("Part 1")
    println(part1(line))

    println("Part 2")
    println(part2(line))
}

