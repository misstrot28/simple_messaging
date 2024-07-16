package com.streamline3.simplemessaging.client

import androidx.recyclerview.widget.RecyclerView
import com.streamline3.simplemessaging.databinding.ItemRowBinding

/**
 * Sets each item_row.xml with either server side or client side information like message,
 * date and username.
 *
 * @author Nirson Samson
 * @date 07.16.2024
 */

class ChatMessagesHolder(
    private val binding: ItemRowBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun setDetails(chatMessage: ChatMessage) {
        binding.txtUserName.text = chatMessage.user
        binding.txtMessage.text = chatMessage.message
        binding.txtDate.text = chatMessage.createdDateTime
    }
}