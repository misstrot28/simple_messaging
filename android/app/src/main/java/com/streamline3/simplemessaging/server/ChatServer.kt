package com.streamline3.simplemessaging.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.ServerSocket
import java.net.Socket

/**
 * Creates ServerSocket and instantiates ChatConsole and TopChatter
 * to be registered as observers.
 * Each time a client connects - a new ChatConnector object is instantiated.
 *
 * @author Nirson Samson
 * @date 07.16.2024
 */

open class ChatServer {
    var _client: Socket? = null

    open fun server() {
        CoroutineScope(Dispatchers.IO).launch {
            ChatConsole
            TopChatter
            try {
                val server = ServerSocket(9999, 2)

                while (true) {
                    val client = server.accept()
                    _client = client

                    ChatConnector(client).run()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                println("Client connected " + _client?.inetAddress?.hostAddress + " " + _client?.port)
            }
        }
    }
}