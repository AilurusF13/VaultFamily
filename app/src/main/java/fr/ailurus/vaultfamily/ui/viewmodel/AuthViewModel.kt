package fr.ailurus.vaultfamily.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import fr.ailurus.vaultfamily.data.model.uistate.AuthUiState
import fr.ailurus.vaultfamily.data.repository.AppDatabase
import fr.ailurus.vaultfamily.domain.auth.AuthManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(private val authManager: AuthManager) : ViewModel() {

    // Tes inputs bruts
    private val _password = MutableStateFlow("")
    private val _confirm = MutableStateFlow("")
    private val _asyncError = MutableStateFlow<String?>(null)

    // La réactivité pure avec combine
    val uiState: StateFlow<AuthUiState> = combine(_password, _confirm, _asyncError) { p, c, err ->
        val validationError = when {
            c.isNotEmpty() && p != c -> "Les mots de passe ne correspondent pas"
            else -> err
        }

        AuthUiState(
            password = p,
            confirm = c,
            error = validationError ?: "",
            firstAuth = !authManager.isVaultInitalized()
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, AuthUiState(
        firstAuth = authManager.isVaultInitalized()
    ))

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        _asyncError.value = null // Reset l'erreur de l'AuthManager quand on tape
    }

    fun onConfirmChange(newConfirm: String) {
        _confirm.value = newConfirm
    }

    private var _authJob: Job? = null // empeche de faire l action plusieurs fois avant la fin de la premeire

    private fun tryOp(operation: suspend (ByteArray) -> Result<Unit>): Boolean {

        if (_authJob?.isActive == true) return false

        val passwordBytes = uiState.value.password.toByteArray()

        var res = false

        _authJob = viewModelScope.launch {

            AppDatabase.clearInstance()
            operation(passwordBytes)
                .onSuccess {
                    res = true
                }
                .onFailure { e ->
                    _asyncError.update { "Mot de passe erroné" }
                    // TODO en attendant un fix e.message ou mot de passe éroné
                    res = false
                }
        }
        return res
    }

    fun tryLogin(): Boolean = tryOp { authManager.loginVault(it) }
    fun trySetup(): Boolean = tryOp { authManager.setupVault(it) }

    fun deleteDb(){
        authManager.deleteDb()
    }

    companion object {
        fun Factory(authManager: AuthManager): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AuthViewModel(authManager = authManager)
            }
        }
    }
}