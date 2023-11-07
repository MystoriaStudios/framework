package net.revive.framework.deployment.cloudflare

import com.google.gson.JsonObject
import eu.roboflax.cloudflare.CloudflareAccess
import eu.roboflax.cloudflare.CloudflareRequest
import eu.roboflax.cloudflare.constants.Category
import eu.roboflax.cloudflare.http.HttpMethod
import eu.roboflax.cloudflare.objects.dns.DNSRecord
import eu.roboflax.cloudflare.objects.zone.Zone
import net.revive.framework.Framework
import net.revive.framework.FrameworkApp

class CloudflareRequestController(
    private val cfAccess: CloudflareAccess
) {
    private val backingZoneTracker: MutableMap<String, Zone> = mutableMapOf()

    init {
        propegateZonesIntoTracker()
    }

    fun propegateZonesIntoTracker() {
        listZones().forEach {
            backingZoneTracker[it.name] = it
        }
    }

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

    fun createZone(accountId: String, name: String, type: String) : Zone? {
        val requestObject = JsonObject().apply {
            this.addProperty("name", name)
            this.addProperty("type", type)

            val accountObject = JsonObject().also {
                it.addProperty("id", accountId)
            }

            this.addProperty("account", Framework.useWithReturn {
                it.serializer.serialize(accountObject)
            })
        }

        val request = CloudflareRequest(
            Category.CREATE_ZONE,
            cfAccess
        ).body(
            Framework.useWithReturn {
                it.serializer.serialize(requestObject)
            }
        ).asObject(
            Zone::class.java
        )

        return if (request.isSuccessful) {
            request.`object`
        } else {
            null
        }
    }

    fun getZoneByName(zoneName: String) : Zone? {
        return backingZoneTracker[zoneName] ?: listZones().firstOrNull {
            it.name.equals(zoneName, true)
        }
    }

    fun listDNSRecords(zoneName: String) : List<DNSRecord> {
        val target = getZoneByName(zoneName) ?: return emptyList()

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
        val target = getZoneByName(zoneName) ?: return null

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