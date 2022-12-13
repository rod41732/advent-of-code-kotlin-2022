import javax.swing.plaf.ColorUIResource

private sealed class Packet {
    data class PacketList(
        val parent: PacketList?,
        val children: MutableList<Packet> = mutableListOf()
    ) :
        Packet(), Comparable<PacketList> {
        override fun compareTo(other: PacketList): Int {
            val elementCmp = children.asSequence()
                .zip(other.children.asSequence()) { left, right -> left.compareTo(right) }
                .firstOrNull { it != 0 }
            return elementCmp ?: children.size.compareTo(other.children.size)
        }

        fun addElement(value: Int) { children.add(PacketNumber(value)) }
        fun addList(): PacketList = PacketList(this).also { children.add(it) }
        override fun print(indent: Int) {
            println("  ".repeat(indent)  + "List (${children.size} elements)")
        }
    }

    data class PacketNumber(val value: Int) : Packet(), Comparable<PacketNumber> {
        override fun compareTo(other: PacketNumber): Int = value.compareTo(other.value)
        override fun print(indent: Int) {
            println("  ".repeat(indent)  + "Item: $value")
        }
    }

    fun compareTo(other: Packet): Int {
        if (this is PacketList || other is PacketList) return this.asPacketList().compareTo(other.asPacketList())
        return (this as PacketNumber).compareTo(other as PacketNumber)
    }

    fun asPacketList(): PacketList = if (this is PacketList) this else PacketList(null, mutableListOf(this))

    abstract fun print(indent: Int = 0)
}



fun main() {
    val input = readInput("Day13")
    val testInput = readInput("Day13_test")

    fun part1(input: List<String>): Int {
        return input.chunked(3)
            .map { it.take(2).map(::parseList) }
            .map { (left, right) -> left.compareTo(right) }
            .withIndex()
            .filter { (_, cmp) -> cmp == -1 }
            .map { (idx) -> idx + 1 }
//            .also { println("correct order: $it ")}
            .sum()
    }

    fun part2(input: List<String>): Int {
        val dividers = listOf(
            parseList("[[2]]"),
            parseList("[[6]]")
        )
        val all = input
            .filter { it.isNotBlank() }
            .map(::parseList)
            .plus(dividers)
            .sorted()
        return dividers.map { all.binarySearch(it) + 1} .reduce { acc, i -> acc * i  }


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
        currentNum =  currentNum * 10 + digit
        hasNum = true
    }
    fun collectNumber() = if (hasNum) currentNum.also { hasNum = false } else null
}
private fun parseList(inp: String): Packet.PacketList {
    val root = Packet.PacketList(null)
    var currentList = root
    val numberBuilder = NumberBuilder()

    inp.forEach {
        when (it) {
            '[' -> currentList = currentList.addList()
            ']' -> {
                numberBuilder.collectNumber()?.also { currentList.addElement(it) }
                currentList = currentList.parent!!
            }
            ',' -> numberBuilder.collectNumber()?.also { currentList.addElement(it) }

            else -> numberBuilder.addDigit(it - '0')
        }
    }
    return root.children[0] as Packet.PacketList
}



