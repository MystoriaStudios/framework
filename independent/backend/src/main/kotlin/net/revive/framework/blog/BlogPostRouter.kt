package net.revive.framework.blog

import express.ExpressRouter
import net.revive.framework.Framework

class BlogPostRouter : ExpressRouter() {

    val posts = mutableListOf<Post>()

    init {
        get("/api/blog/posts") { req, res ->
            println("paosts")
            res.send(Framework.useWithReturn {
                it.serializer.serialize(posts)
            })
        }
        get("/api/blog/post/:key") { req, res ->
            println("posts")
            res.send(Framework.useWithReturn {
                it.serializer.serialize(posts.filter {
                    it.key.equals(req.getParam("key"), true)
                })
            })
        }
        post("/api/blog/post") { req, res ->
            println("posts add")
            posts.add(
                Post(
                    req.getFormQuery("key"),
                    req.getFormQuery("title"),
                    req.getFormQuery("content"),
                    req.getFormQuery("author")
                ))
            res.send(Framework.useWithReturn {
                it.serializer.serialize(posts.filter {
                    it.key == req.getFormQuery("key")
                })
            })
        }
    }
}