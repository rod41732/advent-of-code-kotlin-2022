import java.io.File

fun main() {
    val elfCalories = File("src/Day01.txt").readText().trim()
        .split("\n\n")
        .map { it.split("\n").map { Integer.parseInt(it) }.sum() }

    println("Part 1")
    println(elfCalories.max())

    println("Part 2")
    println(elfCalories.sortedDescending().take(3).sum())
}
