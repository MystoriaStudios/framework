package net.mystoria.framework.storage.type

/**
 * Represents the layer in which the
 * [Storable] will be pushed to.
 *
 * [ALL] - Pushes to both [MONGO] & [REDIS]
 */
enum class FrameworkStorageType(
    private val queryable: Boolean = true,
    private val queryableExtensively: Boolean = false
) {
    MONGO(
        queryableExtensively = true
    ),
    REDIS,
    CACHE,
    ALL(
        queryable = false
    );

    fun validateExtensive() {
        if (!queryableExtensively) throw IllegalStateException("Cannot use a basic query type.")
    }

    fun validate() {
        if (!queryable) throw IllegalStateException("Cannot use a none-queryable type.")
    }
}