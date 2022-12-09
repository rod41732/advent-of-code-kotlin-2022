import java.util.Stack
import kotlin.math.max


fun main() {
    val input = readInput("Day08")

    fun part1(input: List<String>): Int {
        val map = parseGrid(input)
        val left = accMaxFromLeft(map)
        val right = accMaxFromRight(map)
        val down = accMaxFromDown(map)
        val up = accMaxFromUp(map)
        val sides = listOf(up, down, left, right)
        return map.countIndexed { i, j, v -> sides.any { it[i][j] < v } }
    }


    fun part2(input: List<String>): Int {
        val map = parseGrid(input)
        val up = scenicUp(map)
        val down = scenicDown(map)
        val left = scenicLeft(map)
        val right = scenicRight(map)
        val sides = listOf(up, down, left, right)
        return map.maxOfIndexed { i, j, _ -> sides.map { it[i][j] }.reduce { acc, it -> acc * it } }
    }


    val testInput = readInput("Day08_test")
    println(part1(testInput))
    check(part1(testInput) == 21)
    println(part2(testInput))
    check(part2(testInput) == 8)

    println("Part 1")
    println(part1(input))

    println("Part 2")
    println(part2(input))
}


private typealias Grid2D = List<List<Int>>

private fun Grid2D.countIndexed(pred: (i: Int, j: Int, v: Int) -> Boolean): Int {
    return sumOfIndexed { i, row -> row.countIndexed { j, it -> pred(i, j, it) } }
}

private fun Grid2D.maxOfIndexed(apply: (i: Int, j: Int, v: Int) -> Int): Int {
    return maxOfIndexed { i, row -> row.maxOfIndexed { j, it -> apply(i, j, it) } }
}



private fun parseGrid(map: List<String>): Grid2D {
    return map.map { it.map { char -> char - '0' } }
}


private fun accMaxFromLeft(map: Grid2D): Grid2D {
    return map.map { row ->
        row.dropLast(1).runningFold(-1) { maxSoFar, num -> max(num, maxSoFar) }
    }
}

private fun accMaxFromRight(map: Grid2D): Grid2D {

    return map.map { row ->
        row.reversed().dropLast(1).runningFold(-1) { maxSoFar, num -> max(num, maxSoFar) }.reversed()
    }
}


private fun accMaxFromUp(map: Grid2D): Grid2D {
    return accMaxFromLeft(map.transpose()).transpose()
}

private fun accMaxFromDown(map: Grid2D): Grid2D {
    return accMaxFromRight(map.transpose()).transpose()
}


private fun scenicLeft(map: Grid2D): Grid2D {
    return map.map { scenic(it) }
}

private fun scenicRight(map: Grid2D): Grid2D {
    return map.map { scenic(it.reversed()).reversed() }
}

private fun scenicUp(map: Grid2D): Grid2D {
    return scenicLeft(map.transpose()).transpose()
}

private fun scenicDown(map: Grid2D): Grid2D {
    return scenicRight(map.transpose()).transpose()
}


private fun scenic(row: List<Int>): List<Int> {
    val s = Stack<Pair<Int, Int>>()
    return row.map {
        if (s.empty()) {
            s.push(it to 1)
            return@map 0
        }
        var popped = 0
        while (!s.empty()) {
            val top = s.peek()
            if (top.first < it) {
                s.pop()
                popped += top.second
            } else {
                break
            }
        }
        val seen = popped + (if (s.isEmpty()) 0 else 1)
        s.push(it to popped + 1)
        return@map seen
    }
}
