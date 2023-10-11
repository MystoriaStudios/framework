package net.revive.framework.cache

import net.revive.framework.cache.impl.ILocalUUIDCacheTranslator
import org.bukkit.Bukkit
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

class PaperLocalUUIDCacheTranslator : ILocalUUIDCacheTranslator() {
    override fun configure(): CompletionStage<Void> {
        return CompletableFuture.completedFuture(null)
    }

    override fun preLoadCache(async: Boolean) {

    }

    override fun update(username: String, uuid: UUID) {

    }

    override fun update(response: UUIDCacheHelper.MojangResponse, commit: Boolean) {

    }

    override fun uniqueId(username: String): UUID? {
        return Bukkit.getOnlinePlayers().firstOrNull { it.name == username }?.uniqueId
    }

    override fun username(uuid: UUID): String? {
        return Bukkit.getOnlinePlayers().firstOrNull { it.uniqueId == uuid }?.name
    }
}