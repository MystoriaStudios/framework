package net.mystoria.framework.cache

import java.util.UUID
import java.util.concurrent.CompletionStage

interface IUUIDCacheTranslator
{
    val uniqueIdCache: MutableMap<UUID, String>
    val usernameCache: MutableMap<String, UUID>

    fun configure() : CompletionStage<Void>

    fun preLoadCache(async: Boolean = true)

    fun update(response: UUIDCacheHelper.MojangResponse, commit: Boolean = true)

    fun uniqueId(username: String): UUID?

    fun username(uuid: UUID): String?
}