package fr.ailurus.vaultfamily.data.network

import kotlinx.coroutines.flow.StateFlow

interface DiscoveryManager {

    /**
     * État de la découverte (en cours ou arrêté).
     */
    val isScanning: Boolean

    /**
     * Un flux (Flow) émettant la liste mise à jour des adresses IP détectées.
     * On utilise un Set pour éviter les doublons d'instances.
     */
    val discoveredDevices: StateFlow<Set<String>>

    /**
     * Démarre simultanément la publication de l'instance locale (Beacon)
     * et l'écoute des autres instances sur le réseau.
     * * @param serviceName Le nom de l'instance pour l'identifier sur le réseau.
     * @param port Le port sur lequel l'application écoute les connexions entrantes.
     */
    fun startDiscovery(serviceName: String, port: Int)

    /**
     * Arrête toute activité réseau, libère les sockets et les verrous (MulticastLock).
     */
    fun stopDiscovery()

    /**
     * Envoie manuellement une requête de "Ping" ou de présence sur le réseau.
     * Utile si on veut forcer un rafraîchissement sans attendre le prochain cycle du Beacon.
     */
    fun broadcastPresence()

    /**
     * Nettoie la liste des appareils qui n'ont pas donné de signe de vie
     * depuis un certain délai (gestion du Time-to-Live).
     */
    fun cleanupInactiveDevices(timeoutMillis: Long = 30000)
}