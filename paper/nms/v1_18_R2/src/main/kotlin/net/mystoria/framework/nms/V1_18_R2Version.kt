package net.mystoria.framework.nms

import net.mystoria.framework.nms.menu.INMSMenuHandler
import net.mystoria.framework.nms.menu.V1_18_R2MenuHandler

class V1_18_R2Version : INMSVersion {
    override val menuHandler: INMSMenuHandler = V1_18_R2MenuHandler()
}