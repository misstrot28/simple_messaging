package com.streamline3.simplemessaging.server

import com.streamline3.simplemessaging.client.ChatMessage

/**
 * Each class that implements observer calls newMessage method
 * when Observable method notifyObservers is called.
 *
 * @author Nirson Samson
 * @date 07.16.2024
 */

interface Observer {

    fun newMessage(message: ChatMessage)
}