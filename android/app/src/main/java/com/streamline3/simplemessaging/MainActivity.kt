package com.streamline3.simplemessaging


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButton.OnCheckedChangeListener
import com.google.gson.Gson
import com.streamline3.simplemessaging.client.ChatMessage
import com.streamline3.simplemessaging.client.ChatMessageAdapter
import com.streamline3.simplemessaging.databinding.ActivityMainBinding
import com.streamline3.simplemessaging.server.ChatServer
import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset
import java.util.Scanner

/**
 * Connects all classes together
 *
 * @author Nirson Samson
 * @date 07.16.2024
 * */

open class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding

    private lateinit var serverConnect: Thread
    lateinit var serverFeed: Thread

    internal var address = ""
        private set
    internal var port = 9999
        private set
    lateinit var socket: Socket
    lateinit var writer: OutputStream
    lateinit var reader: Scanner
    private lateinit var adapter: ChatMessageAdapter
    private lateinit var chatMessageArrayList: ArrayList<ChatMessage>

    private var connected = false

    override fun onClick(v: View) {
        if (v.id == R.id.backgroundLayout || v.id == R.id.recyclerView) {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        binding.apply {
            supportActionBar?.hide()
            title = ""
            hideChatLayout()

            switchServer.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    ChatServer().server()
                }
            }

            backgroundLayout.setOnClickListener(this@MainActivity)
            recyclerView.setOnClickListener(this@MainActivity)

            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            chatMessageArrayList = ArrayList()
            adapter = ChatMessageAdapter(this@MainActivity, chatMessageArrayList)
            recyclerView.adapter = adapter

            connectButton.setOnClickListener {
                connected = !connected
                if (connected) {
                    connectToServer()
                } else {
                    disconnectFromServer()
                }
            }

            buttonChatboxSend.setOnClickListener {
                val message = edittextChatbox.text.toString().trim()
                if (message.isNotEmpty()) {
                    Thread(ServerWrite(message)).start()
                    edittextChatbox.setText("")
                }
            }

            tapCommandButton.setOnClickListener {
                val popupMenu = PopupMenu(this@MainActivity, it)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.list_users -> {
                            Thread(ServerWrite("LIST USERS")).start()
                            edittextChatbox.setText("")
                            true
                        }

                        R.id.list_history -> {
                            Thread(ServerWrite("LIST HISTORY")).start()
                            edittextChatbox.setText("")
                            true
                        }

                        R.id.list_top -> {
                            Thread(ServerWrite("LIST TOP")).start()
                            edittextChatbox.setText("")
                            true
                        }

                        else -> false
                    }
                }

                popupMenu.inflate(R.menu.command_popup_menu)
                popupMenu.show()
            }
        }
    }

    private fun hideChatLayout() = with(binding) {
        edittextChatbox.visibility = View.GONE
        buttonChatboxSend.visibility = View.GONE
        tapCommandButton.visibility = View.INVISIBLE
        recyclerView.visibility = View.GONE
        buttonChatboxSend.visibility = View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    fun shutdown() {
        socket.close()
        writer.close()
        reader.close()
        serverConnect.interrupt()
        serverFeed.interrupt()
        chatMessageArrayList.clear()
        adapter.notifyDataSetChanged()
    }

    fun connectToServerUI(context: Activity) {
        context.runOnUiThread {
            binding.progressBar.visibility = View.VISIBLE
            context.window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }
    }

    fun disconnectFromServer(context: Activity) {
        context.runOnUiThread {
            binding.connectButton.text = "DISCONNECT"
            binding.tvMessage.text = "Connected"
            connected = true

            // Connection screen
            binding.connectToServerLayout.visibility = View.GONE

            // Chat room
            binding.recyclerView.visibility = View.VISIBLE
            binding.tapCommandButton.visibility = View.VISIBLE
            binding.buttonChatboxSend.visibility = View.VISIBLE
            binding.edittextChatbox.visibility = View.VISIBLE
        }
    }

    fun connectionError(context: Activity) {
        context.runOnUiThread {
            binding.progressBar.visibility = View.GONE
            context.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            connected = false
            // Initialize a new instance of
            AlertDialog.Builder(context)
                .setTitle("Connection error")
                .setMessage("Could not connect to server. Have you got the right IP address?")
                .setPositiveButton("Try again") { _, _ ->
                    Thread(ServerConnect(context)).start()
                }
                .setNegativeButton("OK", null)
                .show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun serverFeed(context: Activity, message: String) {
        context.runOnUiThread {
            binding.progressBar.visibility = View.GONE
            context.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            chatMessageArrayList.add(Gson().fromJson(message, ChatMessage::class.java))

            adapter.notifyDataSetChanged()

            binding.recyclerView.layoutManager!!.scrollToPosition(
                chatMessageArrayList.size - 1
            )
        }
    }

    private fun connectToServer() {
        address = binding.ipAddressEditText.text.toString()
        port = Integer.parseInt(binding.portEditText.text.toString())

        serverConnect = Thread(ServerConnect(this))
        serverConnect.start()
    }

    private fun disconnectFromServer() {
        binding.connectButton.text = "CONNECT"
        binding.tvMessage.text = "Disconnected"
        shutdown()

        // Connection screen enabled
        binding.connectToServerLayout.visibility = View.VISIBLE

        // Chat room UI elements disabled
        hideChatLayout()

        Log.i("SERVER", "INTERRUPTED")
    }

    inner class ServerConnect(private var context: Context) : Runnable {

        override fun run() {
            val newContext = context as Activity

            connectToServerUI(newContext)

            try {
                socket = Socket(address, port)
                writer = socket.getOutputStream()
                reader = Scanner(socket.getInputStream())

                serverFeed = Thread(ServerFeed(newContext))
                serverFeed.start()

                disconnectFromServer(newContext)

            } catch (e: Exception) {
                // Cannot find server
                connectionError(newContext)
                e.printStackTrace()
            }
        }
    }

    inner class ServerFeed(private var context: Activity) : Runnable {
        override fun run() {

            while (true) {
                try {
                    if (serverFeed.isInterrupted)
                    // We've been interrupted: no more crunching.
                        break

                    val message: String = reader.nextLine()
                    Log.i("MESSAGE", "MESSAGE $message")

                    if (serverFeed.isInterrupted) {
                        socket.close()
                        reader.close()
                        writer.close()
                    }

                    serverFeed(context, message)

                } catch (e: Exception) {
                    shutdown()
                    e.printStackTrace()
                }
            }
        }
    }

    inner class ServerWrite(private var message: String) : Runnable {
        override fun run() {
            write(message)
            writer.flush()
        }

        private fun write(message: String) {
            val messageAsJson =
                Gson().toJson(ChatMessage(message, "userName", message, "createdDateAtTime"))
            writer.write((messageAsJson + '\n').toByteArray(Charset.defaultCharset()))
        }
    }
}
