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

fun <T> Collection<T>.partitionWhenIndexed(pred: (index: Int, T) -> Boolean): MutableList<MutableList<T>> {
    return foldIndexed(mutableListOf()) { idx, acc, item ->
        when {
            idx == 0 || pred(idx, item) -> acc.add(mutableListOf(item))
            else -> acc.last().add(item)
        }
        acc
    }
}
