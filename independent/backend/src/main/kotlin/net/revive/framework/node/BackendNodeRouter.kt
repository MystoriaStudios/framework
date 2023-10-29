package net.revive.framework.node

import express.ExpressRouter
import net.revive.framework.Framework
import net.revive.framework.storage.type.FrameworkStorageType

class BackendNodeRouter : ExpressRouter() {

    val nodes = mutableMapOf<String, Node>()

    init {
        get("/api/nodes") { req, res ->
            println("nodes")
            res.send(Framework.useWithReturn {
                it.serializer.serialize(nodes)
            })
        }
        get("/api/nodes/add") { req, res ->
            println("nodes add")
            nodes["test"] = Node("test", "test.com", Node.State.ONLINE)
            res.send(Framework.useWithReturn {
                it.serializer.serialize(nodes)
            })
        }
    }
}