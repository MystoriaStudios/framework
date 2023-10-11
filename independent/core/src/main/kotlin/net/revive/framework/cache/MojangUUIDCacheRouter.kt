package net.revive.framework.cache

import express.ExpressRouter
import express.utils.Status
import net.revive.framework.cache.impl.distribution.DistributedRedisUUIDCache

object MojangUUIDCacheRouter : ExpressRouter() {

    init {
        post("/uuid-cache/uuid/:uuid") { req, res ->
            val uuid = req.getParam("uuid")
                ?: return@post res.sendStatus(Status._400)


            res.send(
                net.revive.framework.Framework.useWithReturn {
                    UUIDCacheHelper.fetchFromMojang(uuid).also {
                        if (it != null) {
                            DistributedRedisUUIDCache.update(it)
                        }
                    }?.let { it1 -> it.serializer.serialize(it1) }
                }
            )
        }

        post("/uuid-cache/username/:username") { req, res ->
            val username = req.getParam("username")
                ?: return@post res.sendStatus(Status._400)



            res.send(
                net.revive.framework.Framework.useWithReturn {
                    UUIDCacheHelper.fetchFromMojang(username).also {
                        if (it != null) {
                            DistributedRedisUUIDCache.update(it)
                        }
                    }?.let { it1 -> it.serializer.serialize(it1) }
                }
            )
        }
    }

}