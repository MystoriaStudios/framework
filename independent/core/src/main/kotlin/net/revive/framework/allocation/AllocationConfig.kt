package net.revive.framework.allocation

import net.revive.framework.config.JsonConfig

@JsonConfig("allocations.json")
class AllocationConfig(
    var allocations: MutableList<Allocation> = mutableListOf<Allocation>().apply {
        for (i in 25565..25570) {
            this.add(Allocation("0.0.0.0", port = i))
        }
    }
)