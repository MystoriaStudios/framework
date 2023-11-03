package net.revive.framework.marketplace

import net.revive.framework.storage.storable.IStorable
import java.util.*

// for now all resources will be posted by the Framework Dev Team
data class MarketplaceShowcaseData(
    override val identifier: UUID,
    var displayName: String,
    var purchases: Int,
    var price: Double, // Price in USD
) : IStorable {
}