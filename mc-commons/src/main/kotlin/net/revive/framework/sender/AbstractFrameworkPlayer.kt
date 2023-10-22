package net.revive.framework.sender


typealias FrameworkPlayer = AbstractFrameworkPlayer<*>

abstract class AbstractFrameworkPlayer<P>(val player: P) : FrameworkSender<P> {
}