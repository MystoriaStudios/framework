package net.revive.framework.security.impl

import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Factory
import net.revive.framework.security.IHashingAlgorithm

object Argon2HashingAlgorithm : IHashingAlgorithm {

    private val argon2: Argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)

    /**
     * Generates a hash for the given input.
     */
    override fun hash(input: CharArray): String {
        // You can adjust parameters like iterations, memory, and parallelism for your needs
        return argon2.hash(2, 65536, 1, input)
    }

    /**
     * Verifies that the given input matches the provided hash.
     */
    override fun verify(hash: String, input: CharArray): Boolean {
        return argon2.verify(hash, input)
    }

    /**
     * (Optional) Generates a salt.
     */
    override fun generateSalt(): ByteArray {
        // This library handles salt generation internally when hashing,
        // so you might not need to expose this externally.
        throw UnsupportedOperationException("This library handles salt generation internally.")
    }

    /**
     * Clears sensitive data from memory.
     */
    override fun clearSensitiveData(data: CharArray) {
        argon2.wipeArray(data)
    }

    /**
     * Returns metadata about the algorithm.
     */
    override fun getAlgorithmInfo(): String {
        return "Argon2id hashing algorithm using argon2-java library."
    }
}