import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.system.measureNanoTime

typealias Coord = Pair<Int, Int>
/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

fun readInputRaw(name: String) = File("src", "$name.txt").readText()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')


fun <T> Collection<T>.sumOfIndexed(apply: (index: Int, T) -> Int): Int {
    return withIndex().sumOf { (i, v) -> apply(i, v) }
}

fun <T> Collection<T>.countIndexed(pred: (index: Int, T) -> Boolean): Int {
    return withIndex().count { (i, v) -> pred(i, v) }
}

fun <T> Collection<T>.maxOfIndexed(apply: (index: Int, T) -> Int): Int {
    return withIndex().maxOf { (i, v) -> apply(i, v) }
}

fun <T> List<List<T>>.transpose(): List<List<T>> {
    return List(this[0].size) { colId ->
        map { row -> row[colId] }
    }
}

// for benchmarking, print avg time used for specified sample sizes
fun measureAvgTime(sampleSizes: List<Int> = listOf(1, 10, 100, 1000), block: () -> Unit): List<Pair<Int, Double>> {
    return sampleSizes.zip(sampleSizes.map { count ->
        measureNanoTime {
            repeat(count) { block() }
        } / 1e6 / count
    })
}

inline fun repeat(n: Long, block: (idx: Long) -> Unit) {
    val m = n.mod(Int.MAX_VALUE).toLong()
    val full = n - m
    repeat ((n / Int.MAX_VALUE.toLong()).toInt()) { high ->
        repeat(Int.MAX_VALUE) { low ->
            block(high.toLong() * Int.MAX_VALUE + low)
        }
    }
    repeat(m.toInt()) {
        block(full + it)
    }
}
