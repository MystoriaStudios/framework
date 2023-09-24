package net.mystoria.framework.utils

object Strings {

    fun xor(input: Boolean, strings: Pair<String, String>) = if (input) strings.first else strings.second
    fun pluralize(input: Int, label: String) : String {
        if (input != 1) return "${label}s"
        return label
    }
}
