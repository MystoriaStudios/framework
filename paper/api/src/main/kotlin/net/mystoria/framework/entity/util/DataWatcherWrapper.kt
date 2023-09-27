package net.mystoria.framework.entity.util

import net.mystoria.framework.flavor.annotation.Inject


inline fun DataWatcherEditor(builder: DataWatcherWrapper.() -> Unit) = DataWatcherWrapper.apply(builder)

class DataWatcherWrapper {

    companion object {
        @Inject
        lateinit var
    }
}
