package net.mystoria.framework

import net.mystoria.framework.flavor.Flavor
import net.mystoria.framework.flavor.FlavorOptions
import net.mystoria.framework.loader.FrameworkModuleLoader
import java.io.File

class FrameworkApp {

    lateinit var loader: FrameworkModuleLoader
    lateinit var flavor: Flavor

    fun main(args: Array<String>) {
        loader = FrameworkModuleLoader(File("modules"))
        Framework.supply(IndependentFramework) {
            flavor = Flavor(this::class, FlavorOptions(it.logger))
            flavor.bind<FrameworkModuleLoader>() to loader
        }

        loader.startup()

        flavor.startup()
    }
}