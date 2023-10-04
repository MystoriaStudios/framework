package net.revive.framework.region.flag

import net.revive.framework.annotation.region.RegionFlag
import net.revive.framework.flavor.service.Configure
import net.revive.framework.plugin.ExtendedKotlinPlugin
import net.revive.framework.utils.objectInstance
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