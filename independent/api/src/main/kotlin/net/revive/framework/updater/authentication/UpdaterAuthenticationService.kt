package net.revive.framework.updater.authentication

import net.revive.framework.flavor.service.Configure
import net.revive.framework.flavor.service.Service
import net.revive.framework.serializer.IAbstractTypeSerializable
import net.revive.framework.updater.UpdaterService

@Service(priority = 50)
object UpdaterAuthenticationService
{
    var authentication: MystoriaConnectionAuthenticationWrapper = MystoriaConnectionAuthenticationWrapper()

    @Configure
    fun configure() {
        authentication = UpdaterService.authentication
    }

    fun getWrapper() = authentication

    class MystoriaConnectionAuthenticationWrapper(
        val apiKey: String = "AKCp8pQvVzYyzygdLTieUB4uMMWt5nGh8pFcd5qAfJP4xEoeWEwpjHSDimSehAQNN759yx4Mx"
    ) : IAbstractTypeSerializable {
        override fun getAbstractType() = MystoriaConnectionAuthenticationWrapper::class.java
    }
}
