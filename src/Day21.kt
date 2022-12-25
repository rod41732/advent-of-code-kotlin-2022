
fun main() {
    fun part1(input: List<String>): Long {
        val ctx = ExpressionContext()
        input.forEach {
            val parts = it.split(" ")
            val name = parts[0].substring(0, 4)
            val cur = ctx.get(name)
            when (parts.size) {
                2 -> cur.num = parts[1].toLong()
                4 -> {
                    val (l, m, r) = parts.subList(1, 4)
                    cur.left = ctx.get(l)
                    cur.right = ctx.get(r)
                    cur.op = m
                }
            }
        }
        return ctx.get("root").calculatedValue
    }

    fun part2(input: List<String>): Long {
        val ctx = ExpressionContext()
        input.forEach {
            val parts = it.split(" ")
            val name = parts[0].substring(0, 4)
            val cur = ctx.get(name)
            if (name == "humn") {
                cur.isUnknown = true
                return@forEach
            }
            when (parts.size) {
                2 -> cur.num = parts[1].toLong()
                4 -> {
                    val (l, m, r) = parts.subList(1, 4)
                    cur.left = ctx.get(l)
                    cur.right = ctx.get(r)
                    cur.op = m
                }
            }
        }
        val root = ctx.get("root")
        var (human, monkey) = ctx.get("root").children().map { it.value }
//        while (human != ctx.get("humn")) {
        while( human != ctx.get("humn")) {

            solve(human, monkey).let { (x, y) ->
                    human = x
                    monkey = y
            }
        }
//        }
        return monkey.calculatedValue
    }

    val testInput = readInput("Day21_test")
    println(part1(testInput))
    check(part1(testInput) == 152L)
    println(part2(testInput))
    check(part2(testInput) == 301L)

    val input = readInput("Day21")
    println("Part1")
    println(part1(input)) // 51928383302238
    println("Part2")
    println(part2(input)) // 3305669217840
}

fun solve(withUnknown: Expression2, constant: Expression2): Pair<Expression2, Expression2> {
    val (unknown, known) = withUnknown.children()
    return when (withUnknown.op!!) {
        "+" -> unknown.value to Expression2(left = constant, op = "-", right = known.value)
        "-" -> unknown.value to Expression2(left = known.value, op = if (known.index == 0) "-" else "+", right = constant)
        "*" -> unknown.value to Expression2(left = constant, op = "/", right = known.value)
        "/" -> unknown.value to Expression2(left = known.value, op = if (known.index == 0) "/" else "*", right = constant)
        else -> throw Error("XD")
    }
}
data class Expression2(
    var num: Long? = null, var left: Expression2? = null, var op: String? = null, var right: Expression2? = null,
    var isUnknown: Boolean = false,
) {
    val calculatedValue: Long by lazy {
        if (num != null) num!!
        else {
            val l = left!!
            val r = right!!
            when (op!!) {
                "+" -> l.calculatedValue + r.calculatedValue
                "-" -> l.calculatedValue - r.calculatedValue
                "*" -> l.calculatedValue * r.calculatedValue
                "/" -> l.calculatedValue / r.calculatedValue
                else -> throw Error("XD")
            }
        }
    }

    val containsUnknown: Boolean by lazy {
        when {
            isUnknown -> true
            num != null -> false
            else -> left!!.containsUnknown || right!!.containsUnknown
        }
    }

    /** return 2 tuple of indexedvalue, first element's value always contains unknown, seconds doesn't,
     * the index allow determining if it's left or right child
     */
    fun children(): List<IndexedValue<Expression2>> {
        return listOf(left!!, right!!).withIndex().sortedBy { if (it.value.containsUnknown) 0 else 1 }
    }

    override fun toString(): String {
        if (num != null) return num!!.toString()
        else if (isUnknown) return "X"
        return "($left $op $right)"
    }
}

data class ExpressionContext(private val x: MutableMap<String, Expression2> = mutableMapOf()) {
    fun get(name: String): Expression2 = x.getOrPut(name) { Expression2() }
}
