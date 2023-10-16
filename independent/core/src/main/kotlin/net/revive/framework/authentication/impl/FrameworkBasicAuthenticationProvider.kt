package net.revive.framework.authentication.impl

import com.mongodb.client.model.Filters.eq
import net.revive.framework.authentication.IAuthenticationProvider
import net.revive.framework.authentication.exception.AuthenticationFailedException
import net.revive.framework.authentication.profile.FrameworkAuthProfile
import net.revive.framework.controller.FrameworkObjectControllerCache
import net.revive.framework.storage.impl.MongoFrameworkStorageLayer
import net.revive.framework.storage.type.FrameworkStorageType

object FrameworkBasicAuthenticationProvider : IAuthenticationProvider {

    val controller = FrameworkObjectControllerCache.create<FrameworkAuthProfile>()

    override fun tryAuthenticate(username: String, password: String) {
        val profile =
            controller.useLayerWithReturn<MongoFrameworkStorageLayer<FrameworkAuthProfile>, FrameworkAuthProfile?>(
                FrameworkStorageType.MONGO
            ) {
                this.loadWithFilterSync(
                    eq(
                        "username", username
                    )
                )
            } ?: throw AuthenticationFailedException()
        if (profile.password != password) throw AuthenticationFailedException()
    }

    override fun hasLogin(username: String): Boolean {
        return controller.useLayerWithReturn<MongoFrameworkStorageLayer<FrameworkAuthProfile>, FrameworkAuthProfile?>(
            FrameworkStorageType.MONGO
        ) {
            this.loadWithFilterSync(
                eq(
                    "username", username
                )
            )
        } != null
    }
}