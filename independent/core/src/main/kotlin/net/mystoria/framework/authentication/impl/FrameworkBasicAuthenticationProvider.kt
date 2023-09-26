package net.mystoria.framework.authentication.impl

import com.mongodb.client.model.Filters.eq
import net.mystoria.framework.authentication.IAuthenticationProvider
import net.mystoria.framework.authentication.exception.AuthenticationFailedException
import net.mystoria.framework.authentication.profile.FrameworkAuthProfile
import net.mystoria.framework.controller.FrameworkObjectControllerCache
import net.mystoria.framework.storage.impl.MongoFrameworkStorageLayer
import net.mystoria.framework.storage.type.FrameworkStorageType

object FrameworkBasicAuthenticationProvider : IAuthenticationProvider {

    val controller = FrameworkObjectControllerCache.create<FrameworkAuthProfile>()

    override fun tryAuthenticate(username: String, password: String) {
        val profile = controller.useLayerWithReturn<MongoFrameworkStorageLayer<FrameworkAuthProfile>, FrameworkAuthProfile?>(FrameworkStorageType.MONGO) {
            this.loadWithFilterSync(eq(
                "username", username
            ))
        } ?: throw AuthenticationFailedException()
        if (profile.password != password) throw AuthenticationFailedException()
    }

    override fun hasLogin(username: String): Boolean {
        return controller.useLayerWithReturn<MongoFrameworkStorageLayer<FrameworkAuthProfile>, FrameworkAuthProfile?>(FrameworkStorageType.MONGO) {
            this.loadWithFilterSync(eq(
                "username", username
            ))
        } != null
    }
}