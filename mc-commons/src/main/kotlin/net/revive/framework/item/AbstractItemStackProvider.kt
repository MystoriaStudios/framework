package net.revive.framework.item

interface AbstractItemStackProvider<I : FrameworkItemStack> {
    fun getEmpty(): I
}