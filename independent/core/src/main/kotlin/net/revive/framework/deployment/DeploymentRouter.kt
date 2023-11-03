package net.revive.framework.deployment

import express.ExpressRouter

object DeploymentRouter : ExpressRouter() {

    init {
        get("/deployment/call/:template") { req, res ->
            val template = DeploymentService.templates[req.getParam("template")]

            if (template != null) {
                DeploymentService.deploy(template)
            }
        }
    }
}