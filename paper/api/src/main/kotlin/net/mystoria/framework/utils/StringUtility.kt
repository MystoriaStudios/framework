package net.mystoria.framework.utils

object StringUtility {

    fun xor(input: Boolean, strings: Pair<String, String>) = if (input) strings.first else strings.second
    fun grammar(input: Int, label: String) : String {
        if (input != 1) return "${label}s"
        return label
    }
}