package com.example.firy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firy.model.MessageType
import com.example.firy.model.TextMessage
import com.example.firy.util.FirestoreUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.fragment_conversation.*
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var messageListenerRegistration: ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var messagesSection: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra(AppConstants.USER_NAME)

        val otherUserId = intent.getStringExtra(AppConstants.USER_ID)

        FirestoreUtil.getOrCreateChatChannel(otherUserId!!){channelId ->
            messageListenerRegistration = FirestoreUtil.addChatMessagesListener(channelId, this, this::updateRecyclerView)
            sendMessageButton.setOnClickListener {
                val messageToSend = TextMessage(editTextMessage.text.toString(), Calendar.getInstance().time,
                FirebaseAuth.getInstance().currentUser!!.uid, MessageType.TEXT)
                editTextMessage.setText(" ")
                FirestoreUtil.sendMessage(messageToSend, channelId)
            }
           //TODO: Send Image messages
//            fabImageButton.setOnLongClickListener {
//
//            }
        }
    }

    private fun updateRecyclerView(messages : List<Item>){
        fun init(){
            chatChannelRecyclerView.apply {
                adapter = GroupAdapter<GroupieViewHolder>().apply {
                    messagesSection = Section(messages)
                    this.add(messagesSection)
                }
                layoutManager = LinearLayoutManager(this@ChatActivity)
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = messagesSection.update(messages)

        if(shouldInitRecyclerView) init()
        else updateItems()

        chatChannelRecyclerView.scrollToPosition(chatChannelRecyclerView.adapter!!.itemCount -1)
    }
}