package net.revive.framework.nms.annotation

import net.revive.framework.nms.NMSVersion


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class NMSHandler(val version: NMSVersion)