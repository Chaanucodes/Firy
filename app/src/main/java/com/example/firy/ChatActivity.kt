package com.example.firy

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firy.model.ImageMessage
import com.example.firy.model.MessageType
import com.example.firy.model.TextMessage
import com.example.firy.model.User
import com.example.firy.util.FirestoreUtil
import com.example.firy.util.StorageUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.fragment_conversation.*
import java.io.ByteArrayOutputStream
import java.util.*

const val CHAT_INTENT_FOR_IMAGE = 2

class ChatActivity : AppCompatActivity() {

    private lateinit var currentChannelId : String
    private lateinit var currentUser : User
    private lateinit var otherUserId : String

    private lateinit var messageListenerRegistration: ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var messagesSection: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra(AppConstants.USER_NAME)

        FirestoreUtil.getCurrentUser {
            currentUser = it!!
        }

        otherUserId = intent.getStringExtra(AppConstants.USER_ID)!!

        FirestoreUtil.getOrCreateChatChannel(otherUserId!!){channelId ->
            currentChannelId = channelId
            messageListenerRegistration = FirestoreUtil.addChatMessagesListener(channelId, this, this::updateRecyclerView)
            sendMessageButton.setOnClickListener {
                val messageToSend = TextMessage(editTextMessage.text.toString(), Calendar.getInstance().time,
                FirebaseAuth.getInstance().currentUser!!.uid, MessageType.TEXT, otherUserId, currentUser.name)
                editTextMessage.setText(" ")
                FirestoreUtil.sendMessage(messageToSend, channelId)
            }

            fabImageButton.setOnClickListener {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                }
                startActivityForResult(Intent.createChooser(intent, "Select Image"), CHAT_INTENT_FOR_IMAGE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CHAT_INTENT_FOR_IMAGE && resultCode == Activity.RESULT_OK && data!= null && data.data != null){
            val selectedImagePath = data.data

            val selectedImageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImagePath)

            val outputStream = ByteArrayOutputStream()

            selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val selectedImageBytes = outputStream.toByteArray()

            StorageUtil.uploadMessageImage(selectedImageBytes){
                val messageToSend = ImageMessage(it , Calendar.getInstance().time, FirebaseAuth.getInstance().currentUser!!.uid, MessageType.IMAGE,
                    otherUserId, currentUser.name)
                FirestoreUtil.sendMessage(messageToSend, currentChannelId )
            }
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