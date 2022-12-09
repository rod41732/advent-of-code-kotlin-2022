import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

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
