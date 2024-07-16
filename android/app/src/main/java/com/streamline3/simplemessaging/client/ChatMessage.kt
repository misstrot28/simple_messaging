package com.streamline3.simplemessaging.client

/**
 * Contains ChatMessage object with important properties like message, user and date.
 *
 * @author Nirson Samson
 * @date 07.16.2024
 */

class ChatMessage(
    val message: String?,
    val user: String,
    val command: String?,
    val createdDateTime: String
)