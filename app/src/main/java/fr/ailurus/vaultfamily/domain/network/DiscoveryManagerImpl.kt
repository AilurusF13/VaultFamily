package fr.ailurus.vaultfamily.domain.network

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import fr.ailurus.vaultfamily.data.network.DiscoveryManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DiscoveryManagerImpl(context: Context): DiscoveryManager {

    private val tag = "Discovery Manager"

    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    private var registrationListener: NsdManager.RegistrationListener? = null

    private val serviceType = "_vaultfamily._udp"
    // Propriétés stockées lors du startDiscovery
    private var currentServiceName: String? = null
    private var currentPort: Int? = null

    override var isScanning: Boolean = false
        private set

    private var discoveryListener: NsdManager.DiscoveryListener? = null

    private val devicesTimestamp = mutableMapOf<String, Long>()
    private val _discoveredDevices = MutableStateFlow<Set<String>>(emptySet())
    override val discoveredDevices = _discoveredDevices.asStateFlow()

    override fun startDiscovery(serviceName: String, port: Int) {
        Log.d(tag, "Démarrage du service: $serviceName sur le port $port")

        this.currentServiceName = serviceName
        this.currentPort = port
        this.isScanning = true

        broadcastPresence()

        // préparer l'écoute

        if (discoveryListener != null){
            Log.d(tag, "Le scanner est déjà actif")
            return
        }

        discoveryListener = object : NsdManager.DiscoveryListener {

            override fun onStartDiscoveryFailed(p0: String?, p1: Int) {}
            override fun onStopDiscoveryFailed(p0: String?, p1: Int) {}
            override fun onDiscoveryStarted(p0: String?) {}
            override fun onDiscoveryStopped(p0: String?) {}
            override fun onServiceLost(p0: NsdServiceInfo?) {}

            override fun onServiceFound(info: NsdServiceInfo) {
                Log.d(tag, "Service trouvé: ${info.serviceName}")

                if (info.serviceType.contains(serviceType)
                    && info.serviceName != serviceName){

                    // 2 alternatives possibles pour les vieux téléphones

                    nsdManager.registerServiceInfoCallback(
                        info,
                        {it.run()},
                        object : NsdManager.ServiceInfoCallback {

                            override fun onServiceUpdated(info: NsdServiceInfo) {
                                val adress = info.hostAddresses.firstOrNull {it is java.net.Inet4Address}
                                adress?.hostAddress.let { ip ->
                                    if (ip != null){
                                        Log.d("DiscoveryManager", "IP résolue: $ip")
                                        devicesTimestamp[ip] = System.currentTimeMillis()
                                        _discoveredDevices.value = devicesTimestamp.keys.toSet()

                                    }
                                }
                            }
                            override fun onServiceInfoCallbackRegistrationFailed(error: Int) {
                                Log.e(tag, "Callback registration failed code($error)")
                            }
                            // on s en fou : on nettoiiera plus tard la liste
                            override fun onServiceLost() {}
                            override fun onServiceInfoCallbackUnregistered(){}
                    })
                }
            }
        }
        nsdManager.discoverServices(
            serviceType,
            NsdManager.PROTOCOL_DNS_SD,
            discoveryListener
        )
    }

    override fun stopDiscovery() {
        isScanning = false
        registrationListener?.let {nsdManager.unregisterService(it)}
        discoveryListener?.let {nsdManager.stopServiceDiscovery(it)}

        registrationListener = null
        discoveryListener = null
        _discoveredDevices.value = emptySet()
    }
    override fun broadcastPresence() {

        val name = currentServiceName
        val type = "_vaultfamily._udp" // Type fixe, commence par _
        val port = currentPort ?: 1331

        if (name.isNullOrBlank() || name.startsWith("_")) {
            // Force un nom valide si le transfert a échoué
            Log.e(tag, "NOM INVALIDE : '$name'. Utilisation d'un nom par défaut.")
        }

        val serviceInfo = NsdServiceInfo().apply {
            // ICI : On assigne bien le nom au nom, et le type au type
            this.serviceName = name ?: "VaultDevice"
            this.serviceType = type
            this.port = port
        }

        Log.d(tag, "VÉRIFICATION : Name=${serviceInfo.serviceName}, Type=${serviceInfo.serviceType}")

        registrationListener = object : NsdManager.RegistrationListener {

            override fun onServiceRegistered(info: NsdServiceInfo) {
                currentServiceName = info.serviceName
            }

            override fun onRegistrationFailed(info: NsdServiceInfo, error: Int) {
                registrationListener = null
                Log.e(tag, "Registration failed code($error)")
            }

            override fun onServiceUnregistered(info: NsdServiceInfo) {
                registrationListener = null
            }

            override fun onUnregistrationFailed(info: NsdServiceInfo, error: Int) {
                registrationListener = null
                Log.e(tag, "Unregistration failed code($error)")
            }

        }
        try {
            nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)

        } catch (e: Exception){
            Log.e(tag , "Erreur register service : ${e.message}")
        }

    }
    override fun cleanupInactiveDevices(timeoutMillis: Long) {
        val currentTime = System.currentTimeMillis()
        val initSize = devicesTimestamp.size

        val iterator = devicesTimestamp.entries.iterator()
        while (iterator.hasNext()){
            val entry = iterator.next()
            if (currentTime - entry.value > timeoutMillis){
                iterator.remove()
            }
        }

        if (devicesTimestamp.size != initSize){
            _discoveredDevices.value = devicesTimestamp.keys.toSet()
            Log.d(tag, "Cleanup effectué. Appareils restants : ${devicesTimestamp.size}")
        }
    }
}