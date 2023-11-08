package net.revive.framework.deployment

import express.ExpressRouter
import net.revive.framework.Framework
import org.apache.http.client.entity.UrlEncodedFormEntity
import java.net.URLDecoder
import java.net.URLEncoder
import javax.sound.sampled.AudioFormat.Encoding

object DeploymentRouter : ExpressRouter() {

    init {
        get("/deployment/call/:template") { req, res ->
            val template = DeploymentService.templates[req.getParam("template")]

            if (template != null) {
                Framework.use {
                    println("deploying template ${it.serializer.serialize(template)}")
                    val response = DeploymentService.deploy(template)
                    println("done.")
                    println(response?.let { it1 -> it.serializer.serialize(it1) })

                    res.send(response?.let { it1 -> it.serializer.serialize(it1) })
                }
            } else {
                res.send("Template not found")
            }
        }

        get("/deployment/template/:template") { req, res ->
            val template = DeploymentService.templates[req.getParam("template")]

            if (template != null) {
                res.send(Framework.useWithReturn {
                    it.serializer.serialize(template)
                })
            } else {
                res.send("Template not found")
            }
        }

        post("/deployment/template/:template") { req, res ->
            val template = DeploymentService.templates[req.getParam("template")]

            if (template != null) {
                template.serverExecutableOrigin = URLDecoder.decode(req.getFormQuery("serverExecutableOrigin"), "UTF-8")
                template.startupCommand = URLDecoder.decode(req.getFormQuery("startupCommand"), "UTF-8")
                template.dockerImage = URLDecoder.decode(req.getFormQuery("dockerImage"), "UTF-8")

                res.send(Framework.useWithReturn {
                    it.serializer.serialize(template)
                })
            } else {
                res.send("Template not found")
            }
        }

        get("/deployment/templates") { req, res ->
            res.send(Framework.useWithReturn {
                it.serializer.serialize(DeploymentService.templates.values)
            })
        }

        get("/deployment/containers") { req, res ->
            res.send(Framework.useWithReturn {
                it.serializer.serialize(DeploymentService.containers.values)
            })
        }
    }
}