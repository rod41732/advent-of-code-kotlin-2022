import java.lang.Exception

fun main() {
    val strategy = readInput("Day02")
        .map { Pair(it[0].code - 'A'.code, it[2].code - 'X'.code)}


    fun part1(strategy: List<Pair<Int, Int>>): Int {
        // let x, y denotes Rock/Paper/Scissor of opponent and you
        // the result will be
        // you win IFF y - x ≡ 1 (mod 3)
        // draw IFF y - x ≡ 1 (mod 3)
        // you lost IFF y - x ≡ -1 (mod 3)
        return strategy.sumOf {  (x, y) ->
            val playScore = y + 1
            val outcomeScore = when ((y + 3 - x) % 3 ) {
                1 -> 6
                0 -> 3
                2 -> 0
                else -> throw Exception("Unreachable!")
            }
            playScore + outcomeScore
        }
    }

    fun part2(strategy: List<Pair<Int, Int>>): Int {
        return strategy.sumOf {  (x, y) ->
            val playScore = (x + y + 2) % 3 + 1
            val outcomeScore = y  * 3
            playScore + outcomeScore
        }
    }
    println("Part 1")
    println(part1(strategy))

    println("Part 2")
    println(part2(strategy))
}
