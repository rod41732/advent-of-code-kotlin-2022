fun main() {
    val input = readInput("Day13")
    val testInput = readInput("Day13_test")

    fun part1(input: List<String>): Int {
        return input.filter { it.isNotBlank() }.map(::parseList).chunked(2)
            .sumOfIndexed { idx, (x, y) -> if (x.compareTo(y) == -1) idx + 1 else 0 }
    }

    fun part2(input: List<String>): Int {
        val dividers = listOf(parseList("[[2]]"), parseList("[[6]]"))
        val all = input.filter { it.isNotBlank() }.map(::parseList).plus(dividers).sorted()
        return dividers.map { all.binarySearch(it) + 1 }.reduce { acc, i -> acc * i }
    }

    println(part1(testInput))
    check(part1(testInput) == 13)

    println(part2(testInput))
    check(part2(testInput) == 140)

    println("Part1")
    println(part1(input)) // 6428

    println("Part2")
    println(part2(input)) // 22464

}

private data class NumberBuilder(var currentNum: Int = 0, var hasNum: Boolean = false) {
    fun addDigit(digit: Int) {
        currentNum = currentNum * 10 + digit
        hasNum = true
    }

    fun collectNumber() = if (hasNum) currentNum.also { hasNum = false; currentNum = 0 } else null
}

private fun parseList(inp: String): Packet {
    val root = PacketList(null)
    var currentList = root
    val numberBuilder = NumberBuilder()

    inp.forEach {
        when (it) {
            '[' -> currentList = currentList.addList()
            ']' -> {
                numberBuilder.collectNumber()?.also { currentList.addElement(it) }
                currentList = currentList.parent!!
            }

            ' ' -> {}
            ',' -> numberBuilder.collectNumber()?.also { currentList.addElement(it) }
            else -> numberBuilder.addDigit(it - '0')
        }
    }
    return root.children[0]
}

private sealed class Packet : Comparable<Packet> {
    override fun compareTo(other: Packet): Int {
        if (this is PacketNumber && other is PacketNumber) return value.compareTo(other.value)
        return compareList(asPacketList(), other.asPacketList())
    }

    fun compareList(left: PacketList, right: PacketList): Int {
        left.children.zip(right.children)
            .forEach { (left, right) -> left.compareTo(right).let { if (it != 0) return it } }
        return left.children.size.compareTo(right.children.size)
    }

    fun asPacketList(): PacketList = if (this is PacketList) this else PacketList(null, mutableListOf(this))
}

private data class PacketList(
    val parent: PacketList?, val children: MutableList<Packet> = mutableListOf()
) : Packet() {

    fun addElement(value: Int) {
        children.add(PacketNumber(value))
    }

    fun addList(): PacketList = PacketList(this).also { children.add(it) }
}


private data class PacketNumber(val value: Int) : Packet()

