package net.revive.framework.nms

/**
 * Enumerates the supported NMS (net.minecraft.server) versions.
 *
 * This is used to identify and manage different versions of the Minecraft server internals
 * for the purpose of providing version-specific functionality and compatibility.
 */
enum class NMSVersion {
    /** Represents the 1.20.1 release. */
    V1_20_R1,

    /** Represents the 1.19.4 release. */
    V1_19_R4,

    /** Represents the 1.18.2 release. */
    V1_18_R2,

    /** Represents the 1.17.1 release. */
    V1_17_R1
}
