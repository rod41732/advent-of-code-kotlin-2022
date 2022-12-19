import kotlin.math.max
import kotlin.math.pow

data class Day16State(val myPos: Int, val elephantPos: Int, val valveStates: Int) {
    fun nextPossibleStates(adj: List<List<Int>>, nonEmptyCount: Int) = sequence<Day16State> {
        if (myPos < nonEmptyCount) {
            yield(copy(valveStates = valveStates or (1 shl myPos)))
        }
        adj[myPos].forEach {
            yield(copy(myPos = it))
        }
    }.flatMap {
        sequence {
            it.apply {
                if (elephantPos < nonEmptyCount) {
                    yield(copy(valveStates = valveStates or (1 shl elephantPos)))
                }
                adj[elephantPos].forEach {
                    yield(copy(elephantPos = it))
                }
            }
        }

    }
}



fun main() {


    fun part1(input: List<String>): Int {
        val valves = input.map(::parseValveRaw).sortedDescending()
        val valveNameMap = valves.withIndex().map { it.value.name to it.index }.toMap()
        val adj = buildMap<Int, MutableList<Int>> {

            valves.forEachIndexed { thisIdx, it ->
                it.adjacentValves.map { adjName -> valveNameMap[adjName]!! }.forEach { adjIdx ->
                    this.getOrPut(thisIdx, { mutableListOf<Int>() }).add(adjIdx)
                }
            }
        }
        val flows = valves.map { it.flow }.takeWhile { it != 0 }
        val nonEmptyCount = valves.count { it.flow > 0 }
        val startIndex = valves.indexOfFirst { it.name == "AA" }
        val possibleStates = (2.0).pow(nonEmptyCount).toInt()

        val flowMemo = mutableMapOf<Int, Int>()
        fun calcFlow(mask: Int) = flowMemo.getOrPut(mask, {
            (0 until nonEmptyCount).sumOf { if (1 shl it and mask != 0) flows[it] else 0 }
        })


        var cur = List(valves.size) { MutableList(possibleStates) { Int.MIN_VALUE } }
        var next = List(valves.size) { MutableList(possibleStates) { Int.MIN_VALUE } }
        cur[startIndex][0] = 0 // first position

        repeat(30) {
            println("loop $it")
            cur.forEachIndexed { position, states ->
                states.forEachIndexed { state, value ->
                    if (value != Int.MIN_VALUE) {
                        // do nothing
                        next[position][state] = max(next[position][state], value + calcFlow(state))
                        // open valve
                        if (position < nonEmptyCount) {
                            next[position][state or (1 shl position)] =
                                max(next[position][state or (1 shl position)], value + calcFlow(state))
                        }
                        // move next
                        adj[position]!!.forEach { nextPos ->
                            next[nextPos][state] = max(next[nextPos][state], value + calcFlow(state))
                        }
                    }
                }
            }
            val tmp = next to cur
            cur = tmp.first
            next = tmp.second
        }
        return cur.maxOf { it.max() }
    }

    fun part2(input: List<String>): Int {
        val valves = input.map(::parseValveRaw).sortedDescending()
        val valveNameMap = valves.withIndex().map { it.value.name to it.index }.toMap()
        val adj = List(valves.size) { mutableListOf<Int>() }
        valves.forEachIndexed { thisIdx, it ->
            it.adjacentValves.map { adjName -> valveNameMap[adjName]!! }.forEach { adjIdx ->
                adj[thisIdx].add(adjIdx)
                adj[adjIdx].add(thisIdx)
            }
        }

        val flows = valves.map { it.flow }.takeWhile { it != 0 }
        val nonEmptyCount = valves.count { it.flow > 0 }
        val startIndex = valves.indexOfFirst { it.name == "AA" }
        val possibleStates = (2.0).pow(nonEmptyCount).toInt()

        val flowMemo = mutableMapOf<Int, Int>()
        fun calcFlow(mask: Int) = flowMemo.getOrPut(mask, {
            (0 until nonEmptyCount).sumOf { if (1 shl it and mask != 0) flows[it] else 0 }
        })

        val x = valves.size
        fun encode(a: Int, b: Int): Int {
            return a * x + b
        }

        fun decode(s: Int): Pair<Int, Int> {
            return s / x to s % x
        }

        var cur = List(valves.size * valves.size) { MutableList(possibleStates) { Int.MIN_VALUE } }
        var next = List(valves.size * valves.size) { MutableList(possibleStates) { Int.MIN_VALUE } }
        cur[encode(startIndex, startIndex)][0] = 0 // first position

        repeat(26) { time ->
            println("loop $time")
            cur.forEachIndexed { enc, states ->
                states.forEachIndexed { state, value ->
                    if (value != Int.MIN_VALUE) {
                        val v = value + calcFlow(state)
                        val (myPosition, elephantPosition) = decode(enc)
                        Day16State(myPosition, elephantPosition, state).nextPossibleStates(adj, nonEmptyCount)
                            .forEach { (myNextPos, elephantNextPos, nextState) ->
                                val s = encode(myNextPos, elephantNextPos)
                                next[s][nextState] = max(next[s][nextState], v)
                            }
                        cur[enc][state] = Int.MIN_VALUE
                    }
                }
            }
            val tmp = next to cur
            cur = tmp.first
            next = tmp.second
        }
        return cur.maxOf { it.max() }
    }

    val testInput = readInput("Day16_test")
    println(part1(testInput))
    check(part1(testInput) == 1651)
    println(part2(testInput))
    check(part2(testInput) == 1707)

    val input = readInput("Day16")
    println(part1(input))
    // WARNING: VERY LONG RUN TIME
    println(part2(input))

}

data class ValveRaw(val name: String, val flow: Int, val adjacentValves: List<String>) : Comparable<ValveRaw> {
    override fun compareTo(other: ValveRaw): Int {
        return flow.compareTo(other.flow).let {
            if (it != 0) it
            else name.compareTo(other.name)
        }
    }


}

private fun parseValveRaw(input: String): ValveRaw {
    val names = Regex("[A-Z]{2}").findAll(input).map { it.value }.toList()
    val flow = Regex("\\d+").find(input)!!.value.toInt()
    return ValveRaw(names[0], flow, names.subList(1, names.size))
}

