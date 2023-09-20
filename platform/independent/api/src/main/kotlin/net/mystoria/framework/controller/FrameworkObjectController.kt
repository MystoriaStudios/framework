package net.mystoria.framework.controller

import net.mystoria.framework.storage.storable.Storable
import kotlin.reflect.KClass

class FrameworkObjectController<D : Storable>(
    private val dataType: KClass<D>
) {
    val localLayerCache
}