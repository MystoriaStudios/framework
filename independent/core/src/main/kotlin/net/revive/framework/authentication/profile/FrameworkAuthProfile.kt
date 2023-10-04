package net.revive.framework.authentication.profile

import net.revive.framework.storage.storable.IStorable
import java.util.*

class FrameworkAuthProfile(
    override val identifier: UUID,
    val username: String,
    val password: String
) : IStorable {
}