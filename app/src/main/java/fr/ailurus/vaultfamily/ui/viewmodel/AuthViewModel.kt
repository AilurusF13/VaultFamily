package fr.ailurus.vaultfamily.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import fr.ailurus.vaultfamily.data.model.uistate.AuthUiState
import fr.ailurus.vaultfamily.domain.auth.AuthManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(private val authManager: AuthManager) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AuthUiState(firstAuth = !authManager.isVaultInitalized())
    )

    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onPasswordChange(newPassword: String){
        _uiState.update { it.copy(password = newPassword) }
    }

    fun onConfirmChange(newConfirm: String){
        _uiState.update { it.copy(confirm = newConfirm) }
        if (_uiState.value.password != newConfirm){
            errorConfirm()
        }
    }

    fun errorConfirm(){
        _uiState.update { it.copy(
            error = "Les mots de passe ne correspondent pas"
        )}
    }

    fun isErrorEmpty(): Boolean{
        return _uiState.value.error.isEmpty()
    }

    private var _authJob: Job? = null // empeche de faire l action plusieurs fois avant la fin de la premeire

    private fun tryOp(operation: suspend (ByteArray) -> Result<Unit>) {

        if (_authJob?.isActive == true) return

        val passwordBytes = _uiState.value.password.toByteArray()

        _authJob = viewModelScope.launch {
            operation(passwordBytes)
                .onSuccess {
                    authManager.accessVault()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message ?: "Erreur inconnue") }
                }
        }
    }

    fun tryLogin() = tryOp { authManager.loginVault(it) }
    fun trySetup() = tryOp { authManager.setupVault(it) }

    companion object {
        fun Factory(authManager: AuthManager): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AuthViewModel(authManager = authManager)
            }
        }
    }
}