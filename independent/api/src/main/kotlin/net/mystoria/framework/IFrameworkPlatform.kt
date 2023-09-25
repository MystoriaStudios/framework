package net.mystoria.framework

import java.util.UUID

interface IFrameworkPlatform {

    val uniqueId: UUID
    val id: String
    val group: String

}