package com.streamline3.simplemessaging.server

import com.streamline3.simplemessaging.client.ChatMessage

/**
 * Used for classes that want to execute the same method for each registered
 * Observer.
 *
 * @author Nirson Samson
 * @date 07.16.2024
 */

interface Observable {
    fun registerObserver(observer: Observer)
    fun deregisterObserver(observer: Observer)
    fun notifyObservers(message: ChatMessage)
}