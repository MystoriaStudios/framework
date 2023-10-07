package net.revive.framework.nms.disguise

import java.util.*

data class DisguiseInfo(
    val uuid: UUID,
    val username: String,
    val skinInfo: String,
    val skinSignature: String
)