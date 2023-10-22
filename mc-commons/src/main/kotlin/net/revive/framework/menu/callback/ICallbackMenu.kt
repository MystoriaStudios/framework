package net.revive.framework.menu.callback

import net.revive.framework.menu.IMenu

interface ICallbackMenu<T> : IMenu {
    var onCallback: (T) -> Unit
}