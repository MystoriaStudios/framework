package net.revive.framework.flavor.reflections

import net.revive.framework.flavor.FlavorOptions
import org.reflections.Reflections
import org.reflections.Store
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.QueryFunction
import java.lang.reflect.Method
import kotlin.reflect.KClass

/**
 * @author Dash
 * @since 4/15/2022
 */
class PackageIndexer(
    private val clazz: KClass<*>,
    options: FlavorOptions,
    loaders: List<ClassLoader> = emptyList(),
) {
    val reflections = Reflections(
        ConfigurationBuilder()
            .forPackage(
                options.`package` ?: this.clazz.java.`package`.name,
                *loaders.toTypedArray()
            )
            .addScanners(
                Scanners.MethodsAnnotated,
                Scanners.TypesAnnotated,
                Scanners.SubTypes
            )
    )

    inline fun <reified T> getSubTypes(): List<Class<*>> {
        return reflections
            .get(subTypes<T>())
            .toList()
    }

    inline fun <reified T : Annotation> getMethodsAnnotatedWith(): List<Method> {
        return reflections
            .get(annotated<T>())
            .toList()
    }

    inline fun <reified T : Annotation> getTypesAnnotatedWith(): List<Class<*>> {
        return reflections
            .getTypesAnnotatedWith(T::class.java)
            .toList()
    }

    inline fun <reified T> annotated(): QueryFunction<Store, Method> {
        return Scanners.MethodsAnnotated
            .with(T::class.java)
            .`as`(Method::class.java)
    }

    inline fun <reified T> subTypes(): QueryFunction<Store, Class<*>> {
        return Scanners.SubTypes
            .with(T::class.java)
            .`as`(Class::class.java)
    }
}
