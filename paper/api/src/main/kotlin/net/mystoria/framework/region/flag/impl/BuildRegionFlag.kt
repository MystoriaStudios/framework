package net.mystoria.framework.region.flag.impl

import com.comphenix.protocol.PacketType
import me.lucko.helper.Events
import me.lucko.helper.protocol.Protocol
import net.mystoria.framework.annotation.region.RegionFlag
import net.mystoria.framework.flavor.service.Configure
import net.mystoria.framework.region.IRegion
import net.mystoria.framework.region.flag.IRegionFlag
import org.bukkit.event.block.BlockPlaceEvent

@RegionFlag
object BuildRegionFlag : IRegionFlag {

    @Configure
    fun configure() {
        Protocol.subscribe(PacketType.Play.Client.BLOCK_PLACE).handler { event ->
            val packet = event.packet

            if (packet.posit)
        }
        Events.subscribe(BlockPlaceEvent::class.java).handler {
            val block = it.block.location
            if (appliesOn(TODO("GET THE REGION FROM THE PLACED BLOCK"))) {
                it.isCancelled = true
            }
        }
    }

    override fun appliesOn(region: IRegion): Boolean {
        return super.appliesOn(region)
    }
}