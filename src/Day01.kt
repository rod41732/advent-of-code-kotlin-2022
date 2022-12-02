import java.util.*

fun main() {
    val elfCalories = readInputRaw("Day01").trim() // prevent trailing endline from causing problem
        .split("\n\n")
        .map { it.split("\n").sumOf { it.toInt() } }

    fun part1(eachElfCalories: List<Int>) = eachElfCalories.max()
    fun part2(eachElfCalories: List<Int>) = eachElfCalories.sortedDescending().take(3).sum()
    fun part2Faster(eachElfCalories: List<Int>): Int {
        val heap = PriorityQueue(Int::compareTo)
        eachElfCalories.forEach {
            if (heap.size != 0 && it < heap.peek()) return@forEach
            heap.add(it)
            if (heap.size > 3) heap.remove()
        }
        return heap.sum()
    }
    val testElfCalories = readInputRaw("Day01_test").trim() // prevent trailing endline from causing problem
        .split("\n\n")
        .map { it.split("\n").sumOf { it.toInt() } }
    check(part1(testElfCalories) == 24000)
    check(part2(testElfCalories) == 45000)


    println("Part 1")
    println(part1(elfCalories))

    println("Part 2")
    println(part2(elfCalories))

    println("Part 2 - O(N) solution")
    println(part2Faster(elfCalories))
}
