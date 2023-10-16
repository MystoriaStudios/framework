package net.revive.framework.disguise.providers

import net.revive.framework.disguise.IDisguiseInfoProvider
import net.revive.framework.nms.disguise.DisguiseInfo
import java.util.*
import java.util.concurrent.CompletableFuture

object MojangDisguiseInfoProvider : IDisguiseInfoProvider {

    override fun getDisguiseInfo(uuid: UUID): CompletableFuture<DisguiseInfo?> {
        TODO("Not yet implemented")
    }

    override fun getDisguiseInfo(username: String): CompletableFuture<DisguiseInfo?> {
        TODO("Not yet implemented")
    }

}