package fr.ailurus.vaultfamily

import fr.ailurus.vaultfamily.data.repository.VaultRepositoryImpl
import fr.ailurus.vaultfamily.data.model.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import fr.ailurus.vaultfamily.ui.screens.MainScreen
import fr.ailurus.vaultfamily.ui.theme.VaultFamilyTheme
import fr.ailurus.vaultfamily.ui.viewmodel.VaultViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    val DEBUG = false

    private val vaultRepository by lazy {
        VaultRepositoryImpl(applicationContext)
    }

    private val vaultViewModel: VaultViewModel by viewModels {
        VaultViewModel.Factory(vaultRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VaultFamilyTheme {
                MainScreen(viewModel = vaultViewModel)
            }
        }

        // 2. INIT LOURDE ENSUITE : Sur un thread d'arrière-plan (IO)
        lifecycleScope.launch(Dispatchers.IO) {

            if (DEBUG) {
                deleteDatabase("secure-vaultfamily-db")
            }

            // Calculs CPU
            val passwordChars = "password-personnel".toCharArray()
            val passphrase = String(passwordChars).toByteArray(Charsets.UTF_8)

            // Chargement Lib
            System.loadLibrary("sqlcipher")

            // Ouverture DB (Lent - PBKDF2)
            vaultRepository.initializeDb(passphrase)

            // 3. DEBUG DATA : On insère uniquement une fois la DB prête
            // Doit être fait ici pour garantir l'ordre séquentiel
            if (DEBUG) {
                insertDebugData()
            }
        }
    }

    private fun insertDebugData() {
        vaultViewModel.saveGroup(
            Group(groupName = "famille"), secret = GroupSecret(
                groupKey = "secret-famille".toByteArray()
            )
        )
        vaultViewModel.saveGroup(
            Group(groupName = "travail"), secret = GroupSecret(
                groupKey = "secret-travail".toByteArray()
            )
        )

        vaultViewModel.saveEntry(
            entry = Entry(
                entrySite = "google.com",
                entryUser = "franck",
                groupId = 1
            ),
            secret = EntrySecret(
                encryptedPassword = "password-google".toByteArray()
            )
        )

        vaultViewModel.saveEntry(
            entry = Entry(
                entrySite = "amazon.fr",
                entryUser = "ailurus",
                groupId = 2
            ),
            secret = EntrySecret(
                encryptedPassword = "password-amazon".toByteArray()
            )
        )
    }
}