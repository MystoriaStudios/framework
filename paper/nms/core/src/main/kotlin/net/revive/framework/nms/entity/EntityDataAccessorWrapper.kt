package net.revive.framework.nms.entity

/**
 * @param T The type of the thing, ex: Int, String ect
 */
class EntityDataAccessorWrapper<T>(
    var typeClass: Class<*>,
    var bitField: Int,
    var bitFlag: Int = -1
) {
    companion object {
        inline fun <reified T> of(bitField: Int): EntityDataAccessorWrapper<T> {
            return EntityDataAccessorWrapper(T::class.java, bitField)
        }

        inline fun <reified T> of(bitField: Int, bitFlag: Int): EntityDataAccessorWrapper<T> {
            return EntityDataAccessorWrapper(T::class.java, bitField, bitFlag)
        }
    }
}