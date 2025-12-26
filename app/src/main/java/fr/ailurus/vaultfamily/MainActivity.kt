
package fr.ailurus.vaultfamily

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
    }
}

val vaultRepository = FakeVaultRepository();