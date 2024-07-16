package com.streamline3.simplemessaging.server

import com.streamline3.simplemessaging.client.ChatMessage

/**
 * ChatConsole registers as observer and prints to server console client input.
 *
 * @author Nirson Samson
 * @date 07.16.2024
 */

object ChatConsole : Observer {

    init {
        ChatHistory.registerObserver(this)
    }

    override fun newMessage(message: ChatMessage) {
        println("${message.user}: ${message.message} || ${message.createdDateTime}")
    }
}