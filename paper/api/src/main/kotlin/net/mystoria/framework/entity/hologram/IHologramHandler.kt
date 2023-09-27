package net.mystoria.framework.entity.hologram

import net.mystoria.framework.entity.hologram.personalized.AbstractPersonalizedNMSHologramEntity
import net.mystoria.framework.entity.hologram.updating.AbstractUpdatingNMSHologram

interface IHologramHandler {

    fun getHolograms(): List<AbstractNMSHologram>

    fun getUpdatingHolograms(): List<AbstractUpdatingNMSHologram>

    fun getPersonalizedHolograms(): List<AbstractPersonalizedNMSHologramEntity>
}