package net.revive.framework.key

import net.kyori.adventure.key.InvalidKeyException
import net.kyori.adventure.key.Key
import net.kyori.examination.ExaminableProperty
import org.intellij.lang.annotations.RegExp
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.stream.Stream


class MinecraftKey(value: String) : Key {
    private val namespace: String
    private val value: String

    init {
        val namespace = "minecraft"
        checkError("namespace", namespace, value, Key.checkNamespace(namespace))
        checkError("value", namespace, value, Key.checkValue(value))
        this.namespace = Objects.requireNonNull(namespace, "namespace")
        this.value = Objects.requireNonNull(value, "value")
    }

    override fun namespace(): String {
        return namespace
    }

    override fun value(): String {
        return value
    }

    override fun asString(): String {
        return asString(namespace, value)
    }

    override fun toString(): String {
        return this.asString()
    }

    override fun examinableProperties(): Stream<out ExaminableProperty> {
        return Stream.of(
            ExaminableProperty.of("namespace", namespace),
            ExaminableProperty.of("value", value)
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Key) return false
        val that = other
        return namespace == that.namespace() && value == that.value()
    }

    override fun hashCode(): Int {
        var result = namespace.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }

    companion object {
        val COMPARATOR: Comparator<in Key> = Comparator.comparing { obj: Key -> obj.value() }
            .thenComparing { obj: Key -> obj.namespace() }

        @RegExp
        val NAMESPACE_PATTERN = "[a-z0-9_\\-.]+"

        @RegExp
        val VALUE_PATTERN = "[a-z0-9_\\-./]+"
        private fun checkError(name: String, namespace: String, value: String, index: OptionalInt) {
            if (index.isPresent) {
                val indexValue = index.asInt
                val character = value[indexValue]
                throw UnsupportedOperationException(
                    String.format(
                        "Non [a-z0-9_.-] character in %s of Key[%s] at index %d ('%s', bytes: %s)",
                        name,
                        asString(namespace, value),
                        indexValue,
                        character, character.toString().toByteArray(StandardCharsets.UTF_8).contentToString()
                    )
                )
            }
        }

        fun allowedInNamespace(character: Char): Boolean {
            return character == '_' || character == '-' || character >= 'a' && character <= 'z' || character >= '0' && character <= '9' || character == '.'
        }

        fun allowedInValue(character: Char): Boolean {
            return character == '_' || character == '-' || character >= 'a' && character <= 'z' || character >= '0' && character <= '9' || character == '.' || character == '/'
        }

        private fun asString(namespace: String, value: String): String {
            return "$namespace:$value"
        }
    }
}

