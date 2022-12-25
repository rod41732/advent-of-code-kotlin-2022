fun main() {
    fun part1(input: List<String>): Long {
        val encryptedCoordinates = input.map { it.toLong() }.withIndex().toMutableList()
        encryptedCoordinates.mix()
        return encryptedCoordinates.map { it.value }.groveCoordinatesSum()
    }

    val DECRYPTION_KEY = 811589153L
    fun part2(input: List<String>): Long {
        val encryptedCoordinates = input.map { it.toLong() * DECRYPTION_KEY }.withIndex().toMutableList()
        repeat(10) { encryptedCoordinates.mix() }
        return encryptedCoordinates.map { it.value }.groveCoordinatesSum()
    }

    val testInput = readInput("Day20_test")
    println(part1(testInput))
    check(part1(testInput) == 3L)
    println(part2(testInput))
    check(part2(testInput) == 1623178306L)

    val input = readInput("Day20")
    println("Part 1")
    println(part1(input))  // 1591
    println("Part 2")
    println(part2(input))  // 14579387544492
}

private fun MutableList<IndexedValue<Long>>.mix() {
    repeat(size) { i ->
        val idx = indexOfFirst { it.index == i }
        removeAt(idx).let { item ->
            add(index = (idx + item.value).mod(size), item)
        }
    }
}

private fun List<Long>.groveCoordinatesSum(): Long {
    return indexOf(0).let { idx ->
        listOf(1000, 2000, 3000).map { this[(idx + it).mod(size)] }
    }.sum()
}
