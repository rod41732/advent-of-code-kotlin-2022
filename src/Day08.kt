import java.util.Stack
import kotlin.math.max

typealias ScenicState = Pair<Stack<Pair<Int, Int>>, Int>

private enum class Side { UP, DOWN, LEFT, RIGHT }

private data class Tree(
    val height: Int,
    var up: Tree? = null,
    var down: Tree? = null,
    var left: Tree? = null,
    var right: Tree? = null,
) {
    val maxUp: Int by lazy { maxSeenTree(Side.UP) }
    val maxDown: Int by lazy { maxSeenTree(Side.DOWN) }
    val maxLeft: Int by lazy { maxSeenTree(Side.LEFT) }
    val maxRight: Int by lazy { maxSeenTree(Side.RIGHT) }

    val scenicUp: ScenicState by lazy { scenicSide(Side.UP) }
    val scenicDown: ScenicState by lazy { scenicSide(Side.DOWN) }
    val scenicLeft: ScenicState by lazy { scenicSide(Side.LEFT) }
    val scenicRight: ScenicState by lazy { scenicSide(Side.RIGHT) }

    val visible: Boolean by lazy { listOf(maxUp, maxDown, maxLeft, maxRight).any { it < height } }

    val scenicScore: Int by lazy {
        listOf(scenicUp, scenicDown, scenicLeft, scenicRight).map { it.second }.reduce { acc, it -> acc * it }
    }

    private fun nextTree(side: Side): Tree? {
        return when (side) {
            Side.UP -> up
            Side.DOWN -> down
            Side.LEFT -> left
            Side.RIGHT -> right
        }
    }

    private fun maxSeenTree(side: Side): Int {
        val next = nextTree(side)
        return if (next == null) -1 else max(next.maxSeenTree(side), next.height)
    }

    private fun calculateScenicAcc(prev: ScenicState): ScenicState {
        val (s, _) = prev
        var popped = 0
        while (!s.empty()) {
            val top = s.peek()
            if (top.first < height) {
                s.pop()
                popped += top.second
            } else {
                break
            }
        }
        val seen = popped + (if (s.isEmpty()) 0 else 1)
        s.push(height to popped + 1)
        return s to seen
    }

    private fun scenicSide(side: Side): ScenicState {
        val next = nextTree(side)
        return when (next) {
            null -> {
                val stack = Stack<Pair<Int, Int>>()
                stack.push(height to 1)
                return stack to 0
            }
            else -> calculateScenicAcc(next.scenicSide(side))
        }
    }
}

fun main() {
    val input = readInput("Day08")

    fun part1(input: List<String>): Int {
        val trees = parseGrid2(input)
        return trees.count { it.visible }
    }

    fun part2(input: List<String>): Int {
        val trees = parseGrid2(input)
        return trees.maxOf { it.scenicScore }
    }

    val testInput = readInput("Day08_test")
    println(part1(testInput))
    check(part1(testInput) == 21)
    println(part2(testInput))
    check(part2(testInput) == 8)

    println("Part 1")
    println(part1(input))

    println("Part 2")
    println(part2(input))
}

private fun parseGrid2(map: List<String>): List<Tree> {
    val tmp = map.map { it.map { char -> char - '0' }.map { Tree(it) } }
    tmp.forEachIndexed { i, row -> row.zipWithNext().forEach { (prev, cur) -> cur.left = prev; prev.right = cur } }
    tmp.transpose()
        .forEachIndexed { i, row -> row.zipWithNext().forEach { (prev, cur) -> cur.up = prev; prev.down = cur } }
    return tmp.flatten()
}
