package net.revive.framework.deployment

import express.ExpressRouter
import net.revive.framework.Framework

object DeploymentRouter : ExpressRouter() {

    init {
        get("/deployment/call/:template") { req, res ->
            val template = DeploymentService.templates[req.getParam("template")]

            if (template != null) {
                res.send(Framework.useWithReturn {
                    it.serializer.serialize(DeploymentService.deploy(template))
                })
            } else {
                res.send("Template not found")
            }
        }
    }
}