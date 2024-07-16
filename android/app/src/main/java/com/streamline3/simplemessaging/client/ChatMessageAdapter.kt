package com.streamline3.simplemessaging.client

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.streamline3.simplemessaging.databinding.ItemRowBinding

/**
 * Adapter for chat messages.
 * RecyclerView recycles each item row.
 *
 * @author Nirson Samson
 * @date 07.16.2024
 */


class ChatMessageAdapter(
    private var context: Context,
    private var chatMessages: ArrayList<ChatMessage>
) :
    RecyclerView.Adapter<ChatMessagesHolder>() {

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessagesHolder {
        val binding = ItemRowBinding.inflate(LayoutInflater.from(context), parent, false)
        return ChatMessagesHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatMessagesHolder, position: Int) {
        val message: ChatMessage = chatMessages[position]
        holder.setDetails(message)
    }
}
