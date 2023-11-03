package net.revive.framework.updater.authentication

import net.revive.framework.flavor.service.Configure
import net.revive.framework.flavor.service.Service
import net.revive.framework.serializer.IAbstractTypeSerializable
import net.revive.framework.updater.UpdaterService

@Service(priority = 50)
object UpdaterAuthenticationService {
    var authentication: IConnectionAuthenticationWrapper = NexusConnectionAuthenticationWrapper()

    @Configure
    fun configure() {
        authentication = UpdaterService.authentication
    }

    fun getWrapper() = authentication

    interface IConnectionAuthenticationWrapper

    class JFrogConnectionAuthenticationWrapper(
        val apiKey: String = "AKCp8pQvVzYyzygdLTieUB4uMMWt5nGh8pFcd5qAfJP4xEoeWEwpjHSDimSehAQNN759yx4Mx"
    ) : IConnectionAuthenticationWrapper, IAbstractTypeSerializable {
        override fun getAbstractType() = JFrogConnectionAuthenticationWrapper::class.java
    }


    class NexusConnectionAuthenticationWrapper(
        val username: String = "admin",
        val password: String = "3493e83c-6e3b-4362-b71a-6618087cb26d"
    ) : IConnectionAuthenticationWrapper, IAbstractTypeSerializable {
        override fun getAbstractType() = NexusConnectionAuthenticationWrapper::class.java
    }
}
