package net.revive.framework.allocation

import express.ExpressRouter
import net.revive.framework.Framework
import net.revive.framework.FrameworkApp

object AllocationRouter : ExpressRouter() {

    init {
        get("/allocations") { req, res ->
            res.send(Framework.useWithReturn {
                it.serializer.serialize(AllocationService.config.allocations)
            })
        }

        post("/allocations") { req, res ->
            val allocation = Allocation(req.getFormQuery("hostname"), req.getFormQuery("port").toIntOrNull() ?: 0)
            if (AllocationService.config.allocations.any {
                it.port == allocation.port
            })  {
                res.send(Framework.useWithReturn {
                    it.serializer.serialize("error" to "You already have a port defined with that allocation.")
                })
                return@post
            }

            AllocationService.config.allocations.add(allocation)

            res.send(Framework.useWithReturn {
                it.serializer.serialize(allocation)
            })
        }
    }
}