package fr.ailurus.vaultfamily.domain.network

import fr.ailurus.vaultfamily.data.network.DiscoveryManager
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.ServerSocket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.copyTo
import io.ktor.utils.io.readFully
import io.ktor.utils.io.writeFully
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NetworkManager {

    private val selectorManager = SelectorManager(Dispatchers.IO)
    private var serverSocket: ServerSocket? = null

    suspend fun startServer(port: Int, onMessageReceived: (ByteArray) -> Unit){
        withContext(Dispatchers.IO) {
            serverSocket = aSocket(selectorManager).tcp().bind("0.0.0.0", port)

            while (true){
                val socket = serverSocket?.accept() ?: break;
                val receiveChannel = socket.openReadChannel()

                try {
                    val size = receiveChannel.readInt()
                    val packet = ByteArray(size)
                    receiveChannel.readFully(packet)

                    withContext(Dispatchers.Main){
                        onMessageReceived(packet)
                    }
                } finally {
                    socket.close()
                }
            }
        }
    }

    suspend fun sendPacket(ip: String, port: Int, encryptedData: ByteArray){
        withContext(Dispatchers.IO){
            val socket = aSocket(selectorManager).tcp().connect(InetSocketAddress(ip, port))
            val sendChannel = socket.openWriteChannel(autoFlush = true)

            try {
                sendChannel.writeInt(encryptedData.size)
                sendChannel.writeFully(encryptedData)
            }finally {
                socket.close()
            }

        }
    }

    fun stopServer(){
        serverSocket?.close()
        selectorManager.close()
    }
}