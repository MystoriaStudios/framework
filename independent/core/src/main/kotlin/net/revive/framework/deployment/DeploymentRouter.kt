package net.revive.framework.deployment

import express.ExpressRouter
import net.revive.framework.Framework

object DeploymentRouter : ExpressRouter() {

    init {
        get("/deployment/call/:template") { req, res ->
            val template = DeploymentService.templates[req.getParam("template")]

            if (template != null) {
                Framework.use {
                    println("deploying teemplate ${it.serializer.serialize(template)}")
                    val response = DeploymentService.deploy(template)
                    println("done.")
                    println(response?.let { it1 -> it.serializer.serialize(it1) })

                    res.send(response?.let { it1 -> it.serializer.serialize(it1) })
                }
            } else {
                res.send("Template not found")
            }
        }
    }
}