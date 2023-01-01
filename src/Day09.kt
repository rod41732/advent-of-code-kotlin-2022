import kotlin.math.abs
import kotlin.math.sign

data class Knot(var x: Int, var y: Int, var next: Knot? = null) {
    var history = mutableSetOf(0 to 0)
    fun move(dir: Direction, count: Int) {
        repeat(count) { move(dir)  }
    }

    fun move(dir: Direction) {
        when (dir) {
            Direction.UP -> x += 1
            Direction.DOWN -> x -= 1
            Direction.RIGHT -> y += 1
            Direction.LEFT -> y -= 1
        }
        val next = next
        if (next != null && !next.isAdjTo(this)) {
            next.moveTowards(this)
        }
    }

    fun isAdjTo(other: Knot): Boolean {
        return abs( x - other.x) <= 1 && abs(y - other.y) <= 1
    }

    // called on "tail" only
    fun moveTowards(other: Knot) {
        other.let {
            x += (it.x - x).sign
            y += (it.y - y).sign
        }
        val next = next
        if (next != null && !next.isAdjTo(this)) {
            next.moveTowards(this)
        }
        history.add(x to y)
    }
}



private data class Command(val dir: Direction, val cnt: Int)

fun main() {
    val lines = readInput("Day09")

    fun part1(input: List<String>): Int {
        val ops = parseOps(input)
        val head = Knot(0, 0)
        val tail = Knot(0, 0)
        head.next = tail
        ops.forEach { head.move(it.dir, it.cnt) }
        return tail.history.size
    }

    fun part2(input: List<String>): Int {
        val ops = parseOps(input)
        val knots = List(10) { Knot (0, 0)}
        knots.zipWithNext().forEach { (cur, next) -> cur.next = next }
        val head = knots.first()
        val tail = knots.last()
        ops.forEach { head.move(it.dir, it.cnt) }
        return tail.history.size
    }

    val linesTest = readInput("Day09_test")
    println(part1(linesTest))
    check(part1(linesTest) == 13)

    val linesTest2 = readInput("Day09_test_2")
    println(part2(linesTest2))
    check(part2(linesTest2) == 36)

    println("Part 1")
    println(part1(lines))

    println("Part 2")
    println(part2(lines))
}

private fun parseOps(input: List<String>): List<Command> {
    return input.map {
        it.split(" ").let { (x, y) ->
            Command(
                when (x) {
                    "U" -> Direction.UP
                    "D" -> Direction.DOWN
                    "L" -> Direction.LEFT
                    else -> Direction.RIGHT
                },
                y.toInt()
            )
        }
    }

}
