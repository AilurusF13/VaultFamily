package fr.ailurus.vaultfamily

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import fr.ailurus.vaultfamily.data.repository.VaultRepositoryImpl
import fr.ailurus.vaultfamily.domain.auth.AuthManagerImpl
import fr.ailurus.vaultfamily.ui.screens.AuthScreen
import fr.ailurus.vaultfamily.ui.theme.VaultFamilyTheme
import fr.ailurus.vaultfamily.ui.viewmodel.AuthViewModel
import fr.ailurus.vaultfamily.ui.viewmodel.VaultViewModel

class MainActivity : ComponentActivity() {

    private val vaultRepository by lazy {
        VaultRepositoryImpl()
    }

    private val vaultViewModel: VaultViewModel by viewModels {
        VaultViewModel.Factory(vaultRepository)
    }

    private val authManager by lazy {
        AuthManagerImpl(applicationContext,vaultRepository)
    }

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModel.Factory(authManager)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VaultFamilyTheme {
                AuthScreen(authViewModel)
            }
        }
//        authViewModel.deleteDb()
    }
}