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
                    println(it.serializer.serialize(response))

                    res.send(it.serializer.serialize(response))
                }
            } else {
                res.send("Template not found")
            }
        }
    }
}