package com.example.firy.model

import java.util.*

data class TextMessage(
    val text: String,
    override val time: Date,
    override val senderId: String,
    override val type: String = MessageType.TEXT,
    override val recipient: String,
    override val senderName: String
) : Message{
    constructor() : this("", Date(0), "","","","")
}