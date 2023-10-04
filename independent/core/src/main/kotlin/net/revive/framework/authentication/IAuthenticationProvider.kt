package net.revive.framework.authentication

interface IAuthenticationProvider {

    fun tryAuthenticate(username: String, password: String)
    fun hasLogin(username: String) : Boolean
}