package net.revive.framework.disguise;

import net.revive.framework.nms.disguise.DisguiseInfo;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a provider for fetching disguise-related information.
 */
interface IDisguiseInfoProvider {

    /**
     * Asynchronously fetches the disguise information associated with a specific UUID.
     *
     * @param uuid The unique identifier for which to retrieve disguise details.
     * @return A future that may complete with the associated disguise information or null if not present.
     */
    fun getDisguiseInfo(uuid: UUID): CompletableFuture<DisguiseInfo?>

    /**
     * Asynchronously fetches the disguise information linked to a specific username.
     *
     * @param username The name of the user for which to obtain disguise details.
     * @return A future that may complete with the associated disguise information or null if not present.
     */
    fun getDisguiseInfo(username: String): CompletableFuture<DisguiseInfo?>
}
