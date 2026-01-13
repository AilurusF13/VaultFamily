package fr.ailurus.vaultfamily.domain.auth

import android.content.Context
import fr.ailurus.vaultfamily.data.repository.VaultRepository

class AuthManagerImpl(
    private val context: Context,
    private val vaultRepository: VaultRepository
): AuthManager {

    override fun isVaultInitalized(): Boolean {
        System.loadLibrary("sqlcipher")
        return context.getDatabasePath("secure-vaultfamily-db").exists()
    }

    override fun deleteDb(){
        context.deleteDatabase("secure-vaultfamily-db")
    }

    // TODO Amélioration : utiliser un salt

    override suspend fun setupVault(passphrase: ByteArray): Result<Unit> {
        return try {
            deleteDb()

            vaultRepository.initializeDb(context, passphrase)
            Result.success(Unit)

        } catch (e: Exception){
            Result.failure(e)
        }

    }

    override suspend fun loginVault(passphrase: ByteArray): Result<Unit> {
        return try {

            vaultRepository.initializeDb(context, passphrase)
            Result.success(Unit)

        } catch (e: Exception){
            Result.failure(e)
        }
    }

    // could be useless, depends on main activity management
    override fun accessVault() {
        println("Access vault")
    }
}