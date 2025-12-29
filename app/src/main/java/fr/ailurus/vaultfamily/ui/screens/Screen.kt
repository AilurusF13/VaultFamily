package fr.ailurus.vaultfamily.ui.screens

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Vault : Screen("vault")
}