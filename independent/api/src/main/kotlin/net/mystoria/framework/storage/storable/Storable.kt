package net.mystoria.framework.storage.storable

import java.util.*

/**
 * Represents a generic object storable
 * in any storage layer type.
 *
 * [Storable] instances are identified
 * via its [identifier]
 */
interface Storable {
    val identifier: UUID
}