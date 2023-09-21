package net.mystoria.framework.updater.authentication

import net.mystoria.framework.flavor.service.Configure
import net.mystoria.framework.flavor.service.Service
import net.mystoria.framework.serializer.IAbstractTypeSerializable
import net.mystoria.framework.updater.UpdaterService

@Service(priority = 50)
object UpdaterAuthenticationService
{
    var authentication: JungleConnectionAuthenticationWrapper = JungleConnectionAuthenticationWrapper()

    @Configure
    fun configure() {
        authentication = UpdaterService.authentication
    }

    fun getWrapper() = authentication

    class JungleConnectionAuthenticationWrapper(
        val apiKey: String = "AKCp8pQvVzYyzygdLTieUB4uMMWt5nGh8pFcd5qAfJP4xEoeWEwpjHSDimSehAQNN759yx4Mx"
    ) : IAbstractTypeSerializable {
        override fun getAbstractType() = JungleConnectionAuthenticationWrapper::class.java
    }
}
