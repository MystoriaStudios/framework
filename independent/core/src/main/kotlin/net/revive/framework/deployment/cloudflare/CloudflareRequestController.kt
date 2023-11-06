package net.revive.framework.deployment.cloudflare

import eu.roboflax.cloudflare.CloudflareAccess
import eu.roboflax.cloudflare.CloudflareCallback
import eu.roboflax.cloudflare.CloudflareRequest
import eu.roboflax.cloudflare.CloudflareResponse
import eu.roboflax.cloudflare.constants.Category
import eu.roboflax.cloudflare.http.HttpMethod
import eu.roboflax.cloudflare.objects.dns.DNSRecord
import eu.roboflax.cloudflare.objects.zone.Zone
import net.revive.framework.Framework
import net.revive.framework.deployment.DeploymentService

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

    fun listDNSRecords(zoneName: String) : List<DNSRecord> {
        val target = listZones().firstOrNull {
            it.name.equals(zoneName, true)
        } ?: return emptyList()

        val request = CloudflareRequest(
            HttpMethod.GET,
            Category.LIST_DNS_RECORDS.additionalPath.replace("{id-1}", target.id),
            cfAccess
        ).asObjectList(DNSRecord::class.java)

        return if (!request.isSuccessful) {
            emptyList()
        } else {
            request.`object`
        }
    }

    fun getDNSRecord(zoneName: String, recordName: String, type: String) : DNSRecord? {
        val list = listDNSRecords(zoneName)

        if (list.isNotEmpty()) {
            val targetedRecord = list.firstOrNull {
                it.name.equals(recordName, ignoreCase = true) && it.type.equals(type, ignoreCase = true)
            } ?: return null

            return targetedRecord
        } else {
            return null
        }
    }

    fun createDNSRecord(zoneName: String, record: DNSRecord) : DNSRecord? {
        val target = listZones().firstOrNull {
            it.name.equals(zoneName, true)
        } ?: return null

        val request = CloudflareRequest(
            HttpMethod.POST,
            Category.CREATE_DNS_RECORD.additionalPath.replace("{id-1}", target.id),
            cfAccess
        ).body(
            record.toString()
        ).asObject(DNSRecord::class.java)

        return if (request.isSuccessful) {
            request.`object`
        } else {
            null
        }
    }
}