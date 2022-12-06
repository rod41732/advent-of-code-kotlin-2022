class Multiset<T> {
    private val unique: MutableSet<T> = mutableSetOf()
    private val count: MutableMap<T, Int> = mutableMapOf()
    fun add(v: T) {
        count[v] = (count[v] ?: 0) + 1
        if (count[v] == 1) unique.add(v)
    }

    fun remove(v: T) {
        count[v] = (count[v] ?: 0) - 1
        if (count[v] == 0) unique.remove(v)
    }

    val items: Set<T> get() = unique.toSet()
}
fun main() {
    val line = readInput("Day06")[0]

    fun solve(text: String, uniqueCount: Int): Int {
        val s = Multiset<Char>()
        return text.zip("_".repeat(uniqueCount) + text).indexOfFirst { (new, old) ->
            s.add(new); s.remove(old)
            s.items.size == uniqueCount
        } + 1
    }

    /** shorter implementation but "less efficient" (won't matter for this small input anyway) */
    fun solveEasy(text: String, uniqueCount: Int): Int {
        return text.windowed(uniqueCount).indexOfFirst { it.toSet().size == uniqueCount } + uniqueCount
    }

    fun part1(text: String) = solve(text, 4)
    fun part2(text: String) = solve(text, 14)

    print(part1("bwbjplbgvbhsrlpgdmjqwftvncz"))
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

