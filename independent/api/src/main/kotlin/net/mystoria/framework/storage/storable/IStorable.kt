package net.mystoria.framework.storage.storable

import java.util.*

/**
 * Represents a generic object storable
 * in any storage layer type.
 *
 * [IStorable] instances are identified
 * via its [identifier]
 */
interface IStorable {
    val identifier: UUID
}