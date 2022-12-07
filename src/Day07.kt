import kotlin.math.min

private data class Node(
    val name: String,
    val size: Int,
    val parent: Node? = null,
    val children: MutableList<Node> = mutableListOf(),
) {
    val totalSize: Int by lazy { size + children.sumOf { it.totalSize } }
    fun isDir(): Boolean = children.isNotEmpty()
}


private fun sumSmallDirectories(node: Node, sizeThres: Int): Int {
    if (!node.isDir()) return 0
    val currentDir = if (node.totalSize <= sizeThres) node.totalSize else 0
    return currentDir + node.children.sumOf { sumSmallDirectories(it, sizeThres) }
}

private fun smallestDirectoryToDelete(node: Node, sizeMin: Int): Int {
    if (!node.isDir()) return Int.MAX_VALUE
    return min(if (node.totalSize < sizeMin) Int.MAX_VALUE else node.totalSize,
        node.children.minOf { smallestDirectoryToDelete(it, sizeMin) })
}

private fun buildDirectoryTree(input: List<String>): Node {
    val root = Node("/", 0)
    var currentNode = root
    input.forEach {
        val args = it.split(" ")
        when (args[0]) {
            "$" -> when (args[1]) {
                "cd" -> when (args[2]) {
                    "/" -> currentNode = root
                    ".." -> currentNode = currentNode.parent!!
                    else -> currentNode = currentNode.children.find { it.name == args[2] }!!
                }

                "ls" -> {}
            }

            "dir" -> {
                val name = args[1]
                val dir = Node(name, 0, currentNode)
                currentNode.children.add(dir)
            }

            else -> {
                val size = args[0].toInt()
                val name = args[1]
                val file = Node(name, size, currentNode)
                currentNode.children.add(file)
            }
        }
    }
    return root
}

private const val DISK_SIZE = 70000000
private const val TARGET_FREE_SPACE = 30000000

fun main() {
    fun part1(input: List<String>): Int {
        val root = buildDirectoryTree(input)
        return sumSmallDirectories(root, 100_000)
    }

    fun part2(input: List<String>): Int {
        val root = buildDirectoryTree(input)
        val spaceToFree = TARGET_FREE_SPACE - (DISK_SIZE - root.totalSize)
        return smallestDirectoryToDelete(root, spaceToFree)
    }

    val linesTest = readInput("Day07_test")
    check(part1(linesTest) == 95437)
    check(part2(linesTest) == 24933642)

    val lines = readInput("Day07")
    println("Part 1")
    println(part1(lines))

    println("Part 2")
    println(part2(lines))
}
