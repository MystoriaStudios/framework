package net.revive.framework.node

import express.ExpressRouter
import net.revive.framework.Framework

class BackendNodeRouter : ExpressRouter() {

    val nodes = mutableListOf<Node>()

    init {
        get("/api/nodes/:search") { req, res ->
            println("nodes")
            res.send(Framework.useWithReturn {
                it.serializer.serialize(nodes.filter {
                    it.organization == req.getParam("search") || it.identifier.toString() == req.getParam("search")
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