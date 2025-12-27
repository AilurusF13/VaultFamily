package fr.ailurus.vaultfamily

import fr.ailurus.vaultfamily.data.repository.VaultRepositoryImpl
import fr.ailurus.vaultfamily.data.model.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import fr.ailurus.vaultfamily.ui.screens.MainScreen
import fr.ailurus.vaultfamily.ui.theme.VaultFamilyTheme
import fr.ailurus.vaultfamily.ui.viewmodel.VaultViewModel
import net.sqlcipher.database.SQLiteDatabase

class MainActivity : ComponentActivity() {

    // 1. CORRECTION : Initialisez le VRAI repository
    private val vaultRepository by lazy {
        VaultRepositoryImpl(applicationContext)
    }

    private val vaultViewModel: VaultViewModel by viewModels {
        VaultViewModel.Factory(vaultRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Déverrouillage de la base de données (c'est le moment !)
        // La passphrase doit être un tableau d'octets.
        val passphrase = SQLiteDatabase.getBytes("votre-mot-de-passe-super-secret".toCharArray())
        vaultRepository.initializeDb(passphrase)

        enableEdgeToEdge()
        setContent {
            VaultFamilyTheme {
                MainScreen(viewModel = vaultViewModel)
            }
        }

        // --- SECTION DE TEST : Insérer des données au démarrage ---
        // (À commenter ou supprimer une fois que l'app est fonctionnelle)

        // 2. CORRECTION : Comment appeler saveGroup
        // On fournit le nom et la clé secrète sous forme de ByteArray
        vaultViewModel.saveGroup(Group(groupName = "famille"), secret = GroupSecret(
            groupKey = "secret-famille".toByteArray()
        ))
        vaultViewModel.saveGroup(Group(groupName = "travail"), secret = GroupSecret(
            groupKey = "secret-travail".toByteArray()
        ))

        // 3. CORRECTION : Comment appeler saveEntry
        vaultViewModel.saveEntry(
            // L'ID du groupe doit correspondre à un groupe existant.
            entry = Entry(
                entrySite = "google.com",
                entryUser = "franck",
                groupId = 0 // IMPORTANT: L'ID du groupe "self" (inséré lors de la création de la db)
            ),
            // Le secret doit être un objet EntrySecret
            secret = EntrySecret(
                encryptedPassword = "password-google".toByteArray()
            )
        )

        vaultViewModel.saveEntry(
            entry = Entry(
                entrySite = "amazon.fr",
                entryUser = "ailurus",
                groupId = 1 // IMPORTANT: L'ID du groupe "famille"
            ),
            secret = EntrySecret(
                encryptedPassword = "password-amazon".toByteArray()
            )
        )
    }
}
