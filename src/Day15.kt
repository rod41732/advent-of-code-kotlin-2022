import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

data class Coord2D(val x: Int, val y: Int) {
    fun manhattanDistance(other: Coord2D): Int {
        return (x - other.x).absoluteValue + (y - other.y).absoluteValue
    }
}


data class SensorResult(val sensor: Coord2D, val nearestBeacon: Coord2D) {
    var nearestDist = sensor.manhattanDistance(nearestBeacon)
    fun cannotContainBeacon(y: Int): IntRange {
        val dy = (sensor.y - y).absoluteValue
        if (dy > nearestDist) return IntRange.EMPTY
        val dx = nearestDist - dy
        return sensor.x - dx..sensor.x + dx
    }
}

fun main() {
    val input = readInput("Day15")
    fun part1(input: List<String>, yToConsider: Int): Int {
        val beacons = input.map(::parseBeaconLine)
        val nonPossiblePositions = beacons.map { it.cannotContainBeacon(yToConsider) }.filter { !it.isEmpty() }
            .sortedBy { it.first }.fold(Int.MIN_VALUE to 0) { (lastX, count), range ->
                val left = range.first
                val right = range.last
                val overlaps = min(max(lastX + 1 - left, 0), range.count())
                max(right, lastX) to count + range.count() - overlaps
            }.second
        // be careful to not count the beacon at same position twice
        val beaconsInThatY =
            beacons.filter { it.nearestBeacon.y == yToConsider }.map { it.nearestBeacon.x }.toSet().size
        return nonPossiblePositions - beaconsInThatY
    }

    fun part2(input: List<String>, searchSpace: Int): Long {
        val beacons = input.map(::parseBeaconLine)
        (0 .. searchSpace) .forEach { yPos ->
            var lastX = -1
            beacons.map { it.cannotContainBeacon(yPos) }
                .also {
                    if (yPos == 1257) {
                        println(it.filter{!it.isEmpty()}.sortedBy { it.first } )
                    }
                }
                .filter { !it.isEmpty() }
                .sortedBy { it.first }
                .forEach { range ->
                    if (lastX > searchSpace) return@forEach
                    val left = range.first
                    val right = range.last
                    if (left - lastX >= 2) {
                        val foundX = lastX + 1
                        val foundY = yPos
                        return 4_000_000L * foundX + foundY
                    }
                    lastX = max(lastX, right)
                }

        }
        throw Exception("Bad Input, no beacon found")
    }

    val inputTest = readInput("Day15_test")
    println(part1(inputTest, yToConsider = 10))
    check(part1(inputTest, yToConsider = 10) == 26)
    println(part2(inputTest, searchSpace = 20))
    check(part2(inputTest, searchSpace = 20) == 56000011L)

    println("Part1")
    println(part1(input, yToConsider = 2_000_000)) // 54036
    println("Part2")
    println(part2(input, searchSpace = 4_000_000)) // 13237873355

}

private fun parseBeaconLine(input: String): SensorResult {
    val (a, b, c, d) = Regex("[-0-9]+").findAll(input).map { it.value.toInt() }.toList()
    return SensorResult(Coord2D(a, b), Coord2D(c, d))
}
