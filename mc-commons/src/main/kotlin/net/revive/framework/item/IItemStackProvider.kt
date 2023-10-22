package net.revive.framework.item

interface IItemStackProvider<I : FrameworkItemStack> {
    fun getEmpty(): I
}