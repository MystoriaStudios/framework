package net.revive.framework.utils

/**
 * Utility object containing methods related to font manipulations.
 */
object FontUtils {

    // Mapping of lowercase characters to their small caps equivalent.
    private val smallCapsMap = mapOf(
        'a' to 'ᴀ', 'b' to 'ʙ', 'c' to 'ᴄ', 'd' to 'ᴅ', 'e' to 'ᴇ',
        'f' to 'ғ', 'g' to 'ɢ', 'h' to 'ʜ', 'i' to 'ɪ', 'j' to 'ᴊ',
        'k' to 'ᴋ', 'l' to 'ʟ', 'm' to 'ᴍ', 'n' to 'ɴ', 'o' to 'ᴏ',
        'p' to 'ᴘ', 'q' to 'ǫ', 'r' to 'ʀ', 's' to 's', 't' to 'ᴛ',
        'u' to 'ᴜ', 'v' to 'ᴠ', 'w' to 'ᴡ', 'x' to 'x', 'y' to 'ʏ', 'z' to 'ᴢ'
    )

    /**
     * Converts a string to its "small caps" representation.
     * Any character not represented in the small caps map will remain unchanged.
     *
     * @param s The input string to be converted.
     * @return The "small caps" representation of the input string.
     */
    fun toSmallCaps(s: String): String {
        val smallCapsStringBuilder = StringBuilder()
        for (char in s) {
            val smallCapsChar = smallCapsMap[char.lowercaseChar()] ?: char
            smallCapsStringBuilder.append(smallCapsChar)
        }
        return smallCapsStringBuilder.toString()
    }
}

/**
 * Extension function for String that applies the "small caps" transformation to the caller string.
 *
 * @return The "small caps" representation of the caller string.
 */
fun String.applySmallCaps(): String {
    return FontUtils.toSmallCaps(this)
}