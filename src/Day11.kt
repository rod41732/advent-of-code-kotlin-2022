private typealias Operand = (v: Long) -> Long

// ALL calculations use Long to prevent integer overflow
private enum class Operator { ADD, MUL }
private data class Expression(val left: Operand, val op: Operator, val right: Operand) {
    fun evaluate(old: Long): Long {
        val lhs = left(old)
        val rhs = right(old)
        return when (op) {
            Operator.ADD -> lhs + rhs
            Operator.MUL -> lhs * rhs
        }
    }
}

private data class Monkey(
    val items: MutableList<Long>,
    val inspectExpression: Expression,
    val testMod: Long,
    val trueMonkeyNo: Int,
    val falseMonkeyNo: Int,
    var inspectionCount: Long = 0,
) {
    lateinit var trueMonkey: Monkey
    lateinit var falseMonkey: Monkey
    lateinit var afterInspect: (v: Long) -> Long

    fun receiveItem(item: Long) {
        items.add(item)
    }

    fun inspectAllItems() {
        inspectionCount += items.size
        items.map { afterInspect(inspectExpression.evaluate(it)) }.forEach {
            val targetMonkey = if (it % testMod == 0L) trueMonkey else falseMonkey
            targetMonkey.receiveItem(it)
        }
        items.clear()
    }
}

private fun parseMonkey(lines: List<String>): Monkey {
    val items = lines[1].substringAfter(": ").split(", ").map { it.toLong() }.toMutableList()
    val expression = lines[2].substringAfter("= ").split(" ").map { it }.let { (l, m, r) ->
        Expression(
            if (l == "old") { old -> old } else { old -> l.toLong() },
            if (m == "*") Operator.MUL else Operator.ADD,
            if (r == "old") { old -> old } else { old -> r.toLong() },
        )
    }
    val testMod = lines[3].substringAfter("by ").toLong()
    val trueMonkeyNo = lines[4].substringAfter("monkey ").toInt()
    val falseMonkeyNo = lines[5].substringAfter("monkey ").toInt()
    return Monkey(
        items = items,
        inspectExpression = expression,
        testMod = testMod,
        trueMonkeyNo = trueMonkeyNo,
        falseMonkeyNo = falseMonkeyNo,
    )
}

fun main() {
    val input = readInput("Day11")
    val inputTest = readInput("Day11_test")

    fun part1(input: List<String>): Int {
        val monkeys = input.chunked(7).map { parseMonkey(it) }.also { monkeys ->
            monkeys.forEach { it ->
                it.trueMonkey = monkeys[it.trueMonkeyNo]; it.falseMonkey = monkeys[it.falseMonkeyNo]
                it.afterInspect = { it / 3 }
            }
        }
        repeat(20) { monkeys.forEach { it.inspectAllItems() } }
        return monkeys.map { it.inspectionCount }.sortedDescending().take(2).reduce { acc, it -> acc * it }.toInt()
    }

    fun part2(input: List<String>, iterations: Int): Long {
        val monkeys = input.chunked(7).map { parseMonkey(it) }.also { monkeys ->
            // this is actually LCM of all monkeys' mod, but since all monkey's are all prime, the calculation is much easier
            val mod = monkeys.fold(1L) { acc, monkey -> acc * monkey.testMod }
            monkeys.forEach { it ->
                it.trueMonkey = monkeys[it.trueMonkeyNo]; it.falseMonkey = monkeys[it.falseMonkeyNo]
                it.afterInspect = { it % (mod) }
            }
        }
        repeat(iterations) { monkeys.forEach { it.inspectAllItems() } }
        return monkeys.map { it.inspectionCount }.sortedDescending().take(2)
            .fold(1) { acc, it -> acc * it }
    }

    check(part1(inputTest) == 10605)
    check(part2(inputTest, 1) == 6L * 4)
    check(part2(inputTest, 20) == 103L * 99)
    check(part2(inputTest, 1000) == 5204L * 5192L)

    println("Part 1")
    println(part1(input))
    println("Part 2")
    println(part2(input, 10000))


}
