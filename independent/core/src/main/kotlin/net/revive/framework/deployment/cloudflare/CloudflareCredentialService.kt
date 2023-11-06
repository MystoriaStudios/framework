package net.revive.framework.deployment.cloudflare

import eu.roboflax.cloudflare.CloudflareAccess

object CloudflareCredentialService {

    lateinit var cloudflareAccess: CloudflareAccess
    lateinit var requestController: CloudflareRequestController

    fun withEmail(email: String, apiKey: String) {
        cloudflareAccess = CloudflareAccess(
            apiKey,
            email
        )
        requestController = CloudflareRequestController(cloudflareAccess)
    }

    fun withToken(token: String) {
        cloudflareAccess = CloudflareAccess(token)
        requestController = CloudflareRequestController(cloudflareAccess)
    }
}