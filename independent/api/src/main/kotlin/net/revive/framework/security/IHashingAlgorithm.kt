package net.revive.framework.security

interface IHashingAlgorithm {

    /**
     * Generates a hash for the given input.
     * @param input - The input data to be hashed.
     * @return The generated hash.
     */
    fun hash(input: CharArray): String

    /**
     * Verifies that the given input matches the provided hash.
     * @param hash - The hash to verify against.
     * @param input - The input data to verify.
     * @return true if the input matches the hash, false otherwise.
     */
    fun verify(hash: String, input: CharArray): Boolean

    /**
     * (Optional) Generates a salt. Some algorithms or implementations might handle salting internally.
     * For those that don't, this method can be used to generate a salt.
     * @return The generated salt.
     */
    fun generateSalt(): ByteArray

    /**
     * Clears sensitive data from memory, especially useful for password handling.
     * @param data - The sensitive data to be cleared.
     */
    fun clearSensitiveData(data: CharArray)

    /**
     * Returns metadata about the algorithm, such as its name or any other relevant info.
     * @return Information about the hashing algorithm.
     */
    fun getAlgorithmInfo(): String
}