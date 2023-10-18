package net.revive.framework.disguise.providers

import com.mongodb.client.model.Filters.eq
import net.revive.framework.controller.FrameworkObjectControllerCache
import net.revive.framework.disguise.IDisguiseInfoProvider
import net.revive.framework.disguise.StoredDisguiseInfo
import net.revive.framework.nms.disguise.DisguiseInfo
import net.revive.framework.storage.impl.MongoFrameworkStorageLayer
import net.revive.framework.storage.type.FrameworkStorageType
import java.util.*
import java.util.concurrent.CompletableFuture


object MongoDBDisguiseInfoProvider : IDisguiseInfoProvider {

    private val controller = FrameworkObjectControllerCache.create<StoredDisguiseInfo>()

    override fun getDisguiseInfo(uuid: UUID): CompletableFuture<DisguiseInfo?> {
        return controller.load(uuid, FrameworkStorageType.MONGO).thenApply { it }
    }

    override fun getDisguiseInfo(username: String): CompletableFuture<DisguiseInfo?> {
        return controller.useLayerWithReturn<MongoFrameworkStorageLayer<StoredDisguiseInfo>, CompletableFuture<StoredDisguiseInfo?>>(
            FrameworkStorageType.MONGO
        ) {
            this.loadWithFilter(eq("username", username))
        }.thenApply { it }
    }

}