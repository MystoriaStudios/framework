package net.mystoria.framework.region.flag

import net.mystoria.framework.annotation.region.RegionFlag
import net.mystoria.framework.flavor.service.Configure
import net.mystoria.framework.plugin.ExtendedKotlinPlugin
import net.mystoria.framework.utils.objectInstance
import org.bukkit.Bukkit
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.hasAnnotation

object RegionFlagService {

    val registeredFlags = mutableMapOf<String, IRegionFlag>()

    fun configure(plugin: ExtendedKotlinPlugin) {
        plugin.packageIndexer
            .getTypesAnnotatedWith<RegionFlag>()
            .map(Class<*>::objectInstance)
            .filterIsInstance<IRegionFlag>()
            .forEach { it ->
                val clazz = it::class

                clazz.declaredFunctions.filter { f -> f.hasAnnotation<Configure>() }.forEach { method ->
                    method.call()
                }
                registeredFlags[clazz.simpleName ?: it.javaClass.simpleName] = it
            }
    }
}