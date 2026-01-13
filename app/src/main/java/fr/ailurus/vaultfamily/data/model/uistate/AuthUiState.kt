package fr.ailurus.vaultfamily.data.model.uistate

data class AuthUiState(
    val firstAuth: Boolean,
    val password: String = "",
    val confirm: String = "",
    val error: String = ""
)