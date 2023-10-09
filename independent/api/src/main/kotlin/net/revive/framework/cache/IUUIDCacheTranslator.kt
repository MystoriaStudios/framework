package net.revive.framework.cache

import java.util.*
import java.util.concurrent.CompletionStage

interface IUUIDCacheTranslator
{
    fun configure() : CompletionStage<Void>

    fun preLoadCache(async: Boolean = true)

    fun update(response: UUIDCacheHelper.MojangResponse, commit: Boolean = true)

    fun uniqueId(username: String): UUID?

    fun username(uuid: UUID): String?
}