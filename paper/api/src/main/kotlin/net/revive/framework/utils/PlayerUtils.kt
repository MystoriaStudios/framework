package net.revive.framework.utils

import net.revive.framework.sender.FrameworkPlayer
import net.revive.framework.sender.PaperFrameworkPlayer
import org.bukkit.entity.Player

val Player.pvc: FrameworkPlayer
    get() = PaperFrameworkPlayer(this)