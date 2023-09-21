package net.mystoria.framework.annotation.command.customizer

object CommandManagerCustomizers {

    val customizers = mutableListOf<Class<*>>()

    inline fun <reified T> default() {
        customizers.add(T::class.java)
    }
}
