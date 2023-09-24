package net.mystoria.framework.cache.impl

import net.mystoria.framework.cache.IUUIDCacheTranslator
import net.mystoria.framework.cache.UUIDCacheHelper
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ConcurrentHashMap

abstract class ILocalUUIDCacheTranslator : IUUIDCacheTranslator