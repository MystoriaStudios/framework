rootProject.name = "framework"

listOf("paper", "velocity", "independent").forEach {
    include(":$it")
    mutableListOf("$it:api", "$it:core")
        .forEach { module ->
            include(module)

            val compat = module.replace(":", "-")
            project(":$module").name = compat
        }
}