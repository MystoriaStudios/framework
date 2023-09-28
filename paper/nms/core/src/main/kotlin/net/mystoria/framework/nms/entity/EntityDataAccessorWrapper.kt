package net.mystoria.framework.nms.entity

/**
 * @param entityClass We need to use reflection to get the class, no instances just the class
 * @param T The type of the thing, ex: Int, String ect
 */
class EntityDataAccessorWrapper<T>(
    var entityClass: Class<*>, // This is used for getting EntityDataAccessor,
    var typeClass: Class<*>
) {
    companion object {
        inline fun <reified T> of(entityClass: Class<*>): EntityDataAccessorWrapper<T> {
            return EntityDataAccessorWrapper(entityClass, T::class.java)
        }
    }
}