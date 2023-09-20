package net.mystoria.framework.controller

import net.mystoria.framework.serializer.FrameworkSerializer
import net.mystoria.framework.storage.FrameworkStorageLayer
import net.mystoria.framework.storage.storable.Storable
import net.mystoria.framework.storage.type.FrameworkStorageType
import kotlin.reflect.KClass

class FrameworkObjectController<D : Storable>(
    private val dataType: KClass<D>
) {
    val localLayerCache = mutableMapOf<FrameworkStorageType, FrameworkStorageLayer<*, D, *>>()
}