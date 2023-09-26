package net.mystoria.framework.authentication

import express.ExpressRouter
import express.http.Cookie
import express.http.CookieFactory
import net.mystoria.framework.Framework
import net.mystoria.framework.authentication.exception.AuthenticationFailedException

object AuthenticationRouter : ExpressRouter() {

    init {
        post("/auth/login") { req, res ->
            val data = mutableMapOf<String, Any>()
            val username = req.getFormQuery("username")
            val password = req.getFormQuery("password")

            data["success"] = false

            try {
                AuthenticationService.authenticationProviders.forEach {
                    if (it.hasLogin(username)) it.tryAuthenticate(username, password)
                }
            } catch (exception: AuthenticationFailedException) {
                exception.printStackTrace()
                data["message"] = exception.message.toString()
                res.send(Framework.useWithReturn {
                    it.serializer.serialize(data)
                })
                return@post
            }

            res.setCookie(Cookie(
                "",
                ""
            ))

        }
    }
}