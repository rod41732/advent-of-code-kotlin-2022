typealias Stack = MutableList<Char>

fun main() {
    val lines = readInput("Day05")

    fun part1(lines: List<String>): String {
        val (stacks, operations) = parseInput(lines)
        operations.forEach { (num, src, dst) ->
            repeat(num) { stacks[dst].add(stacks[src].removeLast()) }
        }
        return stacks.map { it.last() }.joinToString("")
    }

    fun part2(lines: List<String>): String {
        val (stacks, operations) = parseInput(lines)
        operations.forEach { (num, src, dst) ->
            val lastN = (0..num - 1).map { stacks[src].removeLast() }.reversed()
            stacks[dst].addAll(lastN)
        }
        return stacks.map { it.last() }.joinToString("")
    }

    val testLines = readInput("Day05_test")
    check(part1(testLines) == "CMZ")
    check(part2(testLines) == "MCD")

    println("Part 1")
    println(part1(lines))

    println("Part 2")
    println(part2(lines))
}

private fun parseInput(lines: List<String>): Pair<List<Stack>, List<Operation>> {
    val (stackInput, operationInput) = splitInput(lines)
    return parseStacks(stackInput) to operationInput.map(::parseOperation)
}

private fun splitInput(lines: List<String>): Pair<List<String>, List<String>> {
    val sep = lines.indexOfFirst { it.isBlank() }
    val stacksInput = lines.slice(0..sep - 1)
    val operations = lines.subList(sep + 1, lines.size)
    return stacksInput to operations
}

private fun parseStacks(stackInput: List<String>): List<MutableList<Char>> {
    val count = stackInput.last().count { it != ' ' }
    val stacks = (0..count - 1).map { mutableListOf<Char>() }
    stackInput.dropLast(1).reversed().forEach { line ->
        // NOTE: this won't work if number of stacks >= 10
        line.slice(1..line.length step 4).forEachIndexed { i, char ->
            if (char != ' ') stacks[i].add(char)
        }
    }
    return stacks
}


private data class Operation(val count: Int, val srcIndex: Int, val dstIndex: Int)

private fun parseOperation(line: String): Operation {
    val regex = Regex("move (\\d+) from (\\d+) to (\\d+)")
    return regex.find(line)!!.groupValues.drop(1).map { it.toInt() }
        .let { (num, src, dst) -> Operation(num, src - 1, dst - 1) }

}
