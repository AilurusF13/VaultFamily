
package fr.ailurus.vaultfamily

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import fr.ailurus.vaultfamily.data.model.Entry
import fr.ailurus.vaultfamily.data.repository.FakeVaultRepository
import fr.ailurus.vaultfamily.ui.screens.MainScreen
import fr.ailurus.vaultfamily.ui.theme.VaultFamilyTheme
import fr.ailurus.vaultfamily.ui.viewmodel.VaultViewModel

class MainActivity : ComponentActivity() {
    private val vaultViewModel: VaultViewModel by viewModels{
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

        vaultViewModel.addGroup("famille")
        vaultViewModel.addGroup("aaaa")
        vaultViewModel.addGroup("bbbb")
        vaultViewModel.addGroup("ccccc")
        vaultViewModel.addGroup("dddd")
        vaultViewModel.addGroup("eeee")

        vaultViewModel.saveEntry(
            Entry(
                siteWeb = "google.com",
                identifiant = "franck",
                group = "self",
            )
        )
        vaultViewModel.saveEntry(Entry(
            siteWeb = "amazon.fr",
            identifiant = "ailurus",
            group = "famille",
        ))
    }
}

val vaultRepository = FakeVaultRepository();