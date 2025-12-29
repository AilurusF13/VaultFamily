package fr.ailurus.vaultfamily.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import fr.ailurus.vaultfamily.ui.viewmodel.AuthViewModel

@Composable
fun AuthScreen(viewModel: AuthViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Champ Mot de Passe
        OutlinedTextField(
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("Mot de passe") },
            visualTransformation = PasswordVisualTransformation(), // Masque les caractères
            isError = uiState.error.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        )

        if (uiState.firstAuth) {
            Spacer(modifier = Modifier.height(8.dp))
            // Champ Confirmation
            OutlinedTextField(
                value = uiState.confirm,
                onValueChange = { viewModel.onConfirmChange(it) },
                label = { Text("Confirmer le mot de passe") },
                visualTransformation = PasswordVisualTransformation(),
                isError = uiState.error.contains("correspondent pas"),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Zone d'erreur dédiée
        if (uiState.error.isNotEmpty()) {
            Text(
                text = uiState.error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bouton de validation
        Button(
            onClick = {
                if (uiState.firstAuth) viewModel.trySetup() else viewModel.tryLogin()
            },
            enabled = uiState.password.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
                Text(if (uiState.firstAuth) "Initialiser le coffre" else "Déverrouiller")
        }
    }
}