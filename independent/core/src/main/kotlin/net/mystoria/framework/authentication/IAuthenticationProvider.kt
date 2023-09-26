package net.mystoria.framework.authentication

interface IAuthenticationProvider {

    fun tryAuthenticate(username: String, password: String)
    fun hasLogin(username: String) : Boolean
}