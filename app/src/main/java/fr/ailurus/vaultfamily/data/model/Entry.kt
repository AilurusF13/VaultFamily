package fr.ailurus.vaultfamily.data.model


data class Entry(
    val id : Long = 0,
    val siteWeb : String = "",
    val identifiant : String = "",
    val group : String = "self",
    val password : String = ""
)
