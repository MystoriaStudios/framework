package net.revive.framework.node

import express.ExpressRouter
import net.revive.framework.Framework
import net.revive.framework.storage.type.FrameworkStorageType

class BackendNodeRouter : ExpressRouter() {
    init {
        get("/api/nodes") { req, res ->
            println("nodes")
            BackendNodeService.controller.loadAll(FrameworkStorageType.MONGO).thenAccept { result ->
                res.send(Framework.useWithReturn {
                    it.serializer.serialize(result.values)
                })
            }
        }
        get("/api/nodes/add") { req, res ->
            println("nodes add")
            BackendNodeService.controller.save(Node("test", "test.com", Node.State.ONLINE))
            println("saved test node")
            BackendNodeService.controller.loadAll(FrameworkStorageType.MONGO).thenAccept { result ->
            println("loaded")
            res.send(Framework.useWithReturn {
                    it.serializer.serialize(result.values)
                })
            }
        }
    }
}