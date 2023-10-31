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
        post("/api/nodes/:organization/add") { req, res ->
            println("nodes add")
            val node = Framework.useWithReturn {
                it.serializer.deserialize(Node::class, req.body.bufferedReader().readText())
            }

            nodes.removeIf {
                it.identifier == node.identifier
            }

            nodes.add(node)
            res.send(Framework.useWithReturn {
                it.serializer.serialize(nodes.filter {
                    it.organization == req.getParam("organization")
                })
            })
        }
    }
}