package net.revive.framework.cache

import net.revive.framework.cache.impl.ILocalUUIDCacheTranslator
import net.revive.framework.cache.impl.distribution.DistributedRedisUUIDCache
import net.revive.framework.flavor.annotation.Inject
import java.util.*

object UUIDCache
{
    @Inject
    private lateinit var localTranslator: ILocalUUIDCacheTranslator
    private var translator: IUUIDCacheTranslator = DistributedRedisUUIDCache

    fun configure(translator: ILocalUUIDCacheTranslator)
    {
        this.translator = translator

        net.revive.framework.Framework.use {
            it.log("Framework UUID Cache", "Loading UUIDCache with ${translator.javaClass.simpleName}...")

            this.translator.configure()
                .thenRun {
                    this.translator.preLoadCache(async = false)

                    it.log("Framework UUID Cache", "Loaded UUIDCache with ${translator.javaClass.simpleName}!")
                }
        }
    }

    fun uniqueId(username: String): UUID?
    {
        var translated = localTranslator
            .uniqueId(username)

        if (translated != null) return translated


        translated = translator
            .uniqueId(username)

        if (translated != null) return translated

        // TODO: send backend reqeust
        /*
        val translated = translator
            .uniqueId(username)

        if (translated != null)
            return translated

        val mojang = UUIDCacheHelper
            .fetchFromMojang(username)

        if (mojang != null)
        {
            translator.update(mojang, commit = true)

            return mojang.id
        }


         */
        return null
    }

    fun username(uniqueId: UUID): String?
    {
        val translated = translator
            .username(uniqueId)

        if (translated != null)
            return translated

        val mojang = UUIDCacheHelper
            .fetchFromMojang(uniqueId)

        if (mojang != null)
        {
            translator.update(mojang, commit = true)

            return mojang.name
        }

        return null
    }
}