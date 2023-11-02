package net.revive.framework.node

import express.ExpressRouter
import net.revive.framework.Framework

class BackendNodeRouter : ExpressRouter() {

    val nodes = mutableListOf<Node>()

    init {
        get("/api/nodes/:search") { req, res ->
            res.send(Framework.useWithReturn {
                it.serializer.serialize(nodes.filter {
                    it.organization == req.getParam("search") || it.identifier.toString() == req.getParam("search")
                })
            })
        }

        get("/api/nodes/:organization/:node/pods") { req, res ->
            val node = nodes.filter {
                it.organization == req.getParam("organization")
                &&
                it.identifier.toString() == req.getParam("node")
            }.firstOrNull() ?: return@get

            res.send(Framework.useWithReturn {
                it.serializer.serialize(node.pods)
            })
        }

        post("/api/nodes/:organization/add") { req, res ->
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