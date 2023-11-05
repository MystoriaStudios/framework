package net.revive.framework.deployment.cloudflare

import eu.roboflax.cloudflare.CloudflareAccess
import eu.roboflax.cloudflare.CloudflareRequest
import eu.roboflax.cloudflare.constants.Category
import eu.roboflax.cloudflare.objects.zone.Zone

class CloudflareRequestController(
    private val cfAccess: CloudflareAccess
) {
    fun listZones() : List<Zone> {
        val request = CloudflareRequest(
            Category.LIST_ZONES,
            cfAccess
        ).asObjectList(
            Zone::class.java
        )

        return if (request.isSuccessful) {
            request.`object`
        } else {
            emptyList()
        }
    }
}