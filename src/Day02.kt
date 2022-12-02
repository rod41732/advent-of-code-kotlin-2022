fun main() {
    val strategy = readInput("Day02")
        .map (::parseLine)

    fun part1(strategy: List<Pair<Int, Int>>): Int {
        // let x, y denotes Rock/Paper/Scissor of opponent and you
        // the result will be
        // you win IFF y - x ≡ 1 (mod 3)
        // draw IFF y - x ≡ 1 (mod 3)
        // you lost IFF y - x ≡ -1 (mod 3)
        return strategy.sumOf { (x, y) ->
            val playScore = y + 1
            val outcomeScore = when ((y - x).mod(3)) {
                1 -> 6
                0 -> 3
                else -> 0
            }
            playScore + outcomeScore
        }
    }

    fun part2(strategy: List<Pair<Int, Int>>): Int {
        return strategy.sumOf { (x, y) ->
            val playScore = (x + y - 1).mod(3) + 1
            val outcomeScore = y * 3
            playScore + outcomeScore
        }
    }

    val testStrategy = readInput("Day02_test")
        .map (::parseLine)
    check(part1(testStrategy) == 15)
    check(part2(testStrategy) == 12)

    println("Part 1")
    println(part1(strategy))

    println("Part 2")
    println(part2(strategy))
}


fun parseLine(line: String): Pair<Int, Int> =  line[0] - 'A' to line[2] - 'X'
