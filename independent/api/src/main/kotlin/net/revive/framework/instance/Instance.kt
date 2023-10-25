package net.revive.framework.instance

import net.revive.framework.IFrameworkPlatform
import net.revive.framework.metadata.IMetaDataHolder
import net.revive.framework.storage.storable.IStorable
import java.util.*

class Instance(
    val id: String,
    val groups: List<String>,
    override val identifier: UUID = UUID.nameUUIDFromBytes(id.toByteArray()),
    override val metaData: MutableMap<String, String> = mutableMapOf()
) : IMetaDataHolder, IStorable {
    fun provideData(platform: IFrameworkPlatform) {
        platform.updateInstance(this)
    }

    companion object {
        fun create(platform: IFrameworkPlatform): Instance {
            return Instance(
                platform.id,
                platform.groups,
            ).also {
                it.provideData(platform)
            }
        }
    }
}