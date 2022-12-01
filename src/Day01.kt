fun main() {
    val elfCalories = readInputRaw("Day01").trim() // prevent trailing endline from causing problem
        .split("\n\n")
        .map { it.split("\n").sumOf { it.toInt() } }

    println("Part 1")
    println(elfCalories.max())

    println("Part 2")
    println(elfCalories.sortedDescending().take(3).sum())
}
