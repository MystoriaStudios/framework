package net.revive.framework.constants

import net.revive.framework.Framework
import java.io.File

object Deployment {
    lateinit var info: Info

    class Info(
        val serverId: String,
        val serverGroups: List<String>
    ) {
        fun save(path: File) {
            path.writeText(Framework.useWithReturn {
                it.serializer.serialize(this)
            })
        }
    }

    fun scanForInformation() {
        val file = File("deployment.json")

        if (file.exists()) {
            info = Framework.useWithReturn {
                it.serializer.deserialize(Info::class, file.readText())
            }
        }
    }

    object Security {
        const val API_BASE_URL = "https://api.nopox.xyz/"
        const val API_KEY = ""
    }
}