package fr.ailurus.vaultfamily.domain.auth

interface AuthManager {
    /** * Vérifie si c'est la première utilisation (la DB n'existe pas encore)
     */
    fun isVaultInitialized(): Boolean

    /**
     * Initialise le coffre avec un nouveau mot de passe
     * @return Result avec succès ou erreur (ex: mot de passe trop court)
     */
    suspend fun setupVault(passphrase: ByteArray): Result<Unit>

    /**
     * Tente d'ouvrir le coffre existant
     * @return Result avec succès ou erreur (ex: mauvais mot de passe)
     */
    suspend fun unlockVault(passphrase: ByteArray): Result<Unit>
}