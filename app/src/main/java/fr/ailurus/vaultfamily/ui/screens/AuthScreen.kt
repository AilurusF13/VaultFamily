package fr.ailurus.vaultfamily.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.ailurus.vaultfamily.domain.auth.AuthManager
import fr.ailurus.vaultfamily.ui.theme.VaultFamilyTheme
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    authManager: AuthManager,
    onAuthSuccess: () -> Unit
) {
    val isFirstRun = remember { !authManager.isVaultInitialized() }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(if (isFirstRun) "Initialisation" else "Connexion")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe") },
            modifier = Modifier.fillMaxWidth()
        )

        if (isFirstRun) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmer le mot de passe") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    errorText = null
                    val result = if (isFirstRun) {
                        if (password == confirmPassword && password.isNotEmpty()) {
                            authManager.setupVault(password.toByteArray())
                        } else {
                            Result.failure(Exception("Erreur confirmation"))
                        }
                    } else {
                        authManager.unlockVault(password.toByteArray())
                    }

                    if (result.isSuccess) {
                        onAuthSuccess()
                    } else {
                        errorText = result.exceptionOrNull()?.message ?: "Erreur"
                    }
                }
            },
            enabled = password.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isFirstRun) "Créer" else "Ouvrir")
        }

        errorText?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = Color.Red)
        }
    }
}

class FakeAuthManager(private val initialized: Boolean) : AuthManager {
    override fun isVaultInitialized(): Boolean = initialized

    // On simule une réussite immédiate pour la preview
    override suspend fun setupVault(passphrase: ByteArray): Result<Unit> = Result.success(Unit)
    override suspend fun unlockVault(passphrase: ByteArray): Result<Unit> = Result.success(Unit)
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreviewFirst() {
    VaultFamilyTheme {
        // Simulation d'une première installation
        AuthScreen(
            authManager = FakeAuthManager(initialized = false),
            onAuthSuccess = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreviewOther() {
    VaultFamilyTheme {
        // Simulation d'un déverrouillage classique
        AuthScreen(
            authManager = FakeAuthManager(initialized = true),
            onAuthSuccess = {}
        )
    }
}