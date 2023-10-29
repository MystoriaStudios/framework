rootProject.name = "framework"

listOf("paper", "velocity", "independent", "minestom").forEach {
    include(":$it")
    mutableListOf("$it:api", "$it:core")
        .forEach { module ->
            include(module)

            val compat = module.replace(":", "-")
            project(":$module").name = compat
        }
}
include(":independent:backend")

include(":mc-commons")
project(":mc-commons").name = "minecraft-platform"

include(":paper:nms")
include("paper:nms:core")
project(":paper:nms:core").name = "nms-core"

fun registerNMS(ver: String) {
    val module = "paper:nms:v$ver"
    include("paper:nms:v$ver")

    project(":$module").name = "nms-v$ver"
}


listOf("1_20_R1", "1_19_R4", "1_18_R2", "1_17_R1", "1_12_R2", "1_8_R8").forEach {
    registerNMS(it)
}