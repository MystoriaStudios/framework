package net.revive.framework.updater.repository

import net.revive.framework.updater.repository.repositories.ComponentPaginatedRepositoryWrapper
import net.revive.framework.serializer.impl.GsonSerializer
import net.revive.framework.updater.connection.NexusUpdaterConnector
import java.util.concurrent.CompletableFuture

object Repositories
{

    const val COMPONENTS = "https://ci.nopox.xyz/service/rest/v1/components?repository=maven-releases"

    /**
     * Retrieve all components within the
     * releases' category.
     *
     * Note: Nexus makes use of a pagination feature,
     * so we have to make multiple requests to retrieve all components.
     */
    fun components(): ComponentPaginatedRepositoryWrapper
    {
        var request = NexusUpdaterConnector.request(COMPONENTS)

        val wrapper = ComponentPaginatedRepositoryWrapper()
        var iterated: ComponentPaginatedRepositoryWrapper? = null

        while (iterated == null || iterated.continuationToken != null)
        {
            if (iterated?.continuationToken != null)
            {
                request = NexusUpdaterConnector.request(
                    COMPONENTS + "&continuationToken=${iterated.continuationToken}"
                )
            }

            iterated = GsonSerializer.gson
                .fromJson(
                    request.asJson().body.toPrettyString(),
                    ComponentPaginatedRepositoryWrapper::class.java
                )

            if (iterated != null)
            {
                wrapper.items.addAll(iterated.items)
            }
        }

        return wrapper
    }

    fun asyncComponents(): CompletableFuture<ComponentPaginatedRepositoryWrapper>
    {
        return CompletableFuture
            .supplyAsync { components() }
    }
}
