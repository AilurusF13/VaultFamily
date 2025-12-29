package fr.ailurus.vaultfamily.domain.auth

interface AuthManager {

    /**
     * Vérifie si la db est initalisése ou non
     */
    fun isVaultInitalized(): Boolean

    fun deleteDb()

    /**
     * Initalise la db
     */
    suspend fun setupVault(passphrase: ByteArray): Result<Unit>

    /**
     * essaie de unlock la db et l'ouvre
     */
    suspend fun loginVault(passphrase: ByteArray): Result<Unit>

    /**
     *
     */
    fun accessVault()
}