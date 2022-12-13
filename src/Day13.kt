data class D13Node(val value: Int?, val children: MutableList<D13Node> = mutableListOf(), val parent: D13Node? = null): Comparable<D13Node> {
    // convenient function to create node and also add to parent
    companion object {
        fun makeLeaf(parent: D13Node?, value: Int) = D13Node(value, parent = parent).also { parent?.addChildren(it) }
        fun makeList(parent: D13Node?) = D13Node(null, parent = parent).also { parent?.addChildren(it) }
    }

    // Comparable<T> interface
    override fun compareTo(other: D13Node): Int {
        if (value == null) {
            // list
            val other = other.ensureRooted()
            val listCmp =
                children.zip(other.children) { left, right -> left.compareTo(right) }.firstOrNull { it != 0 } ?: 0
            return when (listCmp) {
                0 -> children.size.compareTo(other.children.size)
                else -> listCmp
            }

        } else {
            // elem
            return when (other.value) {
                null -> ensureRooted().compareTo(other)
                else -> value.compareTo(other.value)
            }
        }

    }

    private fun ensureRooted(): D13Node {
        if (value == null) return this
        return D13Node(null, mutableListOf(this))
    }

    fun addChildren(node: D13Node) {
        if (value != null) throw IllegalStateException("Cannot add child since this is not a list")
        children.add(node)
    }

    // for debugging
    fun print(indent: Int = 0) {
        if (value == null) {
            println("  ".repeat(indent) + "List (${children.size})")
            children.forEach { it.print(indent + 1)}
        }
        else {
            println("  ".repeat(indent) + "Item: $value")
        }

    }
}


fun main() {
    val input = readInput("Day13")
    val testInput = readInput("Day13_test")

    fun part1(input: List<String>): Int {
        return input.chunked(3)
            .map { it.take(2).map(::parseList)}
            .map { (left, right) -> left.compareTo(right)}
            .withIndex()
            .filter { (_, cmp) -> cmp == -1 }
            .map { (idx, ) -> idx + 1}
//            .also { println("correct order: $it ")}
            .sum()
    }

    fun part2(input: List<String>): Int {
        val div1 = parseList("[[2]]")
        val div2 = parseList("[[6]]")
        val all = input
            .filter { it.isNotBlank()}
            .map(::parseList)
            .plus(listOf(div1, div2))
            .sorted()
        val div1Index = all.binarySearch(div1) + 1
        val div2Index = all.binarySearch(div2) + 1
        println("$div1Index $div2Index")
        return div1Index * div2Index


    }
    println(part1(testInput))
    check(part1(testInput) == 13)

    println(part2(testInput))
    check(part2(testInput) == 140)

    println("Part1")
    println(part1(input))

    println("Part2")
    println(part2(input))

}

fun parseList(inp: String): D13Node {
    val root = D13Node.makeList(null)
    var cur = root
    var currentNum = 0
    var hasNum = false
    inp.forEach {
        when (it) {
            '[' -> {
                cur = D13Node.makeList(cur)
            }

            ']' -> {
                if (hasNum) {D13Node.makeLeaf(cur, currentNum); currentNum = 0; hasNum = false}
                cur = cur.parent!!
            }
            ',' -> {
                if (hasNum) {
                    D13Node.makeLeaf(cur, currentNum)
                    currentNum = 0
                    hasNum = false
                }
            }
            else -> (it - '0').also { currentNum = currentNum * 10 + it; hasNum = true }
        }
    }
    return root.children[0]
}



