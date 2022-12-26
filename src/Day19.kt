import java.util.*
import kotlin.math.ceil
import kotlin.math.max


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

fun divHelper(a: Int, b: Int): Int {
    if (a == b && b == 0) return 0
    if (a != 0 && b == 0) throw Error("Divide by zero")
    return ceil(a / b.toFloat()).toInt()
}

data class State(
    val machine: Resource = Resource(), val resource: Resource = Resource(), val time: Int = 0, val blueprint: Blueprint
) {

    fun nextStates() = sequence<State> {
        val oreWait = waitTime(blueprint.oreCost)
        if (oreWait != -1) {
            val newTime = time + oreWait + 1
            if (newTime <= 24) {
                yield(
                    copy(
                        time = newTime,
                        resource = resource + (machine * (oreWait + 1)) - blueprint.oreCost,
                        machine = machine.copy(ore = machine.ore + 1)
                    )
                )

            }
        }

        val clayWait = waitTime(blueprint.clayCost)
        if (clayWait != -1) {
            val newTime = time + clayWait + 1
            val op = clayWait != 0 && (resource + machine * clayWait).let { future ->
                listOf(blueprint.oreCost, blueprint.obsidianCost, blueprint.geodeCost).any {
                    future.canAfford(it)
                }
            }
            if (!op) {
                if (newTime <= 24) {
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

        val obsidianWait = waitTime(blueprint.obsidianCost)
        if (obsidianWait != -1) {
            val newTime = time + obsidianWait + 1
            val op = obsidianWait != 0 && (resource + machine * obsidianWait).let { future ->
                listOf(blueprint.oreCost, blueprint.clayCost, blueprint.geodeCost).any {
                    future.canAfford(it)
                }
            }
            if (!op) {
                if (newTime <= 24) {
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

        val geodeWait = waitTime(blueprint.geodeCost)
        if (geodeWait != -1) {
            val newTime = time + geodeWait + 1
            val op = geodeWait != 0 && (resource + machine * obsidianWait).let { future ->
                listOf(blueprint.oreCost, blueprint.clayCost, blueprint.obsidianCost).any {
                    future.canAfford(it)
                }
            }
            if (!op) {
                if (newTime <= 24) {
                    yield(
                        copy(
                            time = time + geodeWait + 1,
                            resource = resource + (machine * (geodeWait + 1)) - blueprint.geodeCost,
                            machine = machine.copy(geode = machine.geode + 1)
                        )
                    )
                }

            }
        }
    }


    private fun waitTime(targetResource: Resource): Int {
        return runCatching {
            max(
                listOf(
                    divHelper(targetResource.ore - resource.ore, machine.ore),
                    divHelper(targetResource.clay - resource.clay, machine.clay),
                    divHelper(targetResource.obsidian - resource.obsidian, machine.obsidian),
                ).max(), 0
            )
        }.getOrDefault(-1)
    }

    fun maxGeodeIfWait(): Int {
        return resource.geode + (time - 24) * machine.geode
    }
}

data class Blueprint(
    val id: Int,
    val oreCost: Resource,
    val clayCost: Resource,
    val obsidianCost: Resource,
    val geodeCost: Resource,
) {
    fun qualityLevel(): Int = id * maxGeode()
    fun maxGeode(): Int {
        val q: Queue<State> = LinkedList();
        q.add(State(machine = Resource(ore = 1), blueprint = this))
        var ans = 0
        while (!q.isEmpty()) {
            val s = q.remove()
            if (s.time > 24) continue
            ans = max(ans, s.maxGeodeIfWait())
            s.nextStates().forEach { q.add(it) }
        }
        return ans
    }
}

fun parseBlueprint(line: String): Blueprint = throw NotImplementedError("XD")

fun main() {
    fun part1(input: List<String>): Int = input.map(::parseBlueprint).map { it.qualityLevel() }.sum()

    val bp1 = Blueprint(
        id = 1,
        oreCost = Resource(ore = 4),
        clayCost = Resource(ore = 2),
        obsidianCost = Resource(ore = 3, clay = 14),
        geodeCost = Resource(ore = 2, obsidian = 7)
    )
    measureAvgTime(sampleSizes = listOf(1)) {
        println(bp1.qualityLevel())
    }.forEach { (c, v) -> println("took $v seconds") }
    check(bp1.qualityLevel() == 9)

    val bp2 = Blueprint(
        id = 2,
        oreCost = Resource(ore = 2),
        clayCost = Resource(ore = 3),
        obsidianCost = Resource(ore = 3, clay = 8),
        geodeCost = Resource(ore = 3, obsidian = 12)
    )
    measureAvgTime(sampleSizes = listOf(1)) {
        println(bp2.qualityLevel())
    }.forEach { (c, v) -> println("took $v seconds") }
//    check(bp2.qualityLevel() == 12 * 2)


}
