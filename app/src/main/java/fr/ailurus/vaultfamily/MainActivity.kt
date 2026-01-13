package fr.ailurus.vaultfamily

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.ailurus.vaultfamily.data.repository.VaultRepositoryImpl
import fr.ailurus.vaultfamily.domain.auth.AuthManagerImpl
import fr.ailurus.vaultfamily.ui.screens.*
import fr.ailurus.vaultfamily.ui.theme.VaultFamilyTheme
import fr.ailurus.vaultfamily.ui.viewmodel.AuthViewModel
import fr.ailurus.vaultfamily.ui.viewmodel.VaultViewModel


class MainActivity : ComponentActivity() {
    // Garde uniquement ce qui est nécessaire au démarrage (Auth)
    private val vaultRepository by lazy { VaultRepositoryImpl() }
    private val authManager by lazy { AuthManagerImpl(applicationContext, vaultRepository) }
    private val authViewModel: AuthViewModel by viewModels { AuthViewModel.Factory(authManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VaultFamilyTheme {
                // On ne passe plus le vaultViewModel ici
                AppNavigation(authViewModel, vaultRepository)
            }
        }
    }
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    vaultRepository: fr.ailurus.vaultfamily.data.repository.VaultRepository // On passe le repo
){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Auth.route){

        composable(Screen.Auth.route){
            AuthScreen(
                viewModel = authViewModel,
                onAuthSuccess = {
                    navController.navigate(Screen.Vault.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Vault.route){
            // ON CRÉE LE VIEWMODEL ICI SEULEMENT
            // Il ne sera instancié qu'APRES le succès de l'auth
            val vaultViewModel: VaultViewModel = viewModel(
                factory = VaultViewModel.Factory(vaultRepository)
            )
            VaultScreen(vaultViewModel)
        }
    }
}