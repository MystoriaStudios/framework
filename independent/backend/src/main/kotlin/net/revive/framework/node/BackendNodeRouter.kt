package net.revive.framework.node

import express.ExpressRouter
import net.revive.framework.Framework
import net.revive.framework.storage.type.FrameworkStorageType

class BackendNodeRouter : ExpressRouter() {

    val nodes = mutableListOf<Node>()

    init {
        get("/api/nodes/:organization") { req, res ->
            println("nodes")
            res.send(Framework.useWithReturn {
                it.serializer.serialize(nodes.filter {
                    it.organization == req.getParam("organization")
                })
            })
        }
        get("/api/nodes/:organization/add") { req, res ->
            println("nodes add")
            nodes.add(Node("test", "test.com", req.getParam("organization"), Node.State.ONLINE))
            res.send(Framework.useWithReturn {
                it.serializer.serialize(nodes.filter {
                    it.organization == req.getParam("organization")
                })
            })
        }
    }
}