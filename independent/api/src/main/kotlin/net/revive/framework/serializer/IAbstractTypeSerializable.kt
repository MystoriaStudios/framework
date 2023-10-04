package net.revive.framework.serializer

import java.lang.reflect.Type

interface IAbstractTypeSerializable {

    fun getAbstractType(): Type {
        throw IllegalStateException("Serializable abstract type has not been setup")
    }

}