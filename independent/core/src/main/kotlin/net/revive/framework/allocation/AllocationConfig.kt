package net.revive.framework.allocation

import net.revive.framework.config.JsonConfig

@JsonConfig("allocations.json")
class AllocationConfig(
    var allocations: MutableList<Allocation> = mutableListOf()
)