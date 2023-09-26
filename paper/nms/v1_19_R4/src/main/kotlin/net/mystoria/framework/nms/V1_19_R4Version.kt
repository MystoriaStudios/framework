package net.mystoria.framework.nms

import net.mystoria.framework.nms.menu.INMSMenuHandler
import net.mystoria.framework.nms.menu.V1_19_R4MenuHandler

class V1_19_R4Version : INMSVersion {
    override val menuHandler: INMSMenuHandler = V1_19_R4MenuHandler()
}