import java.util.*

private data class Region3D(val mn: Point3D, val mx: Point3D) {
    fun contains(point: Point3D): Boolean {
        val (x1, y1, z1) = mn
        val (x2, y2, z2) = mx
        val (x, y, z) = point

        return (x1..x2).contains(x)
                && (y1..y2).contains(y)
                && (z1..z2).contains(z)
    }

    /** returns sequence of points inside this region */
    fun points() = sequence {
        val (x1, y1, z1) = mn
        val (x2, y2, z2) = mx
        (x1..x2).forEach { x ->
            (y1..y2).forEach { y ->
                (z1..z2).forEach { z ->
                    yield(Point3D(x, y, z))
                }
            }
        }
    }
}

data class Point3D(val x: Int, val y: Int, val z: Int) {
    fun adjacentPoints(): List<Point3D> {
        val tmp = listOf(-1, 1)
        return tmp.flatMap {
            listOf(
                Point3D(x + it, y, z),
                Point3D(x, y + it, z),
                Point3D(x, y, z + it),
            )
        }
    }
}

private fun Point3D.regionTo(o: Point3D) = Region3D(this, o)

private fun parseCube(input: String): Point3D {
    val (x, y, z) = input.split(",").map { it.toInt() }
    return Point3D(x, y, z)
}

/** return "bounding box" of the cubes, extending one unit in each direction */
private fun bbox(cubes: List<Point3D>): Pair<Point3D, Point3D> {
    return Point3D(
        x = cubes.minOf { it.x } - 1,
        y = cubes.minOf { it.y } - 1,
        z = cubes.minOf { it.z } - 1,
    ) to Point3D(
        x = cubes.maxOf { it.x } + 1,
        y = cubes.maxOf { it.y } + 1,
        z = cubes.maxOf { it.z } + 1,
    )
}

/** returns surface area of structure consisting of specified cubes */
private fun surfaceArea(cubes: List<Point3D>): Int {
    val processed = mutableSetOf<Point3D>()
    var surface = 0
    cubes.forEach {
        surface += 6
        // subtract touching surfaces
        surface -= it.adjacentPoints().count { adj -> adj in processed } * 2
        processed.add(it)
    }
    return surface
}

/** find hollow space inside the list of cubes, return list of cube needed to fill such hollow */
private fun findHollow(cubes: List<Point3D>): List<Point3D> {
    val (mn, mx) = bbox(cubes)
    val areaLimit = mn.regionTo(mx)

    // bfs to find all reachable nodes
    val existing = cubes.toMutableSet()
    val q: Queue<Point3D> = LinkedList()
    q.add(mn)
    while (!q.isEmpty()) {
        val cur = q.remove()
        cur.adjacentPoints()
            .filter { areaLimit.contains(it) }
            .filter { it !in existing }
            .forEach { q.add(it); existing.add(it) }
    }

    return areaLimit.points().filter { it !in existing }.toList()
}

fun main() {
    fun part1(input: List<String>) = surfaceArea(input.map(::parseCube))
    fun part2(input: List<String>) = input.map(::parseCube).let {
        surfaceArea(it) - surfaceArea(findHollow(it))
    }

    val testInput = readInput("Day18_test")
    println(part1(testInput))
    check(part1(testInput) == 64)
    check(part2(testInput) == 58)


    val input = readInput("Day18")
    println("Part 1")
    println(part1(input))
    println("Part 2")
    println(part2(input))
}

