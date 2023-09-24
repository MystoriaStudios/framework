package net.mystoria.framework.cache

import net.mystoria.framework.Framework
import net.mystoria.framework.cache.impl.LocalUUIDCacheTranslator
import java.util.*

object UUIDCache
{
    private lateinit var translator: IUUIDCacheTranslator

    fun configure(translator: IUUIDCacheTranslator)
    {
        this.translator = translator

        Framework.use {
            it.log("Framework UUID Cache", "Loading UUIDCache with ${translator.javaClass.simpleName}...")

            this.translator.configure()
                .thenRun {
                    this.translator.preLoadCache(async = false)

                    it.log("Framework UUID Cache", "Loaded UUIDCache with ${translator.javaClass.simpleName}!")
                }
        }
    }

    fun useTranslator(lambda: IUUIDCacheTranslator.() -> Unit)
    {
        lambda.invoke(translator)
    }

    fun uniqueId(username: String): UUID?
    {
        validateTranslator()

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

        return null
    }

    fun username(uniqueId: UUID): String?
    {
        validateTranslator()

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

    private fun validateTranslator()
    {
        try
        {
            translator
        } catch (ignored: Exception)
        {
            translator = LocalUUIDCacheTranslator
        }
    }

}