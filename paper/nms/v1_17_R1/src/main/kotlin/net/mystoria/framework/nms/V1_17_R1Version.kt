package net.mystoria.framework.nms

import net.mystoria.framework.nms.menu.INMSMenuHandler
import net.mystoria.framework.nms.menu.V1_17_R1MenuHandler

class V1_17_R1Version : INMSVersion {
    override val menuHandler: INMSMenuHandler = V1_17_R1MenuHandler()
}