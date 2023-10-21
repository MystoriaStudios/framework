package net.revive.framework.sender

import java.util.*

typealias FrameworkConsole = AbstractFrameworkConsole<*>

abstract class AbstractFrameworkConsole<C>(val console: C) : FrameworkSender<C> {
    override val uniqueId: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")
}