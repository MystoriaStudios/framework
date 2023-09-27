package net.mystoria.framework.nms.annotation

import net.mystoria.framework.nms.NMSVersion


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class NMSHandler(val version: NMSVersion)