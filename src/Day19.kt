import kotlin.math.ceil
import kotlin.math.max
import kotlin.system.measureTimeMillis


data class Resource(
    val ore: Int = 0,
    val clay: Int = 0,
    val obsidian: Int = 0,
    val geode: Int = 0,
) {
    operator fun times(n: Int): Resource {
        return Resource(ore = ore * n, clay = clay * n, obsidian = obsidian * n, geode = geode * n)
    }

    operator fun plus(o: Resource): Resource {
        return Resource(
            ore = ore + o.ore,
            clay = clay + o.clay,
            obsidian = obsidian + o.obsidian,
            geode = geode + o.geode,
        )
    }

    operator fun minus(o: Resource): Resource {
        return Resource(
            ore = ore - o.ore,
            clay = clay - o.clay,
            obsidian = obsidian - o.obsidian,
            geode = geode - o.geode,
        )
    }

    fun canAfford(o: Resource): Boolean {
        return ore >= o.ore && clay >= o.clay && obsidian >= o.obsidian && geode >= o.geode
    }
}

inline fun divHelper(a: Int, b: Int): Int {
    if (a <= 0) return 0
    if (b == 0) throw Error("Divide by zero")
    return ceil(a.toFloat() / b.toFloat()).toInt()
}

data class State(
    val machine: Resource = Resource(), val resource: Resource = Resource(), val time: Int = 0, val blueprint: Blueprint
) {
    fun canGetAtLeastOneGeode(maxTime: Int): Boolean {
        if (machine.clay == 0) return time <= maxTime - 4
        if (machine.obsidian == 0) return time <= maxTime - 3
        if (machine.geode == 0) return time <= maxTime - 2
        return true
    }

    fun wastefulToBuildMoreOre(): Boolean = listOf(
        blueprint.oreCost.ore, blueprint.clayCost.ore, blueprint.obsidianCost.ore, blueprint.geodeCost.ore
    ).max().let { mx ->
        machine.ore >= mx && resource.ore >= mx
    }

    fun wastefulToBuildMoreClay(): Boolean = blueprint.obsidianCost.clay.let { mx ->
        machine.clay >= mx && resource.clay >= mx
    }

    fun wastefulToBuildMoreObsidian(): Boolean = blueprint.geodeCost.obsidian.let { mx ->
        machine.obsidian >= mx && resource.obsidian >= mx
    }

    inline fun bestNextStates(maxTime: Int, block: (State) -> Int): Int {
        return sequence {
            if (!canGetAtLeastOneGeode(maxTime = maxTime)) return@sequence
//        {
            if (!wastefulToBuildMoreOre()) {
                val oreWait = waitTime(blueprint.oreCost)
                if (oreWait != -1) {
                    val newTime = time + oreWait + 1
                    if (newTime < maxTime) {
                        yield(
                            copy(
                                time = newTime,
                                resource = resource + (machine * (oreWait + 1)) - blueprint.oreCost,
                                machine = machine.copy(ore = machine.ore + 1)
                            )
                        )

                    }
                }
            }
//        }();

//        {
            if (!wastefulToBuildMoreClay()) {
                val clayWait = waitTime(blueprint.clayCost)
                if (clayWait != -1) {
                    val newTime = time + clayWait + 1
                    if (newTime < maxTime) {
                        yield(
                            copy(
                                time = newTime,
                                resource = resource + (machine * (clayWait + 1)) - blueprint.clayCost,
                                machine = machine.copy(clay = machine.clay + 1)
                            )
                        )
                    }
                }
            }
//        }();

//        {
            if (!wastefulToBuildMoreObsidian()) {
                val obsidianWait = waitTime(blueprint.obsidianCost)
                if (obsidianWait != -1) {
                    val newTime = time + obsidianWait + 1
                    if (newTime < maxTime) {
                        yield(
                            copy(
                                time = newTime,
                                resource = resource + (machine * (obsidianWait + 1)) - blueprint.obsidianCost,
                                machine = machine.copy(obsidian = machine.obsidian + 1)
                            )
                        )
                    }
                }
            }
//        }();

//        (suspend {
            val geodeWait = waitTime(blueprint.geodeCost)
            if (geodeWait != -1) {
                val newTime = time + geodeWait + 1
                if (newTime < maxTime) {
                    yield(
                        copy(
                            time = time + geodeWait + 1,
                            resource = resource + (machine * (geodeWait + 1)) - blueprint.geodeCost,
                            machine = machine.copy(geode = machine.geode + 1)
                        )
                    )
                }

            }
//        })();
        }.maxOfOrNull { block(it) } ?: -1
    }



    inline fun waitTime(targetResource: Resource): Int {
        return runCatching {
            listOf(
                divHelper(targetResource.ore - resource.ore, machine.ore),
                divHelper(targetResource.clay - resource.clay, machine.clay),
                divHelper(targetResource.obsidian - resource.obsidian, machine.obsidian),
            ).max()
        }.let {
            val res = it.getOrNull()
            if (res == null) -1
            else max(res, 0)
        }
    }

    fun maxGeodeIfWait(maxTime: Int): Int {
        return resource.geode + (maxTime - time) * machine.geode
    }
}

data class Blueprint(
    val id: Int,
    val oreCost: Resource,
    val clayCost: Resource,
    val obsidianCost: Resource,
    val geodeCost: Resource,
) {
    fun qualityLevel(maxTime: Int): Int = id * maxGeode(maxTime)
    fun maxGeode(maxTime: Int): Int {
        fun best(fromState: State): Int {
            return max(fromState.maxGeodeIfWait(maxTime), fromState.bestNextStates(maxTime) { best(it) })
        }
        return best(State(machine = Resource(ore = 1), blueprint = this))
    }
}

val re =
    Regex("""Blueprint (\d+): Each ore robot costs (\d+) ore. Each clay robot costs (\d+) ore. Each obsidian robot costs (\d+) ore and (\d+) clay. Each geode robot costs (\d+) ore and (\d+) obsidian.""")

fun parseBlueprint(line: String): Blueprint {
    val nums = re.find(line)!!.groupValues.drop(1).map { it.toInt() }
    return Blueprint(
        id = nums[0],
        oreCost = Resource(ore = nums[1]),
        clayCost = Resource(ore = nums[2]),
        obsidianCost = Resource(ore = nums[3], clay = nums[4]),
        geodeCost = Resource(ore = nums[5], obsidian = nums[6]),
    )
}


fun main() {
    fun part1(input: List<String>): Int = input.map(::parseBlueprint).map { bp ->
        var x = 0;
//            measureTimeMillis {
        x = bp.qualityLevel(maxTime = 24)
//            }.let { println("Blueprint ${bp.id} ql = $x maxGeode = ${x / bp.id}, took $it millis") }
        x
    }.sum()

    fun part2(input: List<String>): Int = input.map(::parseBlueprint).take(3).map { bp ->
        var x = 0;
        measureTimeMillis {
            x = bp.maxGeode(maxTime = 32)
        }.let { println("Blueprint ${bp.id} maxGeode = ${x}, took $it millis") }
        x
    }.reduce {acc, it -> acc * it}

    val testInput = readInput("Day19_test")
    println(part1(testInput))
    check(part1(testInput) == 33)

    val input = readInput("Day19")
    println("Part 1")
    println(part1(input)) // 1365
    // took about 20 seconds
    println("Part 2")
    println(part2(input)) // 4864


}
