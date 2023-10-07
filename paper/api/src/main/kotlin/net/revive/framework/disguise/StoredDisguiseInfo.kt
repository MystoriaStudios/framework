package net.revive.framework.disguise

import net.revive.framework.nms.disguise.DisguiseInfo
import net.revive.framework.storage.storable.IStorable
import java.util.*

class StoredDisguiseInfo(
    override val identifier: UUID,
    username: String,
    skinInfo: String,
    skinSignature: String
) : IStorable, DisguiseInfo(identifier, username, skinInfo, skinSignature)