package fr.ailurus.vaultfamily.domain.auth

interface AuthManager {

    /**
     * Vérifie si la db est initalisése ou non
     */
    fun isVaultInitalized(): Boolean

    /**
     * Initalise la db
     */
    fun setupVault(passphrase: ByteArray): Result<Unit>

    /**
     * Unlock la db et l'ouvre
     */
    fun loginVault(passphrase: ByteArray): Result<Unit>
}