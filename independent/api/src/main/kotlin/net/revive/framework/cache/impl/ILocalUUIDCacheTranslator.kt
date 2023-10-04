package net.revive.framework.cache.impl

import net.revive.framework.cache.IUUIDCacheTranslator
import net.revive.framework.cache.UUIDCacheHelper
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ConcurrentHashMap

abstract class ILocalUUIDCacheTranslator : IUUIDCacheTranslator